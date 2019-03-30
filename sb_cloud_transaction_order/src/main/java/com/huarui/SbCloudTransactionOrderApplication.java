package com.huarui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class SbCloudTransactionOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbCloudTransactionOrderApplication.class, args);
	}

}
