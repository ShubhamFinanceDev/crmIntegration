package CRM.Data.Integration.Service;

import CRM.Data.Integration.Model.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface Service {

    ResponseEntity<CommonResponse> getCustomerData(String date);
}
