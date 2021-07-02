package com.example.spring.scheduling.application;

import java.util.Calendar;
import java.util.Date;
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
		//Lambda that schedules the configured jobs using Spring Task Scheduler
		taskScheduler.scheduleAtFixedRate(() -> {
			LOGGER.info("Refreshing scheduled job details from persistence store");
			loadJobDetails();
		}, pollingIntervalMins * 60 * 1000);
	}
	
	private void loadJobDetails() {
		List<ScheduledJob> jobs = repository.findAll();
		
		for (ScheduledJob job: jobs) {
			boolean jobDefinitionUpdated = true;
			
			if (null != scheduledJobs.get(job.getId())) {
				//Check if the job definition is updated during the last polling interval 
				jobDefinitionUpdated = job.getVersion() == scheduledJobs.get(job.getId()).getVersion(); 
			} 
			
			//Kill an existing scheduled instance of the job if its lifetime is over
			if (null != job.getEndTimestamp() && job.getEndTimestamp().before(Calendar.getInstance().getTime())) {
				killJob(job, futures.get(job.getId()));
			}
			
			if (jobDefinitionUpdated) {
				//Reschedule Job because it is now or updated since last fetch
				killJob(job, futures.get(job.getId()));
				scheduleJob(job);
			}
		}
	}
	
	private void scheduleJob(ScheduledJob job) {
		try {
			Date now = Calendar.getInstance().getTime();
			
			//Job is disabled
			if (!job.isActive()) { 
				return;
			}
			
			//Job's lifetime about to start in future
			if (null != job.getStartTimestamp() && job.getStartTimestamp().after(now)) {
				System.out.println("STart date");
				return;
			}
			
			//Job's lifetime ended already
			if (null != job.getEndTimestamp() && job.getEndTimestamp().before(now)) {
				System.out.println("End date");
				return;
			}
			
			LOGGER.info("Scheduling new job instance: {} ",  job.getId());
			ScheduleHandler handler = (ScheduleHandler) Class.forName(job.getExecutionHandler()).newInstance();
			//Lambda that executes the actual task
			Runnable task = () -> {
				LOGGER.info("Executing Job {} with configuration [{}]", job.getId(), job.getExecutionHandlerConfig());
				handler.setConfig(job.getExecutionHandlerConfig());
				handler.execute();
			};
			ScheduledFuture<?> future = scheduleJob(job, task);
			
			scheduledJobs.put(job.getId(), job);
			futures.put(job.getId(), future);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.error("Invalid configuration: Execution Handler: {}", job.getExecutionHandler());
		}
	}
	
	private ScheduledFuture<?> scheduleJob(ScheduledJob job, Runnable task) {
		String schedule = job.getSchedule().toUpperCase().trim();
		if (schedule.startsWith("CRON:")) {
			return taskScheduler.schedule(task, new CronTrigger(job.getSchedule().substring("CRON:".length())));
		} else if (schedule.startsWith("FIXEDRATE:")) {
			return taskScheduler.scheduleAtFixedRate(task, Long.parseLong(job.getSchedule().substring("FIXEDRATE:".length())));
		} else if (schedule.startsWith("FIXEDDELAY:")) {
			return taskScheduler.scheduleWithFixedDelay(task, Long.parseLong(job.getSchedule().substring("FIXEDDELAY:".length())));
		} else {
			throw new RuntimeException("Invalid schedule configuration: " + job.getSchedule());
		}
	}
	
	private void killJob(ScheduledJob job, ScheduledFuture<?> future) {
		if (null != future && !future.isCancelled() && !future.isDone()) {
			LOGGER.info("Issuing kill signal to job: {} ", job.getId());
			future.cancel(false);
			futures.remove(job.getId());
		}
	}
}
