package com.example.spring.resilience.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties
public class ResilienceConfigurationMap {

	private Map<String, Map<String, Map<String, String>>> resilience;
}
