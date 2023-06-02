package pl.sienicki.atipera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AtiperaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtiperaApplication.class, args);
	}

}
