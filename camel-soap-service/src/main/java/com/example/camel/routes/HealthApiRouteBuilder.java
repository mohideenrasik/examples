package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HealthApiRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration()
			.enableCORS(true)
			.corsAllowCredentials(true)
			.corsHeaderProperty("Access-Control-Allow-Origin","*")
			.corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
		
		rest("/health")
			.get()
				.description("Health Check API")
				.responseMessage()
					.code(200).message("Service is healthy")
				.endResponseMessage()
				.route().routeId("Health")
				.transform().constant("OK"); 
	}
}