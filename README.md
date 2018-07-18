# InstaBot

## Starting from command line
java -jar instabot-1.0.0 -Dusername=<> -Dpassword=<> -Dwebdriver.chrome.driver="${chrome_driver}\chromedriver.exe"

## Command line arguments:
-Dusername=${username} 					// mandatory

-Dpassword=${password} 					// mandatory

-Dwebdriver.chrome.driver="${chrome_driver}\chromedriver.exe"		// mandatory

-Ddelay=${Number}		// delay in sec (optional)

-Dheadless=true			// headless mode, unstable, default=false (optional)

### Use following if using Firefox:
-Dwebdriver.firefox.bin="${firefox_directory}\firefox.exe" 

-Dwebdriver.gecko.driver="${geckodriver}\geckodriver.exe"

## Main class: 
InstabotApplication

## Software requirements:
Chrome driver:

http://chromedriver.chromium.org/downloads

## Swagger UI URL:
http://localhost:8080/instabot/swagger-ui.html

Steps:
1. Run app
2. Open swagger UI
3. Click bot-controller -> /perform-likes -> Try it out -> Choose File -> Execute.


## JAR creation
mvn clean package