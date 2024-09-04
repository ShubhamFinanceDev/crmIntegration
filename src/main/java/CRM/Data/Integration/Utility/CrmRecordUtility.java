package CRM.Data.Integration.Utility;

import CRM.Data.Integration.Model.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class CrmRecordUtility {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${api.url}")
    private String apiUrl;
    @Value("${form.name}")
    private String formName;
    @Value("${operation}")
    private String operation;
    @Value("${overwrite}")
    private String overwrite;
    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = LoggerFactory.getLogger(CrmRecordUtility.class);

    public String getQuery() {
        return "SELECT " +
                "CUSTOMER_NUMBER, " +
                "APPLICATION_NUMBER, " +
                "\"Loan Account No\", " +
                "\"First Name\", " +
                "\"Last Name\", " +
                "\"Mobile Number\", " +
                "\"Residential Address\", " +
                "CITY, " +
                "STATE, " +
                "\"Pin Code\", " +
                "\"Office/ Business Address\", " +
                "\"Permanent Address\", " +
                "\"Branch Name\", " +
                "APPLICATION_RECIEVED_DATE " +
                "FROM neo_cas_lms_sit1_sh.crm2 " +
                "ORDER BY APPLICATION_NUMBER " +
                "FETCH FIRST 1 ROWS ONLY";
    }

    public void callCrmIntegration(HashMap<String, List<?>> crmData, CommonResponse commonResponse) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(crmData));
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, objectMapper.writeValueAsString(crmData), String.class);
            System.out.println("Response: " + responseEntity.getBody());

            commonResponse.setCode("0000");
            commonResponse.setMsg("Success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error while cm api calling" + e.getMessage());
            commonResponse.setCode("1111");
            commonResponse.setMsg("error while cm api calling" + e.getMessage());

        }


    }

    private void getEmailAndSendMail(HashMap<String, Object> crmData, String msg) throws Exception {
        List<HashMap<String, String>> records = (List<HashMap<String, String>>) crmData.get("records");
        for (HashMap<String, String> record : records) {
            if (record.containsKey("Email ID") && record.get("Email ID") != null) {
                sendMail(record.get("Email ID"), msg);
            }
        }
    }

    @Async
    protected void sendMail(String email, String msg) throws Exception {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText(msg);
            mailMessage.setSubject("CrmData Integration Mail");

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
