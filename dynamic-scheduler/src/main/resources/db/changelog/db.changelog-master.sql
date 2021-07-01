--changeset Rasik:1

CREATE TABLE scheduled_job(
	id VARCHAR(100), 
	name VARCHAR(250) NOT NULL,
	execution_handler VARCHAR(1000) NOT NULL,
	execution_handler_config CLOB,
	schedule VARCHAR(250) NOT NULL,
	active BOOLEAN,
	version INTEGER,
	PRIMARY KEY (id));
	
INSERT INTO scheduled_job (
	id, 
	name, 
	execution_handler, 
	execution_handler_config, 
	schedule, 
	active, 
	version) 
VALUES (
	'PRINT_WEATHER', 
	'Print Weather', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'FixedRate:10000', 
	true, 
	1);
	
INSERT INTO scheduled_job (
	id, 
	name, 
	execution_handler, 
	execution_handler_config, 
	schedule, 
	active, 
	version) 
VALUES (
	'PING_GOOGLE', 
	'Ping Google', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"http://www.google.com","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'FixedRate:15000', 
	true, 
	1);