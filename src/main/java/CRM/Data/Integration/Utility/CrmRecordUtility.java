package CRM.Data.Integration.Utility;

import CRM.Data.Integration.Model.CommonResponse;
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

import java.time.LocalDate;
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

    public String getQuery(LocalDate applicationReceivedDate) {
        return " SELECT CUSTOMER_NUMBER APPLICATION_NUMBER Loan Account No First Name Last Name Mobile Number Residential Address \n" +
                    "CITY STATE Pin Code Office/ Business Address Permanent Address Branch Name APPLICATION_RECIEVED_DATE \n" +
                    "FROM neo_cas_lms_sit1_sh.crm2 ORDER BY 2 \n" +
                    "WHERE APPLICATION_RECIEVED_DATE = TO_Date('" + applicationReceivedDate + "', 'YYYY-MM-DD')";
    }

    public void callCrmIntegration(byte[] serializeData, HashMap<String, Object> crmData, CommonResponse commonResponse) throws Exception{

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("data-raw", Arrays.toString(serializeData));
        formData.add("formname", formName);
        formData.add("operation", operation);
        formData.add("overwrite", overwrite);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("Print raw response :" + responseEntity);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseBody != null) {
                if (responseBody.contains("SUCCESS") || responseBody.contains("200")) {
                    getEmailAndSendMail(crmData, "SUCCESS");
                    commonResponse.setCode("0000");
                    commonResponse.setMsg("API call succeeded with status: " + responseBody);
                    logger.info("API call succeeded with status : {}", responseBody);
                } else {
                    getEmailAndSendMail(crmData, "FAILURE");
                    commonResponse.setCode("1111");
                    commonResponse.setMsg("API call returned failure status: " + responseBody);
                    logger.info("API call returned failure status: {}", responseBody);
                }
            } else {
                commonResponse.setCode("1111");
                commonResponse.setMsg("API call failed with status code: " + responseEntity.getStatusCode());
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
    private void sendMail(String email, String msg) throws Exception{
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
