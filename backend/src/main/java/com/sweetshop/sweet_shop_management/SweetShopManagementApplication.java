package com.sweetshop.sweet_shop_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SweetShopManagementApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication app = new SpringApplication(SweetShopManagementApplication.class);
		app.setAddCommandLineProperties(false);
		app.run(args);
	}

}
