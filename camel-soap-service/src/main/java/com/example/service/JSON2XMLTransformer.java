package com.example.service;

import org.apache.camel.Exchange;

public class JSON2XMLTransformer {

	public void transform(Exchange exchange) {
		String operationName = (String) exchange.getIn().getHeader("operationName");
		System.out.println("Transforming JSON payload of operations " + operationName + " [" + exchange.getIn().getBody(String.class) + "]");

		// Use the transformer tool to transform the JSON to XML
		
		exchange.getIn().setBody("<response>OK</response>");
	}
}
