package com.example.spring.resilience.util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class CircuitBreakerUtil extends ResilienceUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerUtil.class);
	
	private Map<String, CircuitBreaker> circuitBreakersMap = new HashMap<String, CircuitBreaker>();
	
	private CircuitBreakerRegistry circuitBreakerRegistry;
	
	//Required only when you have metrics enabled in configuration
	@Autowired
	private MeterRegistry meterRegistry;

	@Override
	public String getResiliencePatternName() {
		return "circuitBreaker";
	}
	
	public <T> T executeWithCircuitBreaker(String key, Supplier<T> protectedMethod) {
		return decorateWithCircuitBreaker(key, protectedMethod).get();
	}
	
	public <T> T executeWithCircuitBreaker(String key, Supplier<T> protectedMethod, Supplier<T> fallback) {
		return decorateWithCircuitBreaker(key, protectedMethod, fallback).get();	
	}
	
	public <T> Supplier<T> decorateWithCircuitBreaker(String key, Supplier<T> protectedMethod) {
		CircuitBreaker cb = circuitBreakersMap.computeIfAbsent(key, (k) -> buildCircuitBreaker(k));
		Supplier<T> interjectedSupplier = () -> {
			LOGGER.info("CircuitBreaker ({}) protected invocation happens on {}", cb.getName(), protectedMethod.toString());
			return protectedMethod.get();
		};
		return cb.decorateSupplier(interjectedSupplier);
	}
	
	public <T> Supplier<T> decorateWithCircuitBreaker(String key, Supplier<T> protectedMethod, Supplier<T> fallback) {
		return () -> {
			try {
				return decorateWithCircuitBreaker(key, protectedMethod).get();	
			} catch (CallNotPermittedException e) {
				return fallback.get();
			}
		};
	}
	
	private CircuitBreaker buildCircuitBreaker(String cbName) {
		if (null == circuitBreakerRegistry) {
			LOGGER.info("Initializing CircuitBreaker Registry");
			CircuitBreakerConfig config = getConfig("default");
			LOGGER.info("Configuring CircuitBreaker Registry with default configurations", config);
			circuitBreakerRegistry = CircuitBreakerRegistry.of(config);
			boolean metricsEnabled = Boolean.valueOf(getConfiguration("default", "metricsEnabled"));
			if (metricsEnabled) {
				//Make sure you have meter registry bean registered with appropriate configuration
				TaggedCircuitBreakerMetrics
					.ofCircuitBreakerRegistry(circuitBreakerRegistry)
					.bindTo(meterRegistry);
			}
		}
		
		LOGGER.info("Creating a new Circuit Breaker for {}", cbName);
		CircuitBreakerConfig cbSpecificConfig = getConfig(cbName);
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(cbName, cbSpecificConfig);
		circuitBreaker.getEventPublisher().onStateTransition((e) -> {
			if (CircuitBreaker.State.CLOSED == e.getStateTransition().getToState()) {
				LOGGER.error("Circuit breaker {} is in CLOSED state", cbName);
			} else {
				LOGGER.error("Circuit breaker {} is in OPEN/HALF-OPEN state", cbName);
			}
		});
		return circuitBreaker;
	}
	
	private CircuitBreakerConfig getConfig(String cbName) {
		// CircuitBreaker configuration can be overridden for specific instances identified by the name 
		return CircuitBreakerConfig.custom()
				.failureRateThreshold(getIntConfiguration(cbName, "failureRateThreshold"))
				.slowCallRateThreshold(getIntConfiguration(cbName, "slowCallRateThreshold"))
				.slowCallDurationThreshold(Duration.ofMillis(getIntConfiguration(cbName, "slowCallDurationThreshold")))
				.permittedNumberOfCallsInHalfOpenState(getIntConfiguration(cbName, "permittedNumberOfCallsInHalfOpenState"))
				.maxWaitDurationInHalfOpenState(Duration.ofMillis(getIntConfiguration(cbName, "maxWaitDurationInHalfOpenState")))
				.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.valueOf(getConfiguration(cbName, "slidingWindowType")))
				.slidingWindowSize(getIntConfiguration(cbName, "slidingWindowSize"))
				.minimumNumberOfCalls(getIntConfiguration(cbName, "minimumNumberOfCalls"))
				.waitDurationInOpenState(Duration.ofMillis(getIntConfiguration(cbName, "waitDurationInOpenState")))
				.automaticTransitionFromOpenToHalfOpenEnabled(Boolean.valueOf(getConfiguration(cbName, "waitDurationInOpenState")))
			    .recordExceptions(getThrowableConfiguration(cbName, "recordExceptions"))
			    .ignoreExceptions(getThrowableConfiguration(cbName, "ignoreExceptions"))
			    .build();
	}
}
