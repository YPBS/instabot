package com.yp.instabot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yp.instabot.service.BotService;

@RestController
public class BotController {
	
	@Autowired
	private BotService botService;
	
	@RequestMapping("/index.html")
	public String index() {
		return "Hello Instabot!";
	}
	
	@RequestMapping(value="/work", method=RequestMethod.POST)
	public void doWork() {
		botService.doWork();
	}
}
