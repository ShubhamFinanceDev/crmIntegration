package CRM.Data.Integration.Controller;

import CRM.Data.Integration.Service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;

@RestController
public class Controller {
    @Autowired
    private Service service;
    private final Logger logger = LoggerFactory.getLogger(Controller.class);

    @PostMapping("/crm-data-integration")
    public ResponseEntity<?> crmIntegration(@RequestParam(name = "date") String date){
        logger.info("CRM Data invoked manually");
       return service.getCustomerData(date);
    }
}