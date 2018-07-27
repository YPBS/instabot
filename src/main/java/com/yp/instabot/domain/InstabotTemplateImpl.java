package com.yp.instabot.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
	private AtomicInteger successCount = new AtomicInteger();
	private AtomicInteger errorCount = new AtomicInteger();
	private AtomicInteger totalCount = new AtomicInteger();
	
	public InstabotTemplateImpl(ExecutorService executor, File file) {
		this.file = file;
		this.executor = executor;
		env = ApplicationContextHolder.getApplicationContextHolder().getEnvironment();
		
		// Instantiate driver based on whether vm argument of headless=true is provide
		if(env.getProperty("headless") != null && "true".equalsIgnoreCase(env.getProperty("headless"))) {
			ChromeOptions options = new ChromeOptions();
	        options.addArguments("headless");
	        driver = new ChromeDriver(options);
		}else {
			driver = new ChromeDriver();
		}
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

		usernameInput.sendKeys(env.getProperty("username"));
		passwordInput.sendKeys(env.getProperty("password"));
		loginButton.click();

		(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.linkText(env.getProperty("username"))));	// profilePictureElement

		boolean isLoggedIn = driver.findElement(By.linkText(env.getProperty("username"))) != null;

		if (!isLoggedIn) {
			log.error("Unable to login...");
			// TODO take snapshot of page and save screenshot for debugging to advice what went wrong
			throw new RuntimeException("Unable to login");
		} else {
			log.info("Successfully logged in...");
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
				
				// induce artificial delay between subsequent requests to avoid rate limiting
				if(env.getProperty("delay") != null) {
					try {
						Thread.sleep(Integer.parseInt(env.getProperty("delay")) * 1000);						
					}catch(Exception ex){
						log.warn("Unable to sleep for " + env.getProperty("delay") + " sec");
					}
				}
			}
		}
		
		if(driver != null)	driver.quit();
		
		log.info(" ***************** STATISTICS ************************");
		log.info("Total: " + totalCount.get() + ", Success:" + successCount.get() + ", Error: " + errorCount.get());
	}
	
	public void processAccount(String account) {
		driver.get("https://www.instagram.com/" + account);
		
		try {
			(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[contains(text(), '" + account + "')]")));
		}catch(Exception ex) {
			log.error(" Error occurred while searching for profile: " + account);
			return;
		}
			
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
		
		WebElement postsTextSpan = null;
		WebElement imagesDiv = null;
		List<WebElement> images = null;
		try {
			postsTextSpan = driver.findElement(By.xpath("//span[text() = 'Posts']"));
			imagesDiv = postsTextSpan.findElement(By.xpath("ancestor-or-self::div/following-sibling::div"));
			images = imagesDiv.findElements(By.tagName("img"));	
		}catch(Exception ex) {
			log.error("Error occurred while finding firs image", ex);
			return;
		}

		
		if(images != null && images.size() == 0) {
			log.error("No images for: " + account);
			errorCount.incrementAndGet();
			return;
		}
		
		// open first image
		WebElement element = images.get(0); 		// https://stackoverflow.com/questions/11908249/debugging-element-is-not-clickable-at-point-error
		Actions actions = new Actions(driver);
		actions.moveToElement(element).doubleClick().perform();
		
		// wait for first image to open wide and search for like button
		WebElement likeButton = null;
		try{
			likeButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[class^=glyphsSpriteHeart]"))); //"//span[@aria-label='Like'))]")));
			likeButton.click();
			log.info("Successfully liked image for: " + account);
			successCount.incrementAndGet();
		}catch(Exception ex) {
			errorCount.incrementAndGet();
			log.error("LIKE button not found on first imagefor : " + account, ex.getMessage());
		}
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
