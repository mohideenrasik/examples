package com.example.spring.scheduling.domain;

public interface ScheduleHandler {

	void setConfig(String config);
	void execute();
}
