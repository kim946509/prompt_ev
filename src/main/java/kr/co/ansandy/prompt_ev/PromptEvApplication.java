package kr.co.ansandy.prompt_ev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PromptEvApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromptEvApplication.class, args);
	}

}
