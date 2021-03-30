# Exposing SOAP Service using Apache Camel

You can start the application using start.bat or start.sh based on your application.

Once the application started successfully you can use the below urls to interact with the applications

http://localhost:8080/api/health - Health check REST endpoint exposed by a camel route <br/>
http://localhost:8080/api/api-doc - Swagger Api documentation url <br/>
http://localhost:8080/actuator - Spring boot actuator endpoint <br/>
http://localhost:8080/actuator/hawtio/console - Hawtio console to manage and monitor Camel engine <br/>

http://localhost:9091/productService?wsdl - WSDL file of the webservice exposed throug Camel