package CRM.Data.Integration.Model;

import lombok.Data;

@Data
public class CustomerRecord {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String residentialAddress;
    private String city;
    private String state;
    private String pinCode;
    private String officeBusinessAddress;
    private String permanentAddress;
    private String customerNumber;
    private String applicationNumber;
    private String loanAccountNo;
    private String branchName;
    private String applicationReceivedDate;
}