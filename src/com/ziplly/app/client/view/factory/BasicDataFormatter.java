package com.ziplly.app.client.view.factory;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory.Formatter;
import com.ziplly.app.client.widget.TweetUtils;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NotificationType;
import com.ziplly.app.model.PostalCodeDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.shared.FieldVerifier;

public class BasicDataFormatter implements Formatter<Object> {
	private static final RegExp urlsPattern = RegExp.compile("(https?:\\/\\/[^\\s]+)");
	private static final RegExp urlPattern = RegExp.compile("(.*)(https?:\\/\\/[^(\\s|,)]+)(\\s*.*)");
	private static final String BR_CODE = "<br/>";
	private static final String NEW_LINE = "\n";

	public static final TimeTemplate timeTemplate = GWT.create(TimeTemplate.class);
	public static final CouponTimeTemplate couponTimeTemplate = GWT.create(CouponTimeTemplate.class);
	
	public interface TimeTemplate extends SafeHtmlTemplates {
		@Template("{0} {1} ago")
		SafeHtml timeElapsed(Long elapsedTime, String key);
	}
	
	public interface CouponTimeTemplate extends TimeTemplate {
		@Override
		@Template("{0} more {1}")
		SafeHtml timeElapsed(Long elapsedTime, String key);
	}
	
	public interface CouponTemplate extends SafeHtmlTemplates {
		@Template("<div class='couponDiv'><h4>{0}</h4>"
				+ "<ul>"
				+ "<li><span class='heading'>Description</span> {1}$</li>"
				+ "<li><span class='heading'>Regular Price</span> {2}$</li>"
				+ "<li><span class='heading'>Discount</span> {3}</li>"
				+ "<li><span class='heading'>Quantity remaining</span> {4}</li>"
				+ "<li><span class='heading'>Start date</span> {5}</li>"
				+ "<li><span class='heading'>End date</span> {6}</li></ul></div>")
		SafeHtml getCouponDetailsTemplate(String title, String description, BigDecimal price, String discount, Long quantity, String start, String end);
	}
	
	public static final CouponTemplate couponTemplate = GWT.create(CouponTemplate.class);
	
	@Override
	public String format(Object value, ValueType type) {
		switch (type) {
			case INTEGER_VALUE:
			case TEXT_VALUE:
				return getContent((String)value);
			case STRING_VALUE:
				return value.toString();
			case DATE_VALUE_MEDIUM:
			case DATE_VALUE_SHORT:
				return getTimeDiff(new Date(), (Date) value, timeTemplate);
			case DATE_VALUE:
				String date = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format((Date) value);
				return date;
			case UNREAD_MESSAGE_COUNT:
				return "(" + value.toString() + ")";
			case GENDER:
				return ((Gender) value).getName();
			case TWEET_TYPE:
				return ((TweetType) value).getTweetName();
			case NOTIFICATION_TYPE:
				return ((NotificationType) value).getNotificationName();
			case PROFILE_IMAGE_URL:
				return getImageUrl((AccountDTO) value);
			case ADDRESS:
				return value.toString();
			case NEIGHBORHOOD:
				NeighborhoodDTO n = (NeighborhoodDTO) value;
				if (n.getParentNeighborhood() != null) {
					return n.getName() + ", " + n.getParentNeighborhood().getName();
				} else {
					return n.getName();
				}
			case FOUND_NEIGHBORHOOD_MESSAGE:
				n = (NeighborhoodDTO) value;
				return "You live in "+format(n, ValueType.NEIGHBORHOOD);
			case POSTAL_CODE:
				PostalCodeDTO p = (PostalCodeDTO) value;
				return p.getCity() + ", " + p.getState();
			case NEIGHBORHOOD_IMAGE:
				NeighborhoodDTO neighborhood = (NeighborhoodDTO) value;
				return getNeighborhoodImageUrl(neighborhood);
			case PRICE:
				BigDecimal price = (BigDecimal)value;
				return price.toPlainString() +"$";
			case PERCENT:
				BigDecimal percent = (BigDecimal)value;
				return percent.toPlainString() +"%";
			case COUPON:
				CouponDTO c = (CouponDTO)value;
				String title = capitalize(c.getDescription()) + " (" + format(c.getPrice(), ValueType.PRICE) + ")";
				String discount = format(c.getDiscount(), ValueType.PERCENT) +" off";
				long quantityRemaining = c.getQuanity() - c.getQuantityPurchased();
		    DateTimeFormat fmt = DateTimeFormat.getFormat("EEEE, MMMM dd, yyyy hh:mm a");
				return couponTemplate.getCouponDetailsTemplate(
						title, c.getDescription(), c.getPrice(), discount, quantityRemaining, fmt.format(c.getStartDate()), 
						fmt.format(c.getEndDate())).asString();
			default:
				throw new IllegalArgumentException("Invalid value type to render");
		}
	}

	String getImageUrl(AccountDTO value) {
		String imgUrl = "";
		if (value.getImages().size() > 0) {
			imgUrl = value.getImages().get(0).getUrl() + "=s1600";
		} else {
			imgUrl = ZResources.IMPL.noImage().getSafeUri().asString();
		}
		return imgUrl;
	}

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

	public String getNeighborhoodImageUrl(NeighborhoodDTO n) {
		while (n != null) {
			if (n.getImages().size() > 0) {
				return n.getImages().get(0).getUrl() + "=s1600";
			}
			
			n = n.getParentNeighborhood();
		}
		return "";
	}
	
	public String getNeighborhoodName(NeighborhoodDTO n) {
		if (n == null) {
			return "";
		}
		
		if (n.getParentNeighborhood() != null && FieldVerifier.isEmpty(n.getParentNeighborhood().getName())) {
			return n.getName() + ", " + n.getParentNeighborhood().getName();
		} else {
			return n.getName();
		}
	}
	
	public String getTimeDiff(Date futureDate, Date date, TimeTemplate timeTemplate) {
		Long seconds = (futureDate.getTime() - date.getTime())/1000;
		
		if (seconds > 60 * 60 * 24) {
			long days = seconds / (60*60*24);
			String daysKey = days > 1 ? "days" : "day";
			return timeTemplate.timeElapsed(days, daysKey).asString();
		} else if (seconds > 60 * 60) {
			long hours = seconds / (60*60);
			String hoursKey = hours > 1 ? "hours" : "hour";
			return timeTemplate.timeElapsed(hours, hoursKey).asString();
		} else	if (seconds > 60) {
			long mins = seconds / 60;
			String minsKey = mins > 1 ? "mins" : "min";
			return timeTemplate.timeElapsed(mins, minsKey).asString();
		} else {
			String secondsKey = seconds > 1 ? "seconds" : "second";
			return timeTemplate.timeElapsed(seconds, secondsKey).asString();
		}
	}

	public static void main(String[] args) {
		String content = "Check this out \n\nhttp://www.yahoo.com/susnata/01923.html hi http://www.msn.com/01923.html";
		String content2 = "Take a look at this - http://www.yahoo.com/susnata/01923.html, http://www.ziplly.com";
		String content3 = "Take a look at this";
		StringBuilder sb = new StringBuilder();
		System.out.println(TweetUtils.getContent(content));
		TweetUtils.replaceUrlWithAnchorTag(content2, sb);
		System.out.println(sb);
	}
	
	public String capitalize(String input) {
		if (input != null) {
			return Character.toUpperCase(input.charAt(0)) + input.substring(1);
		}
		
		return "";
	}
}
