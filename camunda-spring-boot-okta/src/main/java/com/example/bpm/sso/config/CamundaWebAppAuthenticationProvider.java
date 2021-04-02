package com.example.bpm.sso.config;

import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CamundaWebAppAuthenticationProvider extends ContainerBasedAuthenticationProvider {

	@Override
	public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication) {
			return AuthenticationResult.unsuccessful();
		}
		
		String name = authentication.getName();
		if (null == name || name.isEmpty()) {
			return AuthenticationResult.unsuccessful();
		}
		
		AuthenticationResult result = new AuthenticationResult(CamundaIdenityHelper.getUserId(authentication), true);
		result.setGroups(CamundaIdenityHelper.getAuthorities(authentication, engine));
		return result;
	}
	
	
}
