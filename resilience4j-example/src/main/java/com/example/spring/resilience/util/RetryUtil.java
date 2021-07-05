package com.example.spring.resilience.util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.retry.MaxRetriesExceededException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@Component
public class RetryUtil extends ResilienceUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetryUtil.class);
	
	private Map<String, Retry> retryMap = new HashMap<String, Retry>();
	
	private RetryRegistry retryRegistry;
	
	@Override
	public String getResiliencePatternName() {
		return "retry";
	}
	
	public <T> T executeWithRetry(String key, Supplier<T> protectedMethod) {
		return decorateWithRetry(key, protectedMethod).get();
	}
	
	public <T> T executeWithRetry(String key, Supplier<T> protectedMethod, Supplier<T> fallback) {
		try {
			return decorateWithRetry(key, protectedMethod).get();
		} catch (MaxRetriesExceededException e) {
			return fallback.get();
		}
	}
	
	public <T> Supplier<T> decorateWithRetry(String key, Supplier<T> protectedMethod) {
		Retry retry = retryMap.computeIfAbsent(key, (k) -> buildRetry(k));
		Supplier<T> interjectedSupplier = () -> {
			LOGGER.info("Retry ({}) protected invocation happens on {}", retry.getName(), protectedMethod.toString());
			return retry.executeSupplier(protectedMethod);
		};
		return interjectedSupplier;
	}
	
	private Retry buildRetry(String retryName) {
		if (null == retryRegistry) {
			LOGGER.info("Initializing Retry Registry");
			RetryConfig config = getConfig("default");
			LOGGER.info("Configuring Retry Registry with default configurations", config);
			retryRegistry = RetryRegistry.of(config);
		}
		
		LOGGER.info("Creating a new Retry for {}", retryName);
		RetryConfig retrySpecificConfig = getConfig(retryName);
		Retry retry = retryRegistry.retry(retryName, retrySpecificConfig);
		return retry;
	}
	
	private RetryConfig getConfig(String retryName) {
		// Retry configuration can be overridden for specific instances identified by the name 
		return RetryConfig.custom()
				.maxAttempts(getIntConfiguration(retryName, "maxAttempts"))
				.waitDuration((Duration.ofMillis(getIntConfiguration(retryName, "waitDuration"))))
				.retryExceptions(getThrowableConfiguration(retryName, "retryExceptions"))
				.ignoreExceptions(getThrowableConfiguration(retryName, "ignoreExceptions"))
				.failAfterMaxAttempts(Boolean.valueOf(getConfiguration(retryName, "failAfterMaxAttempts")))
			    .build();
	}
}
