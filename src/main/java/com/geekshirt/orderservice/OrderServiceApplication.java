package com.geekshirt.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	/*
	BD en memoria (la ruta h2-console se configura en application.properties)
	http://localhost:8080/api/v1/h2-console

	Swagger
	http://localhost:8080/api/v1/swagger-ui.html#/

	Rabbitmq
	Se levanta desde docker
	http://localhost:15672/#/queues

	No me funciona la dependencia, es un html para ver mas ordenado el health checker
	La ruta deberia ser http://localhost:8080/api/v1/browser/index.html
	compile 'org.springframework.data:spring-data-rest-hal-browser:3.0.8.RELEASE'

	 */

}
