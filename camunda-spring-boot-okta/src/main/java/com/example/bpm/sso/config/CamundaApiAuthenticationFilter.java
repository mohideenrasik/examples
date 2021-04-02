package com.example.bpm.sso.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.util.EngineUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CamundaApiAuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig config) {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
				throws IOException, ServletException {
		
		ProcessEngine engine = EngineUtil.lookupProcessEngine("default");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		try {
			engine.getIdentityService().setAuthentication(CamundaIdenityHelper.getUserId(authentication), 
					CamundaIdenityHelper.getAuthorities(authentication, engine));
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clearAuthentication(engine);
		}
	}
	
	@Override
	public void destroy() {
		
	}
	
	private void clearAuthentication(ProcessEngine engine) {
		engine.getIdentityService().clearAuthentication();
	}
}
