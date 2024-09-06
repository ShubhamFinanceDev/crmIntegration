package CRM.Data.Integration.Utility;

import CRM.Data.Integration.Model.CommonResponse;
import CRM.Data.Integration.Model.CustomerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class CrmRecordUtility {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = LoggerFactory.getLogger(CrmRecordUtility.class);

    public String getQuery() {
        String query = "SELECT\n" +
                "    \"CUSTOMER_NUMBER\" AS customerNumber,\n" +
                "    \"APPLICATION_NUMBER\" AS applicationNumber,\n" +
                "    \"Loan Account No\" AS loanAccountNo,\n" +
                "    \"First Name\" AS firstName,\n" +
                "    \"Last Name\" AS lastName,\n" +
                "    \"Mobile Number\" AS mobileNumber,\n" +
                "    \"Residential Address\" AS residentialAddress,\n" +
                "    \"CITY\" AS city,\n" +
                "    \"STATE\" AS state,\n" +
                "    \"Pin Code\" AS pinCode,\n" +
                "    \"Office/ Business Address\" AS officeBusinessAddress,\n" +
                "    \"Permanent Address\" AS permanentAddress,\n" +
                "    \"Branch Name\" AS branchName,\n" +
                "    \"APPLICATION_RECIEVED_DATE\" AS applicationReceivedDate\n" +
                "FROM\n" +
                "    neo_cas_lms_sit1_sh.crm2\n" +
                "ORDER BY\n" +
                "    applicationNumber  \n" +
                "FETCH FIRST 1 ROWS ONLY";

//        logger.info("Executing query: {}",query);
        return query;
    }

    public void callCrmIntegration(HashMap<String, List<CustomerRecord>> crmData, CommonResponse commonResponse) throws Exception {

        logger.info("Crm API invoked");

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(crmData));
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, objectMapper, String.class);

        commonResponse.setMsg((responseEntity.getStatusCode() == HttpStatus.OK && Objects.requireNonNull(responseEntity.getBody()).contains("success")) ? "Success" : "Crm api is having an error");
        logger.info("Received response from CRM integration API. Status Code: {}, Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
    }


}
