package com.yp.instabot;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAutoConfiguration(exclude={MultipartAutoConfiguration.class})
@PropertySource("classpath:application.properties")
public class InstabotApplication {
	
	private static final Logger log = LoggerFactory.getLogger(InstabotApplication.class);
	
	public static void main(String[] args) {
		
		Thread.setDefaultUncaughtExceptionHandler( (t, e) ->{
			log.error("Uncaught exception: " + e.getMessage(), e);
		});
		SpringApplication.run(InstabotApplication.class, args);
	}
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(apiInfo());
    }
	
	private ApiInfo apiInfo() {
	     return new ApiInfo(
	       "Instabot REST API", 
	       "Invoke autolikes for list of accounts", 
	       "API TOS", 
	       "Terms of service", 
	       null, 
	       "License of API", "API license URL", Collections.emptyList());
	}
	
}
