package com.example.spring.resilience.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;

@Configuration
public class MeterRegistryConfig {

	@Bean
	public MeterRegistry meterRegistry() {
		return new CompositeMeterRegistry()
				.add(jmxMeterRegistry());
	}
	
	private MeterRegistry jmxMeterRegistry() {
		return new JmxMeterRegistry(new JmxConfig() {
			
			@Override
			public String get(String key) {
				return null;
			}
		}, Clock.SYSTEM);
	}	
}
