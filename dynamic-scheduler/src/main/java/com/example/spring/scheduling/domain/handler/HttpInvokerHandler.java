package com.example.spring.scheduling.domain.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.spring.scheduling.domain.ScheduleHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

public class HttpInvokerHandler implements ScheduleHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpInvokerHandler.class);
	
	private Config config;
	
	@Override
	public void setConfig(String configJson) {
		try {
			config = new ObjectMapper().readValue(configJson, Config.class);	
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void execute() {
		if (null != config) {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			if (null != config.requestHeaders) {
				for(Map.Entry<String, String> entry: config.requestHeaders.entrySet()) {
					headers.add(entry.getKey(), entry.getValue());
				}
			}
			
			if ("GET".equalsIgnoreCase(config.getHttpMethod())) {
				ResponseEntity<String> responseEntity = restTemplate.exchange(
						config.targetUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
				LOGGER.info("Request made to [{}] and received [{}]", config.targetUrl, responseEntity.getBody());
			} else if ("POST".equalsIgnoreCase(config.getHttpMethod())) {
				ResponseEntity<String> responseEntity = restTemplate.exchange(
						config.targetUrl, HttpMethod.POST, new HttpEntity<>(config.requestBody, headers), String.class);
				LOGGER.info("Request made to [{}] and received [{}]", config.targetUrl, responseEntity.getBody());
			}  	
		}
	}

	@Data
	@NoArgsConstructor
	public static class Config {
		private String targetUrl;
		private String httpMethod;
		private String requestBody;
		private Map<String, String> requestHeaders;
	}
	
	/*
	public static void main(String[] a) throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("content-type", "plain/text");
		Config c = new Config();
		c.setHttpMethod("POST");
		c.setRequestBody("test");
		c.setRequestHeaders(headers);
		c.setTargetUrl("http://www.google.com");
		System.out.println(new ObjectMapper().writeValueAsString(c));
	}
	*/
}
