package com.yp.instabot.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
	
	public InstabotTemplateImpl(ExecutorService executor, File file) {
		this.file = file;
		this.executor = executor;
		// FirefoxProfile fProfile = new FirefoxProfile();
		// fProfile.setPreference("javascript.enabled", false);
		driver = new ChromeDriver();

		env = ApplicationContextHolder.getApplicationContextHolder().getEnvironment();
	}

	@Override
	public void login() {
		log.info("Initiating login...");
		driver.get(env.getProperty(Constants.INSTAGRAM.LOGIN.URL_LINK));

		WebElement passwordInput = (new WebDriverWait(driver, 10)).until(
				ExpectedConditions.presenceOfElementLocated(By.name(env.getProperty(Constants.INSTAGRAM.LOGIN.PASSWORD_INPUT_NAME))));

		WebElement usernameInput = driver.findElement(By.name(env.getProperty(Constants.INSTAGRAM.LOGIN.USERNAME_INPUT_NAME)));
		WebElement loginButton = driver.findElement(By.xpath("//*[contains(text(), 'Log in')]"));

		usernameInput.sendKeys(env.getProperty("userName"));
		passwordInput.sendKeys(env.getProperty("userPassword"));
		loginButton.click();

		WebElement profilePictureElement = (new WebDriverWait(driver, 10)).until(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'profile picture')]")));

		boolean isLoggedIn = Constants.INSTAGRAM.LOGIN.HOMEPAGE_URL.equalsIgnoreCase(driver.getCurrentUrl());

		if (!isLoggedIn) {
			log.error("Unable to login...");
			// TODO take snapshot of page and save screenshot for debugging to advice what
			// went wrong
			throw new RuntimeException("Unable to login");
		} else {
			log.info("Successfully logged in...");
			readAccounts();
		}

	}

	@Override
	public void readAccounts() {
		log.info("Starting file processing...");

		accounts = new LinkedHashSet<>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("@")) {
					accounts.add(line.substring(1));
				} else {
					accounts.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void performLikes() {

	}

}
