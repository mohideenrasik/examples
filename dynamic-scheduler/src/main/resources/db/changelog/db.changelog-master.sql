--changeset Rasik:1

CREATE TABLE scheduled_job(
	id VARCHAR(100), 
	name VARCHAR(250) NOT NULL,
	execution_handler VARCHAR(1000) NOT NULL,
	execution_handler_config CLOB,
	schedule VARCHAR(250) NOT NULL,
	active BOOLEAN,
	start_timestamp TIMESTAMP,
	end_timestamp TIMESTAMP,
	version INTEGER,
	PRIMARY KEY (id));
	
INSERT INTO scheduled_job (
	id, 
	name, 
	execution_handler, 
	execution_handler_config, 
	schedule, 
	active, 
	start_timestamp,
	end_timestamp,
	version) 
VALUES (
	'PRINT_WEATHER_FIXED_RATE', 
	'Print Weather (Fixed Rate)', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'FixedRate:60000', 
	true,
	{ts '2021-01-01 00:00:00.000'},
	{ts '2022-12-31 23:59:59.999'}, 
	1);

INSERT INTO scheduled_job (
	id, 
	name, 
	execution_handler, 
	execution_handler_config, 
	schedule, 
	active, 
	start_timestamp,
	end_timestamp,
	version) 
VALUES (
	'PRINT_WEATHER_FIXED_DELAY', 
	'Print Weather (Fixed Delay)', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'FixedDelay:120000', 
	true,
	null,
	null, 
	1);

INSERT INTO scheduled_job (
	id, 
	name, 
	execution_handler, 
	execution_handler_config, 
	schedule, 
	active, 
	start_timestamp,
	end_timestamp,
	version) 
VALUES (
	'PRINT_WEATHER_CRON', 
	'Print Weather (Cron)', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'Cron:0 0 8-17 * * MON-FRI', 
	true,
	null,
	null, 
	1);
	