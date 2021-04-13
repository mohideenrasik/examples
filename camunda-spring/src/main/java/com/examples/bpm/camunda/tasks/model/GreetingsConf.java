package com.examples.bpm.camunda.tasks.model;

import org.camunda.bpm.engine.delegate.BpmnError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GreetingsConf {

	private String name;
	private String salutation;
	
	public void validate() {
		if (null != salutation && 
				!"Mr".equalsIgnoreCase(salutation) &&
				!"Mrs".equalsIgnoreCase(salutation) &&
				!"Miss".equalsIgnoreCase(salutation)) {
			throw new BpmnError("GREETINGS_CONFIG_ERROR", "Invalid salutation");
		}
		
		if (null == name || name.isEmpty()) {
			throw new BpmnError("GREETINGS_CONFIG_ERROR", "Invalid name");
		}
	}
}
