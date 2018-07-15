package com.yp.instabot.domain;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.yp.instabot.ApplicationContextHolder;
import com.yp.instabot.utils.Constants;

public class InstabotTemplateImpl extends InstabotTemplate {
	
	private static final Logger log = LoggerFactory.getLogger(InstabotTemplateImpl.class);
	
	private ExecutorService executor = Executors.newFixedThreadPool(11);
	private final File file;
	private WebDriver driver = new FirefoxDriver();
	private Environment env;
	
	public InstabotTemplateImpl(File file){
		this.file = file;
		env = ApplicationContextHolder.getApplicationContextHolder().getEnvironment();
	}
	
	@Override
	public void login() {
		executor.submit(new Runnable() {
			@Override
			public void run(){
				log.info("Initiating login...");
				driver.get(env.getProperty(Constants.INSTAGRAM.LOGIN.URL_LINK));
				
				WebElement passwordInput = (new WebDriverWait(driver, 10))
						  .until(ExpectedConditions.presenceOfElementLocated(By.name(Constants.INSTAGRAM.LOGIN.PASSWORD_INPUT_NAME)));

				WebElement usernameInput = driver.findElement(By.name(Constants.INSTAGRAM.LOGIN.USERNAME_INPUT_NAME));
				WebElement loginButton  = driver.findElement(By.xpath("//*[contains(text(), 'Log in')]"));
				
				usernameInput.sendKeys(env.getProperty(Constants.INSTAGRAM.LOGIN.USERNAME_INPUT_NAME));
				passwordInput.sendKeys(Constants.INSTAGRAM.LOGIN.PASSWORD_INPUT_NAME);
				loginButton.click();
				
				WebElement profilePictureElement = (new WebDriverWait(driver, 10))
						  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'profile picture')]")));
				
				boolean isLoggedIn = Constants.INSTAGRAM.LOGIN.HOMEPAGE_URL.equalsIgnoreCase(driver.getCurrentUrl());
				
				if(!isLoggedIn) {
					log.error("Unable to login...");
					//TODO take snapshot of page and save screenshot for debugging to advice what went wrong
					throw new RuntimeException("Unable to login");
				}else {
					log.info("Successfully logged in...");
					readAccounts();
				}				
			}
			
		});
	}

	@Override
	public void readAccounts() {
		log.info("Starting file processing...");
	}

	@Override
	public void performLikes() {
		
	}

}
