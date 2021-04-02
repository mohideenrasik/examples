package com.example.bpm.sso.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class CamundaIdenityHelper {

	private static final String CAMUNDA_GROUP_PREFIX = "camunda-";
	private static final String CAMUNDA_ADMIN_GROUP_SUFFIX = "admin";
	private static final String CAMUNDA_AUTO_CREATE_GROUP_SUFFIX = "auto-create";
	
	public static List<String> getAuthorities(Authentication authentication, ProcessEngine engine) {
		if (autoCreateCamundaCredentials(authentication)) {
			createCamundaUserIfNotExists(authentication, engine);
		}
		
		String userId = getUserId(authentication);
		List<String> groupIds = new ArrayList<>();
		engine.getIdentityService().createGroupQuery().groupMember(userId).list()
	            .forEach( g -> groupIds.add(g.getId()));
		return groupIds;
	}
	
	private static boolean autoCreateCamundaCredentials(Authentication authentication) {
		boolean result = false;
		for (GrantedAuthority authority: authentication.getAuthorities()) {
			if (authority.getAuthority().equalsIgnoreCase(CAMUNDA_GROUP_PREFIX + CAMUNDA_AUTO_CREATE_GROUP_SUFFIX)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	private static void createCamundaUserIfNotExists(Authentication authentication, ProcessEngine engine) {
		String userId = getUserId(authentication);
		IdentityService identityService = engine.getIdentityService(); 
		User user = identityService.createUserQuery().userId(userId).singleResult();
		if (null == user) {
			User newUser = engine.getIdentityService().newUser(userId);
			newUser.setFirstName(getClaim(authentication, "given_name"));
			newUser.setLastName(getClaim(authentication, "family_name"));
			newUser.setEmail(getClaim(authentication, "email"));
		    identityService.saveUser(newUser);
		}
		
		List<String> groups = authentication.getAuthorities().stream()
				.map(res -> res.getAuthority())
				.filter(res -> res.startsWith(CAMUNDA_GROUP_PREFIX))
				.map(res -> res.substring(CAMUNDA_GROUP_PREFIX.length()))
				.collect(Collectors.toList());
		createCamundaGroupsForUser(engine, groups, userId);
	}
	
	private static void createCamundaGroupsForUser(ProcessEngine engine, List<String> groups, String userId) {
		IdentityService identityService = engine.getIdentityService();
		for (String group: groups) {
			if (group.equalsIgnoreCase(CAMUNDA_AUTO_CREATE_GROUP_SUFFIX)) {
				continue;
			}
			
			if (group.equalsIgnoreCase(CAMUNDA_ADMIN_GROUP_SUFFIX)) {
				group = Groups.CAMUNDA_ADMIN;
			} 
			
			Group camundaGroup = identityService.createGroupQuery().groupId(group).singleResult();
			if (null == camundaGroup) {
				camundaGroup = identityService.newGroup(group);
				camundaGroup.setName(group);
				camundaGroup.setType(Groups.GROUP_TYPE_WORKFLOW);
				identityService.saveGroup(camundaGroup);
			}
			if ( 0 == identityService.createGroupQuery().groupMember(userId).count()) {
				identityService.createMembership(userId, group);
			}
		}
	}
	
	public static String getUserId(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OidcUser) {
			username = ((OidcUser) principal).getName();
		} else {
			username = principal.toString();
		}
		return username;
	}
	
	private static String getClaim(Authentication authentication, String claimName) {
		Object principal = authentication.getPrincipal();
		String result = null;
		if (principal instanceof UserDetails) {
			if (claimName.endsWith("name")) {
				result = ((UserDetails) principal).getUsername();	
			}
		} else if (principal instanceof OidcUser) {
			result = (String) ((OidcUser) principal).getAttributes().get(claimName);
		}
		return result;
	}
}
