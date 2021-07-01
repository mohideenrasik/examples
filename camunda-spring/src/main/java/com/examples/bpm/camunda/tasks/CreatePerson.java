package com.examples.bpm.camunda.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.examples.bpm.camunda.tasks.model.Person;

public class CreatePerson implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Person person = new Person();
		person.setName((String) execution.getVariable("name"));
		person.setEmail((String) execution.getVariable("email"));
		person.setAge((Long) execution.getVariable("age"));
		execution.setVariable("request", person);
		
	}

	
}
