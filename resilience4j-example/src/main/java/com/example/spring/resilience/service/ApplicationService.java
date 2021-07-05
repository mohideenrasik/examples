package com.example.spring.resilience.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service 
public class ApplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);
	
	public String alwaysFail() {
		LOGGER.info("--------------- Alway Fail Invoked -------------> ");
		throw new RuntimeException("Dont call me");
	}
	
	public String invokeGoogle() {
		LOGGER.info("--------------- Onvoke Google Invoked -------------> ");
		RestTemplate template = new RestTemplate();
		return template.getForEntity("http://www.google.com", String.class).getBody();
	}
}
