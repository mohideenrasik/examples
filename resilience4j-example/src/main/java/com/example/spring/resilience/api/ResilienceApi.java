package com.example.spring.resilience.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.resilience.service.ApplicationService;
import com.example.spring.resilience.util.CircuitBreakerUtil;

@RestController
@RequestMapping("/resilience")
public class ResilienceApi {

	@Autowired
	private ApplicationService service;
	
	@Autowired
	private CircuitBreakerUtil cbUtil;
	
	@GetMapping("/success") 
	public ResponseEntity<String> success() {
		String result = cbUtil.executeWithCircuitBreaker("success", service::invokeGoogle, () -> "Google Not Reachable");
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
	
	@GetMapping("/fail") 
	public ResponseEntity<String> fail() {
		String result = cbUtil.executeWithCircuitBreaker("fail", service::alwaysFail, () -> "The method is failing");
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
}
