package com.yp.instabot.domain;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InstabotTemplateImpl extends InstabotTemplate {
	
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	@Override
	public void login() {
		executor.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				
				return null;
			}
			
		});
	}

	@Override
	public void readAccounts() {
		
	}

	@Override
	public void performLikes() {
		
	}

}
