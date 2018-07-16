package com.yp.instabot.service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.yp.instabot.domain.InstabotTemplate;
import com.yp.instabot.domain.InstabotTemplateImpl;

@Service
public class BotService {

	private ExecutorService executor = Executors.newFixedThreadPool(11);

	public void doWork(File file) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				InstabotTemplate instabot = new InstabotTemplateImpl(executor, file);
				try {
					instabot.work();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
