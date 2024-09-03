package CRM.Data.Integration.ServiceImpl;

import CRM.Data.Integration.Utility.CrmDataSerialization;
import CRM.Data.Integration.Utility.CrmRecordUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceImpl implements CRM.Data.Integration.Service.Service {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CrmRecordUtility crmRecordUtility;
    @Autowired
    private CrmDataSerialization crmDataSerialization;

    public byte[] getCustomerData(){


        List<HashMap<String,String>> listOfRecords = new ArrayList<>();
        HashMap<String,Object> crmData = new HashMap<>();
        byte [] serializeData = null;
        try {
            List<Map<String, Object>> crmDataValue = jdbcTemplate.query(crmRecordUtility.getQuery(),new BeanPropertyRowMapper<>());

            for (Map<String, Object> record :crmDataValue){
                HashMap<String,String> reportData = new HashMap<>();
                reportData.put("First Name", (String) record.get("First Name"));
                reportData.put("Last Name", (String) record.get("Last Name"));
                reportData.put("Landline 1", (String) record.get("Landline 1"));
                reportData.put("Mobile Number", (String) record.get("Mobile Number"));
                reportData.put("Email Address", (String) record.get("Email Address"));
                reportData.put("Residential Address", (String) record.get("Residential Address"));
                reportData.put("CITY", (String) record.get("CITY"));
                reportData.put("STATE", (String) record.get("STATE"));
                reportData.put("Pin Code", (String) record.get("Pin Code"));
                reportData.put("Office/Business Address", (String) record.get("Office/Business Address"));
                reportData.put("Permanent Address", (String) record.get("Permanent Address"));
                reportData.put("CUSTOMER_NUMBER", (String) record.get("CUSTOMER_NUMBER"));
                reportData.put("APPLICATION_NUMBER", (String) record.get("APPLICATION_NUMBER"));
                reportData.put("Loan Account No", (String) record.get("Loan Account No"));
                reportData.put("Lead Number", (String) record.get("Lead Number"));
                reportData.put("Application Form Number", (String) record.get("Application Form Number"));
                reportData.put("PAN", (String) record.get("PAN"));
                reportData.put("AADHAR_NO", (String) record.get("AADHAR_NO"));
                reportData.put("DRIVING_LICENCE", (String) record.get("DRIVING_LICENCE"));
                reportData.put("Branch Name", (String) record.get("Branch Name"));
                reportData.put("APPLICATION_RECIEVED_DATE", (String) record.get("APPLICATION_RECIEVED_DATE"));
                reportData.put("CURRENT_STATUS", (String) record.get("CURRENT_STATUS"));

                listOfRecords.add(reportData);
            }
            System.out.println(listOfRecords);
            crmData.put("records", listOfRecords);

            serializeData = crmDataSerialization.serializeCrmData(crmData,"serializedFile.txt");
            crmRecordUtility.callCrmIntegration(serializeData, crmData);
        }catch (Exception e){
            System.out.println(e);
        }
        return serializeData;
    }
}
