package com.example.service;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.cxf.message.MessageContentsList;

public class JaxbMarshaller {

	public void marshall(Exchange exchange) throws Exception {
		MessageContentsList body = (MessageContentsList) exchange.getIn().getBody();
		Object jaxbObject = body.get(0);
		JAXBContext context = JAXBContext.newInstance(jaxbObject.getClass());
		StringWriter writer = new StringWriter();
		context.createMarshaller().marshal(jaxbObject, writer);
		exchange.getIn().setBody(writer.toString());
	}
}
