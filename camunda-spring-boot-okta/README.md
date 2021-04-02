# Spring Boot OKTA Camunda Example

This application demonstrate the integration with OKTA with Spring boot that hosts Camunda Process Engine. OKTA is been used to authenticate the user, upon the successful authentication camunda looks for the profile information in camunda managed data store so it is important to have the user profile for the user id and the associated group details present in camunda identity service. This application auto create camunda users and groups for specific cases. 

Prerequisite:
* Create a developer account in okta https://developer.okta.com
* Create groups suffixed with "camunda-" to find mapping in Camunda identity service
* "camunda-auto-create" is a special group a user part of the group have the privilege to autocreate himself and this roles in camunda when he first logsin
* Create an OpenID Connect application and assign the group to the application
* Add an additional claim named "groups" under the authorization server in OKTA. The claim should be added to be part of the ID token so that the spring boot security can get the group details and use it as authorities 
