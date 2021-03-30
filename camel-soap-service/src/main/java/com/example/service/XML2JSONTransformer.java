package com.example.service;

import org.apache.camel.Exchange;

public class XML2JSONTransformer {

	public void transform(Exchange exchange) {
		String operationName = (String) exchange.getIn().getHeader("operationName");
		System.out.println("Transforming XML payload of operations " + operationName + " [" + exchange.getIn().getBody() + "]");
		
		// Use the transformer tool to transform the XML to JSON
		
		exchange.getIn().setBody("{response: 'ok'}");
	}
}
