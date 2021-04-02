package com.example.bpm.sso.interfaces.web.controller;

import java.util.Collections;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {


	@RequestMapping("/securedPage")
	public ModelAndView securedPage(OAuth2AuthenticationToken authentication) {
		return new ModelAndView("securedPage" , 
				Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
	}
	
	@RequestMapping("/adminPage")
	@ResponseBody
	@PreAuthorize("hasAuthority('ops-users')")
	public String adminPage(@AuthenticationPrincipal OidcUser user) {
		return "<h1>Hello Administrator!! " + user.getFullName() + "</h1>";
	}
}
