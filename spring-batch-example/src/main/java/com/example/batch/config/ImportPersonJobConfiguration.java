package com.example.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.batch.model.Person;

@Configuration
public class ImportPersonJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public ItemReader<Person> importPersonReader() {
		return new FlatFileItemReaderBuilder<Person>()
				.name("personItemReader")
				.resource(new ClassPathResource("data/person-data.csv"))
				.delimited()
				.names(new String[] {"firstName", "lastName"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
						setTargetType(Person.class);
				}})
				.build();
	}
	
	@Bean
	public ItemProcessor<Person, Person> importPersonProcessor() {
		return new ItemProcessor<Person, Person>() {
			public Person process(Person input) {
				return new Person(input.getFirstName().toUpperCase(), input.getLastName().toUpperCase());
			}
		};
	}
	
	@Bean 
	public ItemWriter<Person> importPersonWriter() {
		return new JdbcBatchItemWriterBuilder<Person>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>())
				.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
				.dataSource(dataSource)
				.build();
	}
	
	@Bean 
	public Step importPersonStep1() {
		return stepBuilderFactory.get("Step1")
				.<Person, Person> chunk(2)
				.reader(importPersonReader())
				.processor(importPersonProcessor())
				.writer(importPersonWriter())
				.build();
	}
	
	@Bean
	public Job importPersonJob() {
		return jobBuilderFactory.get("importPersonJob")
				.incrementer(new RunIdIncrementer())
				.flow(importPersonStep1())
				.end()
				.build();
	}
}
