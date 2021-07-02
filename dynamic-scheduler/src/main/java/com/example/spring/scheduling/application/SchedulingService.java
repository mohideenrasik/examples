package com.example.spring.scheduling.application;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.example.spring.scheduling.domain.ScheduleHandler;
import com.example.spring.scheduling.domain.ScheduledJob;
import com.example.spring.scheduling.domain.ScheduledJobRepository;

import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;


@Service
public class SchedulingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingService.class);

	private Map<String, ScheduledJob> scheduledJobs = new HashMap<String, ScheduledJob>();
	private Map<String, ScheduledFuture<?>> futures = new HashMap<String, ScheduledFuture<?>>();
	
	@Value("${scheduler.threadPoolSize}")
	private int threadPoolSize;
	
	@Value("${scheduler.pollingIntervalMins}")
	private int pollingIntervalMins;
	
	@Value("${scheduler.lockAtleastForSecs}")
	private int lockAtleastForSecs;
	
	@Value("${scheduler.lockAtmostForSecs}")
	private int lockAtmostForSecs;
	
	@Autowired
	private TaskScheduler taskScheduler;
	
	@Autowired
	private ScheduledJobRepository repository;
	
	private LockingTaskExecutor shedLockTaskExecutor;
	
	@Autowired
	private LockProvider lockProvider;
	
	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(dataSource);
	}
	
	@Bean
	public TaskScheduler taskScheduler() {
		//Task scheduler with underlying executor of specified thread pool size
		return new ConcurrentTaskScheduler(new ScheduledThreadPoolExecutor(threadPoolSize));
	}
	
	@PostConstruct
	private void loadScheduledJobs() {
		//Shedlock Task executor that would wrap the task with locking aspect
		shedLockTaskExecutor = new DefaultLockingTaskExecutor(lockProvider);
		
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
				jobDefinitionUpdated = job.getVersion() != scheduledJobs.get(job.getId()).getVersion(); 
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
			ScheduledFuture<?> future = scheduleJobForExecution(job, wrapTask(job));
			
			scheduledJobs.put(job.getId(), job);
			futures.put(job.getId(), future);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.error("Invalid configuration: Execution Handler: {}", job.getExecutionHandler());
		}
	}
	
	private Runnable wrapTask(ScheduledJob job) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		ScheduleHandler handler = (ScheduleHandler) Class.forName(job.getExecutionHandler()).newInstance();
		
		//Lambda that executes the actual task
		return () -> {
			LOGGER.info("Executing Job {} with configuration [{}]", job.getId(), job.getExecutionHandlerConfig());
			Duration lockAtleastFor = Duration.ofSeconds(lockAtleastForSecs);
			if (null != job.getLockAtleastForSecs()) {
				lockAtleastFor = Duration.ofSeconds(job.getLockAtleastForSecs());
			}
			Duration lockAtmostFor = Duration.ofSeconds(lockAtmostForSecs);
			if (null != job.getLockAtmostForSecs()) {
				lockAtmostFor = Duration.ofSeconds(job.getLockAtmostForSecs());
			}
			LockConfiguration lockConfig = new LockConfiguration(Instant.now(), job.getId(), lockAtmostFor, lockAtleastFor);
			
			Runnable lockWrappedTask = () -> {
				handler.setConfig(job.getExecutionHandlerConfig());
				handler.execute();
			};
			shedLockTaskExecutor.executeWithLock(lockWrappedTask, lockConfig);
		};
	}
	
	private ScheduledFuture<?> scheduleJobForExecution(ScheduledJob job, Runnable task) {
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
