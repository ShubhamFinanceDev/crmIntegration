package CRM.Data.Integration.ServiceImpl;

import CRM.Data.Integration.Model.CommonResponse;
import CRM.Data.Integration.Utility.CrmRecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceImpl implements CRM.Data.Integration.Service.Service {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CrmRecordUtility crmRecordUtility;

    private final Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

    public CommonResponse getCustomerData(){


        List<HashMap<String,String>> listOfRecords = new ArrayList<>();
        HashMap<String,List<?>> crmData = new HashMap<>();
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<Map<String, Object>> crmDataValue = jdbcTemplate.queryForList(crmRecordUtility.getQuery());
            if (!crmDataValue.isEmpty()) {
                for (Map<String, Object> record : crmDataValue) {
                    HashMap<String, String> reportData = new HashMap<>();
                    reportData.put("First Name", (String) record.get("First Name"));
                    reportData.put("Last Name", (String) record.get("Last Name"));
                    reportData.put("Mobile Number", (String) record.get("Mobile Number"));
                    reportData.put("Residential Address", (String) record.get("Residential Address"));
                    reportData.put("CITY", (String) record.get("CITY"));
                    reportData.put("STATE", (String) record.get("STATE"));
                    reportData.put("Pin Code", (String) record.get("Pin Code"));
                    reportData.put("Office/Business Address", (String) record.get("Office/Business Address"));
                    reportData.put("Permanent Address", (String) record.get("Permanent Address"));
                    reportData.put("CUSTOMER_NUMBER", (String) record.get("CUSTOMER_NUMBER"));
                    reportData.put("APPLICATION_NUMBER", (String) record.get("APPLICATION_NUMBER"));
                    reportData.put("Loan Account No", (String) record.get("Loan Account No"));
                    reportData.put("Branch Name", (String) record.get("Branch Name"));
                    reportData.put("APPLICATION_RECIEVED_DATE", (String) record.get("APPLICATION_RECIEVED_DATE"));


                    listOfRecords.add(reportData);
                }
            }else {
                commonResponse.setCode("1111");
                commonResponse.setMsg("Data not found : {}");
            }
            crmData.put("records", listOfRecords);
            System.out.println(crmData);
            logger.info("Data fetch by query : {}",crmDataValue.size());
            crmRecordUtility.callCrmIntegration(crmData, commonResponse);
        }catch (Exception e){
            commonResponse.setCode("1111");
            commonResponse.setMsg("Technical issue : " + e.getMessage());
            logger.error("Technical issue :{}", e.getMessage());
        }
        return commonResponse;
    }
}
