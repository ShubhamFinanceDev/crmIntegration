package CRM.Data.Integration.Controller;

import CRM.Data.Integration.Model.CommonResponse;
import CRM.Data.Integration.Service.Service;
import CRM.Data.Integration.ServiceImpl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private Service service;
    private final Logger logger = LoggerFactory.getLogger(Controller.class);

    @GetMapping("/crm-data-integration")
    public ResponseEntity<?> crmIntegration(){
        CommonResponse commonResponse = ResponseEntity.ok(service.getCustomerData()).getBody();
        logger.info("CRM integration endpoint hit manually.");
        assert commonResponse != null;
        if (commonResponse.getCode().equals("0000")){
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
