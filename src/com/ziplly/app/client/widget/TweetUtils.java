package com.ziplly.app.client.widget;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class TweetUtils {
	final static RegExp urlPattern = RegExp.compile("(.*)(https?:\\/\\/[^\\s]+)(\\s*.*)");

	/**
	 * INCOMPLETE: TODO Converts a link - "http(s)://... into an anchor.
	 * 
	 * @param content
	 * @return
	 */
	public static String getContent(String content) {
		StringBuilder newContent = new StringBuilder();
		MatchResult matcher = urlPattern.exec(content);
		if (matcher != null) {
			// System.out.println("Group count = "+matcher.getGroupCount());
			int count = matcher.getGroupCount() - 1;
			while (count > 0) {
				String grp1 = matcher.getGroup(1);
				String grp2 = matcher.getGroup(2);
				String grp3 = matcher.getGroup(3);
				// String grp4 = matcher.getGroup(4);
				// System.out.println("G1="+grp1+" G2="+grp2+" G3="+grp3+" G4="+grp4);
				newContent.append(grp1);
				newContent.append("<a class='link' href='" + grp2 + "' target='_blank'>" + grp2 + "</a>");
				newContent.append(grp3);
				// System.out.println("COUNT="+count);
				count -= 3;
			}
		}

		if (matcher == null) {
			newContent.append(content);
		}
		return newContent.toString();
	}
}
