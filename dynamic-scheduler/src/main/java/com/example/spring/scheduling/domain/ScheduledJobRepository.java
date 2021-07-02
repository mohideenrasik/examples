package com.example.spring.scheduling.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, String>{

}
