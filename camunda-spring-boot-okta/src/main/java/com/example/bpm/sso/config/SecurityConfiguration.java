package com.example.bpm.sso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**").authorizeRequests()
			.antMatchers("/login/**").permitAll()
			.antMatchers("/oauth2/**").permitAll()
			.anyRequest().authenticated()
     		.and().oauth2Login()
     		.and().csrf().ignoringAntMatchers("/logout").csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()); //.disable();
	}
}
