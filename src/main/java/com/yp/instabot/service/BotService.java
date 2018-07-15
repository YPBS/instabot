package com.yp.instabot.service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.yp.instabot.domain.InstabotTemplate;
import com.yp.instabot.domain.InstabotTemplateImpl;

@Service
public class BotService {

	public void doWork(File file) {
		InstabotTemplate instabot = new InstabotTemplateImpl(file);
		instabot.work();
	}
}
