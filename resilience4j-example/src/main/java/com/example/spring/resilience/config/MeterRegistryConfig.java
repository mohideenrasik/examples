package com.example.spring.resilience.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.spectator.atlas.AtlasConfig;

import io.micrometer.atlas.AtlasMeterRegistry;
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
				.add(jmxMeterRegistry())
				.add(atlasMeterRegistry());
	}
	
	private MeterRegistry jmxMeterRegistry() {
		return new JmxMeterRegistry(new JmxConfig() {
			
			@Override
			public String get(String key) {
				return null;
			}
		}, Clock.SYSTEM);
	}
	
	private MeterRegistry atlasMeterRegistry() {
		return new AtlasMeterRegistry(new AtlasConfig() {

			//While we can override the default configuration here, we can also provide throug application.yml file 
			@Override
			public String get(String k) {
				return null;
			}
		}, Clock.SYSTEM);
	}
}
