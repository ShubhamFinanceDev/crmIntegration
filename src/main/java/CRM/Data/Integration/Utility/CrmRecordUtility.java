package CRM.Data.Integration.Utility;

import CRM.Data.Integration.Model.CommonResponse;
import CRM.Data.Integration.Model.CustomerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class CrmRecordUtility {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${spring.mail.receiver}")
    private String receiver;
    @Value("${api.url}")
    private String apiUrl;
    @Value("${excel.save.path}")
    private String directoryPath;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = LoggerFactory.getLogger(CrmRecordUtility.class);

    public String getQuery(String date) {
        String query = "SELECT DISTINCT \n" +
                "       \"APPLICATION_NUMBER\" AS applicationNumber, \n" +
                "       \"CUSTOMER_NUMBER\" AS customerNumber,\n" +
                "       \"Loan Account No\" AS loanAccountNo,\n" +
                "       \"First Name\" AS firstName,\n" +
                "       \"Last Name\" AS lastName,\n" +
                "       DBMS_LOB.SUBSTR(\"Mobile Number\", 4000, 1) AS mobileNumber,\n" +
                "       \"Residential Address\" AS residentialAddress,\n" +
                "       \"CITY\" AS city,\n" +
                "       \"STATE\" AS state,\n" +
                "       \"Pin Code\" AS pinCode,\n" +
                "       \"Office/ Business Address\" AS officeBusinessAddress,\n" +
                "       \"Permanent Address\" AS permanentAddress,\n" +
                "       \"Branch Name\" AS branchName ,\n" +
                "       \"APPLICATION_RECIEVED_DATE\" AS APPLICATIONRECIEVEDDATE\n" +
                "FROM (\n" +
                "    SELECT *  \n" +
                "    FROM neo_cas_lms_sit1_sh.crm2 \n" +
                "    WHERE APPLICATION_RECIEVED_DATE != 'Migrated Case' \n" +
                "    AND APPLICATION_RECIEVED_DATE IS NOT NULL\n" +
                ") \n" +
                "WHERE APPLICATION_RECIEVED_DATE IS NOT NULL\n" +
                "  AND LENGTH(APPLICATION_RECIEVED_DATE) >= 19  -- Ensure it has date and time\n" +
                "  AND REGEXP_LIKE(SUBSTR(APPLICATION_RECIEVED_DATE, 1, 8), '^[0-9]{2}-[0-9]{2}-[0-9]{2}$')\n" +
                "  AND TO_DATE(SUBSTR(APPLICATION_RECIEVED_DATE, 1, 8), 'dd-mm-yy') = TO_DATE('"+date+"', 'dd-mm-yy')";
        return query;
    }

    public void callCrmIntegration(HashMap<String, List<?>> crmData, CommonResponse commonResponse) throws Exception {
        logger.info("Crm API invoked");

        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(objectMapper.writeValueAsString(crmData));
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, objectMapper.writeValueAsString(crmData), String.class);

        boolean isSuccess = responseEntity.getStatusCode() == HttpStatus.OK && Objects.requireNonNull(responseEntity.getBody()).contains("success");
        System.out.println(isSuccess);
        commonResponse.setMsg(isSuccess ? "Success" : "Crm API is having an error");
        logger.info("Received response from CRM integration API. Status Code: {}, Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
        if (isSuccess) {
            sendMail("CRM integration was successful");
        } else {
            sendMail("CRM integration was failed");
        }
    }

    @Async
    public void sendMail(String msg) throws Exception {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(receiver);
            mailMessage.setText(msg);
            mailMessage.setSubject("CrmData Integration Mail");

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void generateExcel(List<CustomerRecord> crmDataValue) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"First Name", "Application No", "Last Name", "Contact No 1", "Email ID", "Residential Address", "City", "Pincode", "State", "Customer Number", "Agreement Number", "Branch", "Permanent Address"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (CustomerRecord entry : crmDataValue) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getFirstName());
                row.createCell(1).setCellValue(entry.getApplicationNumber());
                row.createCell(2).setCellValue(entry.getLastName());
                row.createCell(3).setCellValue(entry.getMobileNumber());
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue(entry.getResidentialAddress());
                row.createCell(6).setCellValue(entry.getCity());
                row.createCell(7).setCellValue(entry.getPinCode());
                row.createCell(8).setCellValue(entry.getState());
                row.createCell(9).setCellValue(entry.getCustomerNumber());
                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue(entry.getBranchName());
                row.createCell(12).setCellValue(entry.getPermanentAddress());
            }
            workbook.write(byteArrayOutputStream);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String timestamp = LocalDateTime.now().format(formatter);
            File file = new File(directoryPath, "CustomerRecords_" + timestamp + ".xlsx");

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byteArrayOutputStream.writeTo(fileOutputStream);
            }

            System.out.println("Excel file saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}