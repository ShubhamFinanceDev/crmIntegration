package CRM.Data.Integration.ServiceImpl;

import CRM.Data.Integration.Entity.CrmRecords;
import CRM.Data.Integration.Model.CrmData;
import CRM.Data.Integration.Repository.CrmRecordsRepo;
import CRM.Data.Integration.Utility.CrmRecordUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ServiceImpl implements CRM.Data.Integration.Service.Service {

    @Autowired
    private CrmRecordsRepo crmRecordsRepo;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CrmRecordUtility crmRecordUtility;

    public CrmData getCustomerData(){

        List<HashMap<String,String>> listOfRecords = new ArrayList<>();
        CrmData crmData = new CrmData();
        try {
//            List<CrmRecords> crmData = crmRecordsRepo.findAll();
            List<CrmRecords> crmDataValue = jdbcTemplate.query(crmRecordUtility.getQuery(),new BeanPropertyRowMapper<>(CrmRecords.class));

            for (CrmRecords record :crmDataValue){
                HashMap<String,String> reportData = new HashMap<>();
                reportData.put("Email ID",record.getEmailId());
                reportData.put("First Name",record.getFirstName());
                reportData.put("Last Name",record.getLastName());
                reportData.put("Contact No 2",record.getContactNo2());
                reportData.put("Residential Address",record.getResidentialAddress());
                reportData.put("City",record.getCity());
                reportData.put("Pincode",record.getPincode());
                reportData.put("State",record.getState());
                reportData.put("Customer Number",record.getCustomerNumber());
                reportData.put("Agreement Number",record.getAgreementNumber());
                reportData.put("Branch",record.getBranch());
                reportData.put("Permanent Address",record.getPermanentAddress());

                listOfRecords.add(reportData);
            }
            crmData.setRecords(listOfRecords);
            crmRecordUtility.callCrmIntegration(crmData);

        }catch (Exception e){
            System.out.println(e);
        }
        return crmData;
    }
}
