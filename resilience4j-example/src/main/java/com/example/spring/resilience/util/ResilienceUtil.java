package com.example.spring.resilience.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.spring.resilience.config.ResilienceConfigurationMap;

public abstract class ResilienceUtil {
	
	public abstract String getResiliencePatternName();
	
	@Autowired
	private ResilienceConfigurationMap resilienceConfigurationMap;
	
	protected int getIntConfiguration(String cbName, String configName) {
		return Integer.parseInt(getConfiguration(cbName, configName));
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends Throwable>[] getThrowableConfiguration(String patternName, String configName) {
		try {
			Class<? extends Throwable>[] exceptions = new Class[0];
			String classNames = getConfiguration(patternName, configName);
			if (null != classNames) {
				String[] classes = classNames.split(",");
				exceptions = new Class[classes.length];
				int counter = -1;
				for (String className: classes) {
					exceptions[++counter] = (Class<? extends Throwable>) Class.forName(className.trim());
				}
			}
			return exceptions;
		} catch (Exception e) {
			throw new RuntimeException("Invalid resilience pattern configurations:" + patternName + "." + configName, e);
		}
	}
	
	protected String getConfiguration(String cbName, String configName) {
		Map<String, Map<String, String>> patternConfig = getConfigParamters(resilienceConfigurationMap, getResiliencePatternName());
		if (null != patternConfig.get(cbName) && 
				null != patternConfig.get(cbName).get(configName)) {
			return patternConfig.get(cbName).get(configName);
		} else {
			return patternConfig.get("default").get(configName);
		}
	}
	
	protected Map<String, Map<String, String>> getConfigParamters(ResilienceConfigurationMap config, String pattern) {
		return config.getResilience().get(pattern);
	}
}
