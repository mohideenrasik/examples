package com.example.spring.scheduling.domain;

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
	private int version;
}
