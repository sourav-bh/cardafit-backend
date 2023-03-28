package uni.siegen.bgf.cardafit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CardaFitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardaFitBackendApplication.class, args);
	}

}