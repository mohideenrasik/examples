--changeset Rasik:1

CREATE TABLE shedlock (
	name VARCHAR(64) NOT NULL,
	lock_until TIMESTAMP(3) NOT NULL,
	locked_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
	locked_by VARCHAR(255) NOT NULL,
	PRIMARY KEY (name)
);

CREATE TABLE scheduled_job(
	id VARCHAR(100), 
	name VARCHAR(250) NOT NULL,
	execution_handler VARCHAR(1000) NOT NULL,
	execution_handler_config CLOB,
	schedule VARCHAR(250) NOT NULL,
	active BOOLEAN,
	start_timestamp TIMESTAMP,
	end_timestamp TIMESTAMP,
	lock_atleast_for_secs LONG,
	lock_atmost_for_secs LONG,
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
	lock_atleast_for_secs,
	lock_atmost_for_secs,
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
	15,
	60,
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
	lock_atleast_for_secs,
	lock_atmost_for_secs,
	version) 
VALUES (
	'PRINT_WEATHER_FIXED_DELAY', 
	'Print Weather (Fixed Delay)', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'FixedDelay:120000', 
	false,
	null,
	null,
	15,
	90, 
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
	lock_atleast_for_secs,
	lock_atmost_for_secs,
	version) 
VALUES (
	'PRINT_WEATHER_CRON', 
	'Print Weather (Cron)', 
	'com.example.spring.scheduling.domain.handler.HttpInvokerHandler', 
	'{"targetUrl":"https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22","httpMethod":"GET","requestHeaders":{"content-type":"plain/text"}}', 
	'Cron:0 0 8-17 * * MON-FRI', 
	false,
	null,
	null,
	null,
	null, 
	1);
	