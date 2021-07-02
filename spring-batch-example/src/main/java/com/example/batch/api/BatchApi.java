package com.example.batch.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchApi {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchApi.class);
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private TaskExecutor taskExecutor;

	@GetMapping("/job/{jobId}/start")
	public ResponseEntity<String> triggerJob(@PathVariable("jobId") String jobId) {
		
		try {
			//Find the Job object by the id 
			Job job = (Job) context.getBean(jobId);
			taskExecutor.execute(() -> {
				try {
					//It is required to send different input parameters to the job for each call, otherwise it will not run second time 
					JobParameters parameters = new JobParametersBuilder()
							.addLong("time", System.currentTimeMillis())
							.toJobParameters();
					jobLauncher.run(job, parameters);
				} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
						| JobParametersInvalidException e) {
					LOGGER.error(e.getMessage(), e);
				}	
			});
			
			return new ResponseEntity<String>("Start signal sent for Job " + jobId , HttpStatus.OK);
		} catch (NoSuchBeanDefinitionException | ClassCastException ex ) {
			return new ResponseEntity<String>("Unable to find Job " + jobId , HttpStatus.NOT_FOUND);
		}
	}
}
