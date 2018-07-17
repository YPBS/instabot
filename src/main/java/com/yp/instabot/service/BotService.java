package com.yp.instabot.service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yp.instabot.domain.InstabotTemplate;
import com.yp.instabot.domain.InstabotTemplateImpl;

@Service
public class BotService {

	private static final Logger log = LoggerFactory.getLogger(BotService.class);
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	public void doWork(File file) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				InstabotTemplate instabot = new InstabotTemplateImpl(executor, file);
				try {
					instabot.work();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					long end = System.currentTimeMillis();
					log.info("Total time taken for processing: " + (end-start) + " ms.");
				}
			}
		});
	}
}
