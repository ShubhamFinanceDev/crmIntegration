package CRM.Data.Integration.Entity;

import jakarta.persistence.*;
import lombok.Data;

@jakarta.persistence.Entity
@Data
@Table(name = "records")
public class CrmRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "email_id")
    private String emailId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "contact_no2")
    private String contactNo2;
    @Column(name = "residential_address")
    private String residentialAddress;
    @Column(name = "city")
    private String city;
    @Column(name = "pincode")
    private String pincode;
    @Column(name = "state")
    private String state;
    @Column(name = "customer_number")
    private String customerNumber;
    @Column(name = "agreement_number")
    private String agreementNumber;
    @Column(name = "branch")
    private String branch;
    @Column(name = "permanent_address")
    private String permanentAddress;
}
