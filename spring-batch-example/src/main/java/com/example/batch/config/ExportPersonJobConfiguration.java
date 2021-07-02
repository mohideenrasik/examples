package com.example.batch.config;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.example.batch.model.Person;

@Configuration
public class ExportPersonJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;

	@Bean
	public ItemReader<Person> exportPersonReader() {
		return new JdbcCursorItemReaderBuilder<Person>()
				.name("personItemReader")
				.dataSource(dataSource)
				.sql("SELECT * FROM people ORDER BY first_name")
				.rowMapper(new BeanPropertyRowMapper<Person>(Person.class))
				.build();
	}
		
	@Bean
	public ItemWriter<Person> exportPersonWriter() {
		return new ItemWriter<Person>() {

			@Override
			public void write(List<? extends Person> items) throws Exception {
				for (Person person: items) {
					System.out.println(person);
				}
			}
		};
	}
	
	@Bean
	public Step exportPersonStep1() {
		return stepBuilderFactory.get("Step1")
				.<Person, Person> chunk(2)
				.reader(exportPersonReader())
				.writer(exportPersonWriter())
				.build();
	}
	
	@Bean
	public Job exportPersonJob() {
		return jobBuilderFactory.get("exportPersonJob")
				.incrementer(new RunIdIncrementer())
				.flow(exportPersonStep1())
				.end()
				.build();
	}
}
