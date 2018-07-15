package com.yp.instabot.service;

import org.springframework.stereotype.Service;

import com.yp.instabot.domain.InstabotTemplate;
import com.yp.instabot.domain.InstabotTemplateImpl;

@Service
public class BotService {

	public void doWork() {
		InstabotTemplate instabot = new InstabotTemplateImpl();
		instabot.work();
	}
}
