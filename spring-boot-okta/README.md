# Spring Boot OKTA Example

This application demonstrate the integration with OKTA with Spring boot. 

Prerequisite:
* Create a developer account in okta https://developer.okta.com
* Create a group and some users in OKTA
* Create an OpenID Connect application and assign the group to the application
* Add an additional claim named "groups" under the authorization server in OKTA. The claim should be added to be part of the ID token so that the spring boot security can get the group details and use it as authorities 
