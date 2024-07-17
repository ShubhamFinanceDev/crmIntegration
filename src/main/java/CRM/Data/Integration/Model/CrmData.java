package CRM.Data.Integration.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class CrmData {
    List<HashMap<String,String>> records = new ArrayList<>();
}
