package com.example.spring.scheduling.domain;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ScheduledJob {

	@Id	
	private String id;
	private String name;
	private String executionHandler;
	private String executionHandlerConfig;
	private String schedule;
	private boolean active;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private int version;
}
