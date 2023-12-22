package it.polito.se2.g04.thesismanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class ThesisManagementApplication {


	public static void main(String[] args) {
		SpringApplication.run(ThesisManagementApplication.class, args);
	}

}
