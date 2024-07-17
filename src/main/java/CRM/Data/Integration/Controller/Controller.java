package CRM.Data.Integration.Controller;

import CRM.Data.Integration.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/crm-data-integration")
    public ResponseEntity<?> crmIntegration(){
        return ResponseEntity.ok(service.getCustomerData());
    }
}
