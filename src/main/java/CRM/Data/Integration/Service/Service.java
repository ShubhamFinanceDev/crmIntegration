package CRM.Data.Integration.Service;

import CRM.Data.Integration.Model.CommonResponse;
import org.springframework.http.ResponseEntity;

import java.sql.Date;

public interface Service {

    ResponseEntity<CommonResponse> getCustomerData(Date date);
}
