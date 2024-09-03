package CRM.Data.Integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CrmDataIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmDataIntegrationApplication.class, args);
	}

}
