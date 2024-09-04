package CRM.Data.Integration.Utility;

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

    public String getQuery() throws Exception {
        return "SELECT\n" +
                "    \"CUSTOMER_NUMBER\",\n" +
                "    \"APPLICATION_NUMBER\",\n" +
                "    \"Loan Account No\",\n" +
                "    \"First Name\",\n" +
                "    \"Last Name\",\n" +
                "    \"Mobile Number\",\n" +
                "    \"Residential Address\",\n" +
                "    \"CITY\",\n" +
                "    \"STATE\",\n" +
                "    \"Pin Code\",\n" +
                "    \"Office/ Business Address\",\n" +
                "    \"Permanent Address\",\n" +
                "    \"Branch Name\",\n" +
                "    \"APPLICATION_RECIEVED_DATE\"\n" +
                "FROM\n" +
                "    neo_cas_lms_sit1_sh.crm2\n" +
                "ORDER BY\n" +
                "    2";
    }

    public void callCrmIntegration(byte[] serializeData, HashMap<String, Object> crmData) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("data-raw", Arrays.toString(serializeData));
        formData.add("formname", formName);
        formData.add("operation", operation);
        formData.add("overwrite", overwrite);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("print raw response :"+responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseBody != null) {
                if (responseBody.contains("SUCCESS") || responseBody.contains("200")) {
                  //  getEmailAndSendMail(crmData,"SUCCESS");
                    System.out.println("API returned error status: " + responseBody);
                }else{
                   // getEmailAndSendMail(crmData, "Failure");
                    System.out.println("API returned error status: " + responseBody);
                }
            } else {
                System.out.println("API call failed with status code: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }

    private void getEmailAndSendMail(HashMap<String, Object> crmData, String msg) {

        List<HashMap<String, String>> records = (List<HashMap<String, String>>) crmData.get("records");
        for (HashMap<String, String> record : records){
            if (record.containsKey("Email Address") && record.get("Email Address") != null) {
//                sendMail(record.get("Email Address"),msg);
                sendMail("saurabhsingh2757@gmail.com",msg);
            }
        }
    }

    @Async
    protected void sendMail(String email, String msg) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText(msg);
            mailMessage.setSubject("CrmData integration mail");

            javaMailSender.send(mailMessage);
            System.out.println("Send mail successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}