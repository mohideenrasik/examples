package com.examples.bpm.camunda.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.examples.bpm.camunda.tasks.model.GreetingsConf;
import com.examples.bpm.camunda.tasks.model.GreetingsService;

public class GreetingsDelegate implements JavaDelegate {

	private String greeting;
	
	public GreetingsDelegate(String greeting) {
		this.greeting = greeting;
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String name = (String) execution.getVariable("name");
		String salutation = (String) execution.getVariable("salutation");
		new GreetingsService(greeting, new GreetingsConf(name, salutation)).greet();
	}
}
