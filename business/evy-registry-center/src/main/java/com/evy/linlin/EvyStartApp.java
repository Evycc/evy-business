package com.evy.linlin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EvyStartApp {

	public static void main(String[] args) {
		SpringApplication.run(EvyStartApp.class, args);
	}

}
