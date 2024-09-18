package CRM.Data.Integration.Utility;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class CalendarUtility {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
    public String crmProcessDate(int prevDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -prevDay);
        Date currentDate = calendar.getTime();
        String formattedDate = dateFormat.format(currentDate);
        System.out.println("Process date: " + formattedDate);
        return formattedDate; // Return the formatted date string
    }
}
