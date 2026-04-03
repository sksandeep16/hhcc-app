package com.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner checkDatabaseConnection(DataSource dataSource) {
		return args -> {
			try (Connection connection = dataSource.getConnection()) {
				String url = connection.getMetaData().getURL();
				String driverName = connection.getMetaData().getDriverName();
				System.out.println("==============================================");
				System.out.println("  DATABASE CONNECTION: SUCCESS");
				System.out.println("  URL    : " + url);
				System.out.println("  Driver : " + driverName);
				System.out.println("  Status : " + (connection.isValid(2) ? "Valid" : "Invalid"));
				System.out.println("==============================================");
			} catch (Exception e) {
				System.out.println("==============================================");
				System.out.println("  DATABASE CONNECTION: FAILED");
				System.out.println("  Reason : " + e.getMessage());
				System.out.println("==============================================");
			}
		};
	}

}
