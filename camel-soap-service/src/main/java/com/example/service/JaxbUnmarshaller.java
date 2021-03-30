package com.example.service;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;

import com.example.ws.product_service.types.ProductResponse;

public class JaxbUnmarshaller {

	public void unmarshall(Exchange exchange) throws Exception {
		String operationName = (String) exchange.getIn().getHeader("operationName");
		System.out.println("Building service response of operations " + operationName + " [" + exchange.getIn().getBody() + "]" );
		
		String xmlContext = exchange.getIn().getBody(String.class);
		xmlContext = getDummyResponse();
		JAXBContext context = JAXBContext.newInstance(ProductResponse.class);
		exchange.getIn().setBody(context.createUnmarshaller().unmarshal(new StringReader(xmlContext)));
	}
	
	private String getDummyResponse() throws Exception {
		ProductResponse result = new ProductResponse();
		result.setId("1");
		result.setDescription("Some Description");
		result.setPrice(100);
		JAXBContext context = JAXBContext.newInstance(ProductResponse.class);
		StringWriter writer = new StringWriter();
		context.createMarshaller().marshal(result, writer);
		return writer.toString();
	}
}
