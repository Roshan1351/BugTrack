package com.bugtrack.bugtrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BugtrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugtrackApplication.class, args);
	}

}
