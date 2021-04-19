package com.example.bpm.sso.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class CamundaIdenityHelper {

	private static final String CAMUNDA_GROUP_PREFIX = "camunda-";
	private static final String CAMUNDA_AUTO_CREATE_GROUP_SUFFIX = "auto-create";
	
	public static List<String> getAuthorities(Authentication authentication, ProcessEngine engine) {
		//Camunda Cockpit app requires user profile to available in Camunda store
		//Tasklist and Admin do not require the user profile to be present in camunda store
		
		final List<String> groupIds = new ArrayList<>();
		if (autoCreateCamundaCredentials(authentication)) {
			createCamundaUserResources(authentication, engine);
			String userId = getUserId(authentication);
			engine.getIdentityService().createGroupQuery().groupMember(userId).list()
		            .forEach( g -> groupIds.add(g.getId()));
		} else {
			createCamundaUserIfNotExists(engine, authentication);
			groupIds.addAll(getCamundaGroups(authentication));
		}
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
	
	private static void createCamundaUserResources(Authentication authentication, ProcessEngine engine) {
		createCamundaUserIfNotExists(engine, authentication);
		createCamundaGroupsForUser(engine, getCamundaGroups(authentication), getUserId(authentication));
	}
	
	private static List<String> getCamundaGroups(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.map(res -> res.getAuthority())
				.filter(res -> res.startsWith(CAMUNDA_GROUP_PREFIX))
				.map(res -> res.equalsIgnoreCase(Groups.CAMUNDA_ADMIN) ? res : res.substring(CAMUNDA_GROUP_PREFIX.length()))
				.collect(Collectors.toList()); 
	}

	private static void createCamundaGroupsForUser(ProcessEngine engine, List<String> groups, String userId) {
		IdentityService identityService = engine.getIdentityService();
		for (String group: groups) {
			if (group.equalsIgnoreCase(CAMUNDA_AUTO_CREATE_GROUP_SUFFIX)) {
				continue;
			}
			
			createCamundaGroupIfNotExists(engine, group, group); 
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
	
	public static String getUserName(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OidcUser) {
			username = ((OidcUser) principal).getFullName();
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
	
	public static Group createCamundaGroupIfNotExists(ProcessEngine engine, String groupId, String groupName) {
		IdentityService identityService = engine.getIdentityService();
		Group camundaGroup = identityService.createGroupQuery().groupId(groupId).singleResult();
		if (null == camundaGroup) {
			camundaGroup = identityService.newGroup(groupId);
			if (null == groupName || groupName.isEmpty()) {
				camundaGroup.setName(groupId);
			} else {
				camundaGroup.setName(groupName);
			}
			
			camundaGroup.setType(Groups.GROUP_TYPE_WORKFLOW);
			identityService.saveGroup(camundaGroup);
		}
		return camundaGroup;
	}
	
	public static Authorization addPrivilegeToGroup(ProcessEngine engine, String groupId, 
			Resource resource, String resourceId, Permission[] permissions) {
		AuthorizationService authService = engine.getAuthorizationService();
		Authorization authorization = authService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
		authorization.setGroupId(groupId);
		authorization.setResource(resource);
		authorization.setResourceId(resourceId);
		authorization.setPermissions(permissions);
		authService.saveAuthorization(authorization);
		return authorization;
	}
	
	public static void createCamundaUserIfNotExists(ProcessEngine engine, Authentication authentication) {
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
	}
}
