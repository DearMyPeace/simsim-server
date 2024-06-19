package com.project.simsim_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SimsimServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimsimServerApplication.class, args);
	}

}
