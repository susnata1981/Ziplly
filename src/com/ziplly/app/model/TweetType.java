package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ziplly.app.shared.EmailTemplate;

/**
 * Changes here need to be also made in {@link TweetNotificationBLIImpl}. TODO
 * to abstract that.
 */
public enum TweetType {
	ALL("All", NotificationType.NONE, false, false, EmailTemplate.NONE),
	GENERAL("General", NotificationType.NONE, true, true, EmailTemplate.NONE),
	ANNOUNCEMENT("Announcement", NotificationType.ANNOUNCEMENT, true, true, EmailTemplate.ANNOUNCEMENT),
	SECURITY_ALERTS("Security Alerts", NotificationType.SECURITY_ALERT, true, true, EmailTemplate.SECURITY_ALERT),
	HELP("Help", NotificationType.NONE, true, true, EmailTemplate.NONE),
	CLASSIFIEDS("Classifieds", NotificationType.NONE, true, false, EmailTemplate.NONE),
	OFFERS("Offers", NotificationType.OFFERS, true, true, EmailTemplate.OFFER),
//	HOT_DEALS("Hot Deals", NotificationType.OFFERS, true, true, EmailTemplate.OFFER),
	COUPON("Coupon", NotificationType.COUPON, false, false, EmailTemplate.COUPON_PUBLISHED);

	private String tweetName;
	private NotificationType notificationType;
	private boolean canUsersPublish;
	private boolean canBusinessPublish;
  private EmailTemplate template;

	TweetType(String name, NotificationType type, boolean canUsersPublish, boolean canBusinessPublish, EmailTemplate template) {
		this.tweetName = name;
		this.setNotificationType(type);
		this.setCanUsersPublish(canUsersPublish);
		this.setCanBusinessPublish(canBusinessPublish);
		this.template = template;
	}

	public static List<TweetType> getAllTweetTypeForPublishingByUser() {
		ArrayList<TweetType> types = new ArrayList<TweetType>();
		for (TweetType type : values()) {
			if (type.isCanUsersPublish()) {
				types.add(type);
			}
		}
		return types;
	}

	public static List<TweetType> getAllTweetTypeForPublishingByBusiness() {
		ArrayList<TweetType> types = new ArrayList<TweetType>();
		for (TweetType type : values()) {
			if (type.isCanBusinessPublish()) {
				types.add(type);
			}
		}
		return types;
	}

	public String getTweetName() {
		return tweetName;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public boolean isCanUsersPublish() {
		return canUsersPublish;
	}

	public void setCanUsersPublish(boolean canUsersPublish) {
		this.canUsersPublish = canUsersPublish;
	}

	public boolean isCanBusinessPublish() {
		return canBusinessPublish;
	}

	public void setCanBusinessPublish(boolean canBusinessPublish) {
		this.canBusinessPublish = canBusinessPublish;
	}
	
	public static List<TweetType> getPromotionTypes() {
	  return Arrays.asList(TweetType.COUPON);
	}

  public static List<TweetType> getTweetTypesForRequiringNotification() {
    List<TweetType> result = new ArrayList<TweetType>();
    for(TweetType type: values()) {
      if (type.getTemplate() != EmailTemplate.NONE) {
        result.add(type);
      }
    }
    
    return result;
  }

  public EmailTemplate getTemplate(TweetType type) {
    EmailTemplate template = type.getTemplate();
    if (template != EmailTemplate.NONE) {
      throw new RuntimeException("Invalid tweet type for sending email");
    }
    
    return type.getTemplate();
  }
  
  public EmailTemplate getTemplate() {
    return template;
  }

  public void setTemplate(EmailTemplate template) {
    this.template = template;
  }
}
