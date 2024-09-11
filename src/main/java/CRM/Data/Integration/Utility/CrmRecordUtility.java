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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CrmRecordUtility {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${api.url}")
    private String apiUrl;
    @Value("${project.save.path}")
    private String directoryPath;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = LoggerFactory.getLogger(CrmRecordUtility.class);

    public String getQuery() {
        String query = "select  \"CUSTOMER_NUMBER\" AS customerNumber,\n" +
                "                    distinct\"APPLICATION_NUMBER\" AS applicationNumber,\n" +
                "                    \"Loan Account No\" AS loanAccountNo,\n" +
                "                    \"First Name\" AS firstName,\n" +
                "                    \"Last Name\" AS lastName,\n" +
                "                    \"Mobile Number\" AS mobileNumber,\n" +
                "                    \"Residential Address\" AS residentialAddress,\n" +
                "                    \"CITY\" AS city,\n" +
                "                    \"STATE\" AS state,\n" +
                "                    \"Pin Code\" AS pinCode,\n" +
                "                    \"Office/ Business Address\" AS officeBusinessAddress,\n" +
                "                    \"Permanent Address\" AS permanentAddress,\n" +
                "                    \"Branch Name\" AS branchName from (\n" +
                "select *  from neo_cas_lms_sit1_sh.crm2 where APPLICATION_RECIEVED_DATE !='Migrated Case' \n" +
                "and  APPLICATION_RECIEVED_DATE is not null) where \n" +
                "to_date(substr(APPLICATION_RECIEVED_DATE,1,8),'dd-mm-yy') = to_date(to_char(trunc(sysdate-2),'dd-mm-yyyy'),'dd-mm-yy')\n";
        return query;
    }

    public void callCrmIntegration(HashMap<String, List<?>> crmData, CommonResponse commonResponse) throws Exception {
        logger.info("Crm API invoked");

        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(objectMapper.writeValueAsString(crmData));
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, objectMapper.writeValueAsString(crmData), String.class);

        commonResponse.setMsg((responseEntity.getStatusCode() == HttpStatus.OK && Objects.requireNonNull(responseEntity.getBody()).contains("success")) ? "Success" : "Crm api is having an error");
        logger.info("Received response from CRM integration API. Status Code: {}, Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
    }

    public void generateExcel(List<CustomerRecord> crmDataValue) {
        try {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"First Name", "Application No", "Last Name", "Contact No 1", "Email ID",
                    "Residential Address", "City", "Pincode", "State", "Customer Number",
                    "Agreement Number", "Branch", "Permanent Address"};
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
                row.createCell(12).setCellValue(entry.getBranchName());
                row.createCell(13).setCellValue(entry.getPermanentAddress());
            }
            workbook.write(byteArrayOutputStream);
            workbook.close();

            File file = new File(directoryPath, String.valueOf(LocalDateTime.now()));
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byteArrayOutputStream.writeTo(fileOutputStream);
            }

            System.out.println("Excel file saved to: " + file.getAbsolutePath());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}