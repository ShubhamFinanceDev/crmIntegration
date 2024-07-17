package CRM.Data.Integration.Utility;

import CRM.Data.Integration.Model.CrmData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
@Service
public class CrmRecordUtility {
    @Value("${api.url}")
    private String apiUrl;
    @Value("${form.name}")
    private String formName;
    @Value("${operation}")
    private String operation;
    @Value("${overwrite}")
    private String overwrite;
    // @Value("${api.key}")
    // private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getQuery() {
        return "select * from records";
    }

    public void callCrmIntegration(CrmData crmData) {

        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("data-raw", crmData.toString());
        formData.add("formname", formName);
        formData.add("operation", operation);
        formData.add("overwrite", overwrite);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            String responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseBody != null) {
                if (responseBody.contains("SUCCESS")) {
                    System.out.println("Success response: " + responseBody);
                } else {
                    System.out.println("API returned error status: " + responseBody);
                }
            } else {
                System.out.println("API call failed with status code: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }
}