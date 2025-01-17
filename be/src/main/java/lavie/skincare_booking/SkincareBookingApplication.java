package lavie.skincare_booking;

import org.springdoc.core.configuration.SpringDocDataRestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class SkincareBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkincareBookingApplication.class, args);
	}

}
