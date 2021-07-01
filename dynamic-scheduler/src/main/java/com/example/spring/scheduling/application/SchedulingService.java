package com.example.spring.scheduling.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.example.spring.scheduling.domain.ScheduleHandler;
import com.example.spring.scheduling.domain.ScheduledJob;
import com.example.spring.scheduling.domain.ScheduledJobRepository;


@Service
public class SchedulingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingService.class);
	
	@Value("${scheduler.pollingIntervalMins}")
	private int pollingIntervalMins;
	
	@Autowired
	private TaskScheduler taskScheduler;
	
	@Autowired
	private ScheduledJobRepository repository;
	
	private Map<String, ScheduledJob> scheduledJobs = new HashMap<String, ScheduledJob>();
	private Map<String, ScheduledFuture<?>> futures = new HashMap<String, ScheduledFuture<?>>();
	
	@PostConstruct
	private void loadScheduledJobs() {
		taskScheduler.scheduleAtFixedRate(() -> {
			LOGGER.info("Refreshing scheduled job details from persistence store");
			loadJobDetails();
		}, pollingIntervalMins * 60 * 1000);
	}
	
	private void loadJobDetails() {
		List<ScheduledJob> jobs = repository.findByActive(true);
		for (ScheduledJob job: jobs) {
			boolean scheduleJob = true;
			if (null != scheduledJobs.get(job.getId())) {
				scheduleJob = job.getVersion() != scheduledJobs.get(job.getId()).getVersion(); 
			}
			
			if (scheduleJob) {
				killJob(job, futures.get(job.getId()));
				scheduleJob(job);
			}
		}
	}
	
	private void killJob(ScheduledJob job, ScheduledFuture<?> future) {
		if (null != future && !future.isCancelled() && !future.isDone()) {
			LOGGER.info("Issuing kill signal to job: {} ", job.getId());
			future.cancel(false);
		}
	}
	
	private void scheduleJob(ScheduledJob job) {
		try {
			LOGGER.info("Scheduling new job instance: {} ",  job.getId());
			ScheduleHandler handler = (ScheduleHandler) Class.forName(job.getExecutionHandler()).newInstance();
			Runnable task = () -> {
				handler.setConfig(job.getExecutionHandlerConfig());
				handler.execute();
			};
			
			String schedule = job.getSchedule().toUpperCase().trim();
			ScheduledFuture<?> future = null;
			if (schedule.startsWith("CRON:")) {
				future = taskScheduler.schedule(task, new CronTrigger(job.getSchedule().substring("CRON:".length())));
			} else if (schedule.startsWith("FIXEDRATE:")) {
				future = taskScheduler.scheduleAtFixedRate(task, Long.parseLong(job.getSchedule().substring("FIXEDRATE:".length())));
			} else if (schedule.startsWith("FIXEDDELAY:")) {
				future = taskScheduler.scheduleWithFixedDelay(task, Long.parseLong(job.getSchedule().substring("FIXEDDELAY:".length())));
			} else {
				throw new RuntimeException("Invalid schedule configuration: " + job.getSchedule());
			}
			
			scheduledJobs.put(job.getId(), job);
			futures.put(job.getId(), future);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.error("Invalid configuration: Execution Handler: {}", job.getExecutionHandler());
		}
	}
}
