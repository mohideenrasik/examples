package com.examples.bpm.camunda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.examples.bpm.camunda.tasks.GreetingsDelegate;

@Configuration
public class DelegatesConfiguration {

	@Value("${greeting}")
	private String greeting;
	
	@Bean
	public GreetingsDelegate greetings() {
		return new GreetingsDelegate(greeting);
	}
}
