package com.yp.instabot.utils;

import org.springframework.web.multipart.MultipartFile;

public class CommonUtils {

	public static String getFileExtension(MultipartFile file) {
		if(file != null) {
			String fileName = file.getOriginalFilename();
			return fileName.substring(fileName.lastIndexOf("."));
		}
		
		return null;
	}
}
