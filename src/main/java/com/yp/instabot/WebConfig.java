package com.yp.instabot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	
	// https://stackoverflow.com/questions/22723871/required-multipartfile-parameter-file-is-not-present/29411814#
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
	    CommonsMultipartResolver multipartResolver 
	            = new CommonsMultipartResolver();
	    return multipartResolver;
	}

}
