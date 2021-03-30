package com.example.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfComponent;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.springframework.stereotype.Component;

import com.example.service.JaxbMarshaller;
import com.example.service.JaxbUnmarshaller;
import com.example.service.JSON2XMLTransformer;
import com.example.service.XML2JSONTransformer;
import com.example.ws.product_service.Product;

@Component
public class ProductServiceRoutBuilder extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		CxfComponent cxfComponent = new CxfComponent(getContext());
	    CxfEndpoint cxfEndpoint = new CxfEndpoint("http://localhost:9091/productService", cxfComponent);
	    cxfEndpoint.setServiceClass(Product.class);		    		
		getContext().getRegistry().bind("productServiceEndpoint", cxfEndpoint);
		
		from("cxf:bean:productServiceEndpoint")
			.bean(JaxbMarshaller.class)
			.bean(XML2JSONTransformer.class)
			.removeHeader(Exchange.HTTP_URI)
			.toD("http://localhost:8080/api/health?bridgeEndpoint=true&httpMethod=GET")
			.bean(JSON2XMLTransformer.class)
			.bean(JaxbUnmarshaller.class)
			;
	}

}
