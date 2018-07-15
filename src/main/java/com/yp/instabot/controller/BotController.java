package com.yp.instabot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {
	
	
	@RequestMapping("/")
	public String test() {
		return "Hello Instabot!";
	}
}
