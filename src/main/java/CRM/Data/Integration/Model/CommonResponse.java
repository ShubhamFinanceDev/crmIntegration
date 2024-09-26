package CRM.Data.Integration.Model;

import lombok.Data;

import java.io.File;

@Data
public class CommonResponse {
    private String msg;
    private File excel;
}
