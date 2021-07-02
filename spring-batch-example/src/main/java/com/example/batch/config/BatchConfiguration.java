package com.example.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Value("${batch.executor.corePoolSize}")
	private int corePoolSize;
	
	@Value("${batch.executor.maxPoolSize}")
	private int maxPoolSize;
	
	@Bean
	public TaskExecutor threadPoolTaskExecutor() {
		//This executor is used to the requests on a controlled threaded environment
		//Multi-threading inside task execution has to be configured at the individual component level
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
	    return executor;
	}
	
}
