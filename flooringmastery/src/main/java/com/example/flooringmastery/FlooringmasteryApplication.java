package com.example.flooringmastery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.controller.Controller;

@SpringBootApplication
public class FlooringmasteryApplication {

	public static void main(String[] args) {
		//SpringApplication.run(FlooringmasteryApplication.class, args);
		 ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		 Controller controller = ctx.getBean("controller", Controller.class);
		 controller.run();
	}

}
