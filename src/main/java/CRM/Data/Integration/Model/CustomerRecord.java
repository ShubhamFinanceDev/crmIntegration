package CRM.Data.Integration.Model;

import lombok.Data;

import java.time.LocalDateTime;

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
    private LocalDateTime applicationReceivedDate;
}