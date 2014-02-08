package com.ziplly.app.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ZipllyServiceImpl {
	final static Pattern urlPattern = Pattern.compile("(.*)(https?:\\/\\/[^\\s]+)(\\s*.*)");
	public String getContent(String content) {
		StringBuilder newContent = new StringBuilder();
		Matcher matcher = urlPattern.matcher(content);
		boolean found = false;
		
		while (matcher.find()) {
			String grp1 = matcher.group(1);
			String grp2 = matcher.group(2);
			String grp3 = matcher.group(3);
			
			newContent.append(grp1);
			newContent.append("<a href='"+grp2+"'>"+grp2+"</a>");
			newContent.append(grp3);
			found = true;
		} 
		
		if (!found) {
			newContent.append(content);
		}
		return newContent.toString();
	}
	
	public static void main(String[] args) {
		ZipllyServiceImpl z = new ZipllyServiceImpl();
		String c = z.getContent("here http://www.yahoo.com there");
		System.out.println("C="+c);
	}
}

