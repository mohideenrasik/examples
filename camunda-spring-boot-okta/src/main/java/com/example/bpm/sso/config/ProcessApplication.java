package com.example.bpm.sso.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessApplication {
	
	@EventListener
	private void processPostDeploy(PostDeployEvent event) {
		ProcessEngine engine = event.getProcessEngine();
		
		CamundaIdenityHelper.createCamundaGroupIfNotExists(engine, "opsusers", "Operations Users");
		addCommonPrivileges(engine, "opsusers", "cockpit");
		
		CamundaIdenityHelper.createCamundaGroupIfNotExists(engine, "businessusers", "Business Users");
		addCommonPrivileges(engine, "businessusers", "tasklist");
	}
	
	private void addCommonPrivileges(ProcessEngine engine, String groupId, String application) {
		CamundaIdenityHelper.addPrivilegeToGroup(engine, groupId, Resources.APPLICATION, application, new Permission[] {Permissions.ALL});
		CamundaIdenityHelper.addPrivilegeToGroup(engine, groupId, Resources.PROCESS_DEFINITION, "*", new Permission[] {Permissions.ALL});
		CamundaIdenityHelper.addPrivilegeToGroup(engine, groupId, Resources.PROCESS_INSTANCE, "*", new Permission[] {Permissions.ALL});
		CamundaIdenityHelper.addPrivilegeToGroup(engine, groupId, Resources.FILTER, "*", new Permission[] {Permissions.ALL});
	}
}