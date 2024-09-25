package CRM.Data.Integration.ServiceImpl;

import CRM.Data.Integration.Model.CommonResponse;
import CRM.Data.Integration.Model.CustomerRecord;
import CRM.Data.Integration.Utility.CalendarUtility;
import CRM.Data.Integration.Utility.CrmRecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@EnableScheduling
public class ServiceImpl implements CRM.Data.Integration.Service.Service {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CrmRecordUtility crmRecordUtility;
    @Autowired
    private CalendarUtility calendarUtility;

    private final Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

    @Scheduled(cron = "0 0 20 * * *")
    public void executeTask() {
        String date = calendarUtility.crmProcessDate(1);
        logger.info("CRM Data invoked by scheduler");
        getCustomerData(String.valueOf(date));
    }

    public ResponseEntity<CommonResponse> getCustomerData(String date) {

        HashMap<String, List<?>> crmData = new HashMap<>();
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<CustomerRecord> crmDataValue = jdbcTemplate.query(crmRecordUtility.getQuery(date), new BeanPropertyRowMapper<>(CustomerRecord.class));
            List<HashMap<String, String>> crmRequest = new ArrayList<>();
            if (!crmDataValue.isEmpty()) {
                for (CustomerRecord fetchData : crmDataValue) {
                    HashMap<String, String> crmRequestData = convertParamsForCrmRequest(fetchData);

                    crmRequest.add(crmRequestData);
                }
                logger.info("Data fetched successfully. Number of records: {}", crmDataValue.size());
                commonResponse.setMsg("Data fetched successfully.");
                crmRecordUtility.generateExcel(crmDataValue);
                crmData.put("records", crmRequest);
                crmRecordUtility.callCrmIntegration(crmData, commonResponse);
                logger.info("API triggered successfully. Timestamp: {}", LocalDateTime.now());

            } else {
                crmRecordUtility.sendMail("Data fetched successfully.");
                commonResponse.setMsg("Data not found : {}");
                logger.info("Data not found for query Triggered on Timestamp: {}", LocalDateTime.now());
            }

            return ResponseEntity.ok(commonResponse);
        } catch (Exception e) {
            commonResponse.setMsg("Technical issue : " + e.getMessage());
            logger.error("Error occurred during data retrieval or CRM integration. Exception: {}", e.getMessage(), e);
            return new ResponseEntity<>(commonResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HashMap<String, String> convertParamsForCrmRequest(CustomerRecord data) {
        HashMap<String, String> data1 = new HashMap<>();
        data1.put("First Name", data.getFirstName());
        data1.put("Application No", data.getApplicationNumber());
        data1.put("Last Name", data.getLastName());
        data1.put("Contact No 1", data.getMobileNumber());
        data1.put("Email ID", "");
        data1.put("Residential Address", data.getResidentialAddress());
        data1.put("City", data.getCity());
        data1.put("Pincode", data.getPinCode());
        data1.put("State", data.getState());
        data1.put("Customer Number", data.getCustomerNumber());
        data1.put("Agreement Number", "");
        data1.put("Branch", data.getBranchName());
        data1.put("Permanent Address", data.getPermanentAddress());
        return data1;
    }
}