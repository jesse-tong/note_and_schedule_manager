package com.jessetong.notemanager.notedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication(
scanBasePackages = "com.jessetong.notemanager",
exclude = { SecurityAutoConfiguration.class })
@EnableJpaRepositories(basePackages = "com.jessetong.notemanager.repository")
@EntityScan(basePackages = "com.jessetong.notemanager.entity")
@RestController
public class NotedemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotedemoApplication.class, args);
	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		return "Hello, World!";
	}
	@GetMapping("/greet")
	public String greet(@RequestParam(value = "name", defaultValue = "World", required = false) String name) {
		return "Hello, " + (name != null ? name : "") + "!";
	}

}
