package com.example.discoverserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author admin
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoverServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoverServerApplication.class, args);
	}

}

