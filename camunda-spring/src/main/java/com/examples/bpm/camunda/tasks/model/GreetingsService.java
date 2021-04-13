package com.examples.bpm.camunda.tasks.model;

public class GreetingsService {

	private String greeting;
	private GreetingsConf configuration;
	
	public GreetingsService(String greeting, GreetingsConf configuration) {
		this.greeting = greeting;
		this.configuration = configuration;
	}
	
	public void greet() {
		configuration.validate();
		System.out.println(this.greeting + " " + configuration.getSalutation() + "." + configuration.getName());	
	}
	
}
