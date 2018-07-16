package com.yp.instabot.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.yp.instabot.ApplicationContextHolder;
import com.yp.instabot.utils.Constants;

public class InstabotTemplateImpl extends InstabotTemplate {

	private static final Logger log = LoggerFactory.getLogger(InstabotTemplateImpl.class);

	private final File file;
	private WebDriver driver = null;
	private Environment env;
	private Set<String> accounts = null;
	private ExecutorService executor;
	private Semaphore sem = new Semaphore(10);
	private AtomicInteger successCount = new AtomicInteger();
	private AtomicInteger errorCount = new AtomicInteger();
	private AtomicInteger totalCount = new AtomicInteger();
	
	public InstabotTemplateImpl(ExecutorService executor, File file) {
		this.file = file;
		this.executor = executor;
		driver = new ChromeDriver();

		env = ApplicationContextHolder.getApplicationContextHolder().getEnvironment();
	}

	@Override
	public void login()  throws Exception {
		log.info("Initiating login...");
		driver.get(env.getProperty(Constants.INSTAGRAM.LOGIN.URL_LINK));
		driver.manage().window().maximize();
		
		WebElement passwordInput = (new WebDriverWait(driver, 10)).until(
				ExpectedConditions.presenceOfElementLocated(By.name(env.getProperty(Constants.INSTAGRAM.LOGIN.PASSWORD_INPUT_NAME))));

		WebElement usernameInput = driver.findElement(By.name(env.getProperty(Constants.INSTAGRAM.LOGIN.USERNAME_INPUT_NAME)));
		WebElement loginButton = driver.findElement(By.xpath("//*[contains(text(), 'Log in')]"));

		usernameInput.sendKeys(env.getProperty("userName"));
		passwordInput.sendKeys(env.getProperty("userPassword"));
		loginButton.click();

		WebElement profilePictureElement = (new WebDriverWait(driver, 10)).until(
				ExpectedConditions.presenceOfElementLocated(By.linkText(env.getProperty("userName"))));

		boolean isLoggedIn = driver.findElement(By.linkText(env.getProperty("userName"))) != null;

		if (!isLoggedIn) {
			log.error("Unable to login...");
			// TODO take snapshot of page and save screenshot for debugging to advice what went wrong
			throw new RuntimeException("Unable to login");
		} else {
			log.info("Successfully logged in...");
			readAccounts();
		}

	}

	@Override
	public void readAccounts() throws Exception {
		log.info("Starting file processing...");

		accounts = new LinkedHashSet<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("@")) {
					accounts.add(line.substring(1));
				} else {
					accounts.add(line);
				}
			}
			
			totalCount.set(accounts.size());
		}
	}

	@Override
	public void performLikes() throws Exception{
		log.info("Beginning likes...");
		
		if(accounts != null && !accounts.isEmpty()) {
			for(String account : accounts) {
				log.info("Opening page for " + account);
				processAccount(account);
				
				/*
				sem.acquire();
				
				executor.submit(new Runnable() {
					@Override
					public void run() {
						try {
							processAccount(account);
						} finally {
							sem.release();
						}
					}
				});*/
			}
		}
	}
	
	public void processAccount(String account) {
		driver.get("https://www.instagram.com/" + account);
		
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[contains(text(), '" + account + "')]")));
		
		if(driver.getTitle().contains("Page Not Found")) {
			log.error("Page not found for " + account);
			errorCount.incrementAndGet();
			return;
		}
		
		boolean privateAccountMessage = isElementPresent(By.xpath("//h2[contains(text(), 'This Account is Private')]"));
		if(privateAccountMessage) {
			log.error("Private Account issue for: " + account);
			errorCount.incrementAndGet();
			return;
		}
		
		List<WebElement> images = driver.findElements(By.tagName("img"));
		
		if(images != null && images.size() == 1) {
			log.error("No images for: " + account);
			errorCount.incrementAndGet();
			return;
		}
		
		// open first image
		WebElement element = images.get(1); 		// https://stackoverflow.com/questions/11908249/debugging-element-is-not-clickable-at-point-error
		Actions actions = new Actions(driver);
		actions.moveToElement(element).doubleClick().perform();
		
		// wait for first image to open wide and search for like button
		WebElement likeButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'Like')]")));
		
		likeButton.click();
		
		log.info("Successfully liked image for: " + account);
		successCount.incrementAndGet();
	}
	
	public boolean isElementPresent(By locatorKey) {
	    try {
	        driver.findElement(locatorKey);
	        return true;
	    } catch (org.openqa.selenium.NoSuchElementException e) {
	        return false;
	    }
	}


}
