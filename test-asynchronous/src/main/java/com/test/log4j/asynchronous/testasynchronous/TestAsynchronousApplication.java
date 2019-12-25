package com.test.log4j.asynchronous.testasynchronous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestAsynchronousApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestAsynchronousApplication.class, args);
		AsynchronousTest test = new AsynchronousTest();
		test.log();
	}
}
