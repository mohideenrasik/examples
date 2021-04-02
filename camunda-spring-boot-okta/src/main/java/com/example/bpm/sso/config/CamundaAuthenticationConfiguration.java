package com.example.bpm.sso.config;

import java.util.Collections;

import javax.servlet.Filter;

import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaAuthenticationConfiguration {

	@Bean
	public FilterRegistrationBean<Filter> camundaApiAuthenticationFilter() {
		FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<Filter>();
		filterRegistration.setFilter(new CamundaApiAuthenticationFilter());
		filterRegistration.setOrder(102);
		filterRegistration.addUrlPatterns("/camunda/api/*");
		return filterRegistration;
	}

	@Bean
	public FilterRegistrationBean<Filter> camundaContainerBasedAuthenticationFilter() {
		FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<Filter>();
		filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
		filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider",
				CamundaWebAppAuthenticationProvider.class.getName()));
		filterRegistration.setOrder(101);
		filterRegistration.addUrlPatterns("/camunda/app/*");
		return filterRegistration;
	}
}
