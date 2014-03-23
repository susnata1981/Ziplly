package com.ziplly.app.client.widget;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class TweetUtils {
	final static RegExp urlsPattern = RegExp.compile("(https?:\\/\\/[^\\s]+)");
	final static RegExp urlPattern = RegExp.compile("(.*)(https?:\\/\\/[^(\\s|,)]+)(\\s*.*)");
	private static final String BR_CODE = "<br/>";
	private static final String NEW_LINE = "\n";

	/**
	 * INCOMPLETE: TODO Converts a link - "http(s)://... into an anchor.
	 * 
	 * @param content
	 * @return
	 */
	public static String getContent(String content) {
		StringBuilder newContent = new StringBuilder();
		boolean foundPattern = true;
		MatchResult matcher = null;//urlsPattern.exec(content);
		
		if (content == null || content.length() == 0) {
			return "";
		}
		
		int sindex = 0;
		while(foundPattern) {
			matcher = urlsPattern.exec(content.substring(sindex));
			if (matcher != null) {
				String group = matcher.getGroup(0);
				int patternStartIndex = content.indexOf(group);
				int patternEndIndex = patternStartIndex + group.length() ;//content.indexOf(' ', patternStartIndex);
				newContent.append(content.substring(sindex, patternStartIndex));
				replaceUrlWithAnchorTag(group, newContent);
				sindex = patternEndIndex;
				
				if (sindex == -1 || sindex >= content.length()) {
					foundPattern = false;
				}
			} else {
				newContent.append(content.substring(sindex));
				foundPattern = false;
			}
		}

		return newContent.toString().replaceAll(NEW_LINE, BR_CODE);
	}
	
	public static void replaceUrlWithAnchorTag(String content, StringBuilder result) {
		MatchResult matcher = urlPattern.exec(content);
		if (matcher != null) {
			String grp1 = matcher.getGroup(1);
			String grp2 = matcher.getGroup(2);
			String grp3 = matcher.getGroup(3);
			result.append(grp1);
			result.append("<a class='link' href='" + grp2 + "' target='_blank'>" + grp2 + "</a>");
			result.append(grp3);
		}
	}

	public static void main(String[] args) {
		String content = "Check this out \n\nhttp://www.yahoo.com/susnata/01923.html hi http://www.msn.com/01923.html";
		String content2 = "Take a look at this - http://www.yahoo.com/susnata/01923.html, http://www.ziplly.com";
		String content3 = "Take a look at this";
		StringBuilder sb = new StringBuilder();
		System.out.println(TweetUtils.getContent(content));
//		TweetUtils.replaceUrlWithAnchorTag(content2, sb);
//		System.out.println(sb);
	}
}
