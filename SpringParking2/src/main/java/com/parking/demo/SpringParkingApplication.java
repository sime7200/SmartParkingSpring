package com.parking.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.parking.demo.service.ParkingService;

@SpringBootApplication
public class SpringParkingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringParkingApplication.class, args);
		DbCreate.main(args);
	}

}
