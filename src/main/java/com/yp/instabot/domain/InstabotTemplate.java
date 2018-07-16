package com.yp.instabot.domain;

public abstract class InstabotTemplate {

	public final void work() throws Exception {
		login();
	    readAccounts();
	    performLikes();
	}
	
	public abstract void login() throws Exception;
    public abstract void readAccounts() throws Exception;
    public abstract void performLikes() throws Exception;
}
