package com.yp.instabot.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yp.instabot.service.BotService;

import io.swagger.annotations.ApiOperation;

@ControllerAdvice
@RestController
public class BotController {
	
	private static final Logger log = LoggerFactory.getLogger(BotController.class);
	
	@Autowired
	private BotService botService;
	
	@RequestMapping(value="/index.html", method=RequestMethod.GET)
	public String index() {
		return "Hello Instabot!";
	}
	
    @ApiOperation(value = "Like pictures of accounts mentioned in the file", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@RequestMapping(value="/perform-likes", method=RequestMethod.POST, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)	
	public void doWork(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest, HttpServletResponse httpResponse ) {
    	log.info("File Name: " + (file == null ? "null" : file.getOriginalFilename()));

    	if(file != null) {
    		File standardFile = new File(file.getOriginalFilename());
    		try {
				file.transferTo(standardFile);
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException(e);
			}
    		botService.doWork(standardFile);
    	}
	}
    
    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletRequest req, Exception ex) {
    	Map<String, String[]> paramsMap = req.getParameterMap();
    	
    	StringBuilder params = new StringBuilder();
    	if(paramsMap != null && !paramsMap.isEmpty()) {
    		for(Map.Entry<String, String []> entry : paramsMap.entrySet()) {
    			params.append(entry.getKey()).append(" : ").append(entry.getValue() != null ? entry.getValue().toString() : "null").append("\n");
    		}
    	}
    	
    	String errorMessage = new StringBuilder("Path: ").append(req.getContextPath()).append("\n")
    			.append(" Method: ").append(req.getMethod()).append("\n")
    			.append(" Params: ").append(params.toString()).append("\n")
    			.append(" Error: ").append(ex.getMessage()).toString();
    	log.error(errorMessage);
    	log.error("Error: ", ex);
    	
    	
    }
}
