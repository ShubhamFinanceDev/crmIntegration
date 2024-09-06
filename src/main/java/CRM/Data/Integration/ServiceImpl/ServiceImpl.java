package CRM.Data.Integration.ServiceImpl;

import CRM.Data.Integration.Model.CommonResponse;
import CRM.Data.Integration.Model.CustomerRecord;
import CRM.Data.Integration.Utility.CrmRecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

//    @Scheduled(cron = "0 0/1 * * * *")
public CommonResponse getCustomerData(){

    HashMap<String,List<CustomerRecord>> crmData = new HashMap<>();
    CommonResponse commonResponse = new CommonResponse();
    try {
        List<CustomerRecord> crmDataValue = jdbcTemplate.query(crmRecordUtility.getQuery(), new BeanPropertyRowMapper<>(CustomerRecord.class));
        if (!crmDataValue.isEmpty()) {
            logger.info("Data fetched successfully. Number of records: {}", crmDataValue.size());
            commonResponse.setCode("0000");
            commonResponse.setMsg("Data fetched successfully.");
            crmData.put("records", crmDataValue);
            logger.info("Data prepared for CRM integration: {}", crmDataValue);
        }else {
            commonResponse.setCode("1111");
            commonResponse.setMsg("Data not found : {}");
            logger.info("Data not found for query Triggered on Timestamp: {}" ,LocalDateTime.now());
        }
//        crmRecordUtility.callCrmIntegration(crmData, commonResponse);
        logger.info("API triggered successfully. Timestamp: {}", LocalDateTime.now());
    }catch (Exception e){
        commonResponse.setCode("1111");
        commonResponse.setMsg("Technical issue : " + e.getMessage());
        logger.error("Error occurred during data retrieval or CRM integration. Exception: {}",e.getMessage(), e);
    }
    return commonResponse;
}
}
