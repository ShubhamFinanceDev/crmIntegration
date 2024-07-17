package CRM.Data.Integration.Service;

import CRM.Data.Integration.Model.CrmData;

import java.util.HashMap;
import java.util.List;

@org.springframework.stereotype.Service
public interface Service {

    CrmData getCustomerData();
}
