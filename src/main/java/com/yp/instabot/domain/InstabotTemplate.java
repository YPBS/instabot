package com.yp.instabot.domain;

public abstract class InstabotTemplate {

	public final void work() {
		login();
	    readAccounts();
	    performLikes();
	}
	
	public abstract void login();
    public abstract void readAccounts();
    public abstract void performLikes();
}
