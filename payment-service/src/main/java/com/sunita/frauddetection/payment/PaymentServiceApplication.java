package com.sunita.frauddetection.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
		System.out.println("JVM Timezone = " + java.util.TimeZone.getDefault());
	}

}
