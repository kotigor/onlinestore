package com.konstantinov.onlinestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class OnlinestoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlinestoreApplication.class, args);
	}

}
