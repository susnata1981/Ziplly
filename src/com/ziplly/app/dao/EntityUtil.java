package com.ziplly.app.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

import com.google.common.collect.Lists;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.model.AccountNotificationSettings;
import com.ziplly.app.model.AccountNotificationSettingsDTO;
import com.ziplly.app.model.BusinessAccount;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.BusinessProperties;
import com.ziplly.app.model.BusinessPropertiesDTO;
import com.ziplly.app.model.Comment;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.Conversation;
import com.ziplly.app.model.ConversationDTO;
import com.ziplly.app.model.Coupon;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.Hashtag;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.model.Image;
import com.ziplly.app.model.ImageDTO;
import com.ziplly.app.model.Interest;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.Location;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.Love;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.Message;
import com.ziplly.app.model.MessageDTO;
import com.ziplly.app.model.Neighborhood;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PendingInvitations;
import com.ziplly.app.model.PendingInvitationsDTO;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PostalCode;
import com.ziplly.app.model.PostalCodeDTO;
import com.ziplly.app.model.PrivacySettings;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.model.PurchasedCoupon;
import com.ziplly.app.model.PurchasedCouponDTO;
import com.ziplly.app.model.Spam;
import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.model.Subscription;
import com.ziplly.app.model.SubscriptionPlan;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.Transaction;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.model.Tweet;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.overlay.SubscriptionDTO;

public class EntityUtil {

	public static AccountDTO convert(Account acct) {
		if (acct instanceof PersonalAccount) {
			return clone((PersonalAccount) acct, false);
		} else if (acct instanceof BusinessAccount) {
			return clone((BusinessAccount) acct, false);
		}

		throw new IllegalArgumentException();
	}

	public static AccountDTO convert(Account acct, boolean topLevelFields) {
		if (acct instanceof PersonalAccount) {
			return clone((PersonalAccount) acct, true);
		} else if (acct instanceof BusinessAccount) {
			return clone((BusinessAccount) acct, true);
		}

		throw new IllegalArgumentException();
	}

	public static Account convert(AccountDTO acct) {
		if (acct instanceof PersonalAccountDTO) {
			return new PersonalAccount((PersonalAccountDTO) acct);
		} else if (acct instanceof BusinessAccountDTO) {
			return new BusinessAccount((BusinessAccountDTO) acct);
		}

		throw new IllegalArgumentException();
	}

	public static void clone(Account account, AccountDTO acct, boolean onlyTopLevelFields) {
		copyCommonAccountFields(account, acct);

		if (onlyTopLevelFields) {
			return;
		}

		if (Hibernate.isInitialized(account.getAccountNotification())) {
			for (AccountNotification an : account.getAccountNotification()) {
				acct.getAccountNotifications().add(clone(an));
			}
		}
		if (Hibernate.isInitialized(account.getNotificationSettings())) {
			for (AccountNotificationSettings ans : account.getNotificationSettings()) {
				acct.getNotificationSettings().add(clone(ans));
			}
		}

		if (Hibernate.isInitialized(account.getPrivacySettings())) {
			for (PrivacySettings ps : account.getPrivacySettings()) {
				acct.getPrivacySettings().add(clone(ps));
			}
		}
	}

	private static void copyCommonAccountFields(Account account, AccountDTO acct) {
		acct.setAccountId(account.getAccountId());
		acct.setPassword(account.getPassword());
		acct.setFacebookId(account.getFacebookId());
		acct.setEmail(account.getEmail());
		acct.setUrl(account.getUrl());
		// acct.setImageUrl(account.getImageUrl());
		acct.setRole(account.getRole());
		acct.setStatus(account.getStatus());
		acct.setLastLoginTime(account.getLastLoginTime());
		acct.setTimeCreated(account.getTimeCreated());
		acct.setUid(account.getUid());

		if (Hibernate.isInitialized(account.getLocations())) {
			for (Location loc : account.getLocations()) {
				acct.addLocation(clone(loc));
			}
		}

		for (Image image : account.getImages()) {
			acct.addImage(clone(image));
		}

		if (account.getCurrentLocation() != null) {
			acct.setCurrentLocation(clone(account.getCurrentLocation()));
		}
	}

	// public static AccountDTO topLevelClone(Account account) {
	// if (account instanceof PersonalAccountDTO) {
	//
	// } else if (account instanceof BusinessAccountDTO){
	//
	// }
	//
	// acct.setAccountId(account.getAccountId());
	// acct.setPassword(account.getPassword());
	// acct.setFacebookId(account.getFacebookId());
	// acct.setEmail(account.getEmail());
	// acct.setUrl(account.getUrl());
	// acct.setImageUrl(account.getImageUrl());
	// acct.setZip(account.getZip());
	// acct.setNeighborhood(account.getNeighborhood());
	// acct.setCity(account.getCity());
	// acct.setState(account.getState());
	// acct.setRole(account.getRole());
	// acct.setStatus(account.getStatus());
	// acct.setLastLoginTime(account.getLastLoginTime());
	// acct.setTimeCreated(account.getTimeCreated());
	// acct.setUid(account.getUid());
	// return acct;
	// }

	public static PersonalAccountDTO clone(PersonalAccount account, boolean onlyTopLevelFields) {
		PersonalAccountDTO dest = new PersonalAccountDTO();
		clone(account, dest, onlyTopLevelFields);
		dest.setFirstName(account.getFirstName());
		dest.setLastName(account.getLastName());
		dest.setIntroduction(account.getIntroduction());
		dest.setOccupation(account.getOccupation());
		dest.setGender(account.getGender());
		dest.setBadge(account.getBadge());

		if (Hibernate.isInitialized(account.getInterests())) {
			for (Interest interest : account.getInterests()) {
				InterestDTO interestDto = clone(interest);
				dest.getInterests().add(interestDto);
			}
		}
		return dest;
	}

	public static BusinessAccountDTO clone(BusinessAccount account, boolean onlyTopLevelFields) {
		BusinessAccountDTO resp = new BusinessAccountDTO();
		clone(account, resp, onlyTopLevelFields);
		resp.setName(account.getName());
		resp.setPhone(account.getPhone());
		resp.setBusinessType(account.getBusinessType());
		resp.setWebsite(account.getWebsite());
		// resp.setStreet1(account.getStreet1());
		// resp.setStreet2(account.getStreet2());
		resp.setProperties(clone(account.getProperties()));
		resp.setCategory(account.getCategory());

		for(Subscription subscription : account.getSubscriptions()) {
			resp.getSubscriptions().add(clone(subscription, true));
		}

		return resp;
	}

	public static InterestDTO clone(Interest interest) {
		InterestDTO interestDto = new InterestDTO();
		interestDto.setInterestId(interest.getInterestId());
		interestDto.setName(interest.getName());
		interestDto.setTimeCreated(interest.getTimeCreated());
		return interestDto;
	}

	public static TweetDTO clone(Tweet tweet) {
		return clone(tweet, true);
	}

	public static TweetDTO clone(Tweet tweet, boolean needSender) {
		TweetDTO resp = new TweetDTO();
		resp.setTweetId(tweet.getTweetId());
		resp.setImageId(tweet.getImageId());
		resp.setType(tweet.getType());
		resp.setContent(tweet.getContent());
		resp.setStatus(tweet.getStatus());
		resp.setImage(tweet.getImage());
		resp.setTimeCreated(tweet.getTimeCreated());

		// Neighborhood
		for (Neighborhood n : tweet.getTargetNeighborhoods()) {
			resp.getTargetNeighborhoods().add(clone(n));
		}

		if (tweet.getCoupon() != null) {
			resp.setCoupon(clone(tweet.getCoupon()));
		}

		// Image
		for (Image image : tweet.getImages()) {
			resp.addImage(clone(image));
		}

		// Sender
		if (needSender && Hibernate.isInitialized(tweet.getSender())) {
			resp.setSender(convert(tweet.getSender()));
		}

		// Comments
		if (Hibernate.isInitialized(tweet.getComments())) {
			for (Comment c : tweet.getComments()) {
				resp.getComments().add(clone(c));
			}
		}

		// Likes
		if (Hibernate.isInitialized(tweet.getLikes())) {
			for (Love l : tweet.getLikes()) {
				resp.getLikes().add(clone(l));
			}
		}
		return resp;
	}

	public static List<TweetDTO> cloneList(List<Tweet> tweets) {
		List<TweetDTO> result = Lists.newArrayList();
		for (Tweet tweet : tweets) {
			result.add(EntityUtil.clone(tweet));
		}
		return result;
	}

	public static List<AccountDTO> cloneAccountList(List<Account> accounts) {
		List<AccountDTO> result = Lists.newArrayList();
		for (Account account : accounts) {
			result.add(convert(account));
		}
		return result;
	}

	public static CommentDTO clone(Comment comment) {
		CommentDTO resp = new CommentDTO();
		resp.setCommentId(comment.getCommentId());
		resp.setAuthor(convert(comment.getAuthor()));
		resp.setContent(comment.getContent());
		resp.setTimeCreated(comment.getTimeCreated());

		TweetDTO tweet = new TweetDTO();
		tweet.setTweetId(comment.getTweet().getTweetId());
		resp.setTweet(tweet);

		return resp;
	}

	public static LoveDTO clone(Love like) {
		LoveDTO resp = new LoveDTO();
		resp.setAuthor(convert(like.getAuthor()));
		resp.setTimeCreated(like.getTimeCreated());
		return resp;
	}

	public static ConversationDTO clone(Conversation c) {
		ConversationDTO resp = new ConversationDTO();
		resp.setId(c.getId());
		resp.setReceiver(convert(c.getReceiver()));
		resp.setSender(convert(c.getSender()));
		resp.setTimeUpdated(c.getTimeUpdated());
		resp.setSubject(c.getSubject());
		resp.setStatus(c.getStatus());
		resp.setTimeCreated(c.getTimeCreated());

		for (Message m : c.getMessages()) {
			resp.add(clone(m));
		}
		return resp;
	}

	public static MessageDTO clone(Message m) {
		MessageDTO resp = new MessageDTO();
		resp.setMessage(m.getMessage());
		resp.setSender(convert(m.getSender()));
		resp.setReceiver(convert(m.getSender()));
		resp.setTimeCreated(m.getTimeCreated());
		return resp;
	}

	public static SubscriptionPlanDTO clone(SubscriptionPlan plan) {
		SubscriptionPlanDTO resp = new SubscriptionPlanDTO();
		resp.setSubscriptionId(plan.getSubscriptionId());
		resp.setName(plan.getName());
		resp.setDescription(plan.getDescription());
		resp.setFee(plan.getFee());
		resp.setCouponsAllowed(plan.getCouponsAllowed());
		resp.setTweetsAllowed(resp.getTweetsAllowed());
		resp.setStatus(plan.getStatus());
		resp.setPlanType(plan.getPlanType());
		resp.setTimeCreated(plan.getTimeCreated());
		return resp;
	}

	public static SubscriptionDTO clone(Subscription subscription) {
		return clone(subscription, true);
	}

	public static SubscriptionDTO clone(Subscription subscription, boolean topLevelOnly) {
		SubscriptionDTO resp = new SubscriptionDTO();
		resp.setSubscriptionId(subscription.getSubscriptionId());
		resp.setSubscriptionPlan(clone(subscription.getSubscriptionPlan()));
		// Avoid recursion
		resp.setTransaction(clone(subscription.getTransaction(), topLevelOnly));
		resp.setStatus(subscription.getStatus());
		resp.setTimeUpdated(subscription.getTimeUpdated());
		resp.setTimeCreated(subscription.getTimeCreated());
		return resp;
	}
	
	public static List<SubscriptionPlanDTO> cloneSubscriptionPlanList(List<SubscriptionPlan> plans) {
		List<SubscriptionPlanDTO> resp = Lists.newArrayList();
		for (SubscriptionPlan plan : plans) {
			resp.add(EntityUtil.clone(plan));
		}
		return resp;
	}

	public static AccountNotificationDTO clone(AccountNotification an) {
		AccountNotificationDTO resp = new AccountNotificationDTO();
		resp.setNotificationId(an.getNotificationId());
		resp.setRecipient(convert(an.getRecipient(), true));
		resp.setSender(convert(an.getSender(), true));

		// Tweet
		if (an.getTweet() != null) {
			resp.setTweet(clone(an.getTweet()));
		}

		// Conversation
		if (an.getConversation() != null) {
			resp.setConversation(clone(an.getConversation()));
		}

		resp.setReadStatus(an.getReadStatus());
		resp.setType(an.getType());
		resp.setTimeUpdated(an.getTimeUpdated());
		resp.setTimeCreated(an.getTimeCreated());
		return resp;
	}

	public static AccountNotificationSettingsDTO clone(AccountNotificationSettings ans) {
		AccountNotificationSettingsDTO resp = new AccountNotificationSettingsDTO();
		resp.setNotificationId(ans.getNotificationId());
		AccountDTO acct = new AccountDTO();
		acct.setAccountId(ans.getAccount().getAccountId());
		resp.setAccount(acct);
		resp.setType(ans.getType());
		resp.setAction(ans.getAction());
		resp.setTimeCreated(ans.getTimeCreated());
		return resp;
	}

	public static PrivacySettingsDTO clone(PrivacySettings ps) {
		PrivacySettingsDTO resp = new PrivacySettingsDTO();
		resp.setId(ps.getId());
		resp.setSection(ps.getSection());
		resp.setSetting(ps.getSetting());
		return resp;
	}

	public static HashtagDTO clone(Hashtag h) {
		HashtagDTO resp = new HashtagDTO();
		resp.setId(h.getId());
		resp.setTag(h.getTag());
		if (Hibernate.isInitialized(h.getTweets())) {
			for (Tweet t : h.getTweets()) {
				resp.add(clone(t));
			}
		}
		return resp;
	}

	public static List<HashtagDTO> cloneHashtahList(List<Hashtag> result) {
		List<HashtagDTO> resp = new ArrayList<HashtagDTO>();
		for (Hashtag h : result) {
			resp.add(clone(h));
		}
		return resp;
	}

	public static SpamDTO clone(Spam s) {
		SpamDTO resp = new SpamDTO();
		resp.setId(s.getId());
		resp.setReporter(convert(s.getReporter()));
		resp.setTweet(clone(s.getTweet()));
		return resp;
	}

	public static List<AccountNotificationDTO>
	    cloneAccountNotificationList(List<AccountNotification> result) {
		// return Lists.transform(result, new Function<AccountNotification,
		// AccountNotificationDTO>() {
		// @Override
		// public AccountNotificationDTO apply(AccountNotification an) {
		// return EntityUtil.clone(an);
		// }
		// });
		List<AccountNotificationDTO> resp = Lists.newArrayList();
		for (AccountNotification an : result) {
			resp.add(clone(an));
		}
		return resp;
	}

	public static BusinessPropertiesDTO clone(BusinessProperties source) {
		BusinessPropertiesDTO dest = new BusinessPropertiesDTO();
		dest.setId(source.getId());
		dest.setSundayStartTime(source.getSundayStartTime());
		dest.setSundayEndTime(source.getSundayEndTime());
		dest.setMondayStartTime(source.getMondayStartTime());
		dest.setMondayEndTime(source.getMondayEndTime());
		dest.setTuesdayStartTime(source.getTuesdayStartTime());
		dest.setTuesdayEndTime(source.getTuesdayEndTime());
		dest.setWednesdayStartTime(source.getWednesdayStartTime());
		dest.setWednesdayEndTime(source.getWednesdayEndTime());
		dest.setThursdayStartTime(source.getThursdayStartTime());
		dest.setThursdayEndTime(source.getThursdayEndTime());
		dest.setFridayStartTime(source.getFridayStartTime());
		dest.setFridayEndTime(source.getFridayEndTime());
		dest.setSaturdayStartTime(source.getSaturdayStartTime());
		dest.setSaturdayEndTime(source.getSaturdayEndTime());
		dest.setHolidays(source.getHolidays());
		dest.setAcceptsCreditCard(source.getAcceptsCreditCard());
		dest.setParkingAvailable(source.isParkingAvailable());
		dest.setWifiAvailable(source.getWifiAvailable());
		dest.setGoodForKids(source.getGoodForKids());
		dest.setPriceRange(source.getPriceRange());
		dest.setCuisine(source.getCuisine());
		return dest;
	}

	public static NeighborhoodDTO clone(Neighborhood neighborhood) {
		NeighborhoodDTO dest = new NeighborhoodDTO();

		if (neighborhood.getCity() != null) {
			dest.setCity(neighborhood.getCity());
		}
		dest.setState(neighborhood.getState());
		dest.setName(neighborhood.getName());
		dest.setType(neighborhood.getType());
		dest.setImageUrl(neighborhood.getImageUrl());
		dest.setNeighborhoodId(neighborhood.getNeighborhoodId());
		dest.setTimeCreated(neighborhood.getTimeCreated());

		// Add parent neighborhood.
		if (neighborhood.getParentNeighborhood() != null) {
			dest.setParentNeighborhood(clone(neighborhood.getParentNeighborhood()));
		}

		// Add postal code
		for (PostalCode p : neighborhood.getPostalCodes()) {
			dest.addPostalCode(clone(p));
		}

		// Add images.
		for (Image image : neighborhood.getImages()) {
			dest.addImage(clone(image));
		}
		return dest;
	}

	public static List<NeighborhoodDTO> cloneNeighborhoodList(List<Neighborhood> neighborhoods) {
		List<NeighborhoodDTO> result = Lists.newArrayList();
		for (Neighborhood neighborhood : neighborhoods) {
			result.add(clone(neighborhood));
		}

		return result;
	}

	public static PostalCodeDTO clone(PostalCode postalCode) {
		PostalCodeDTO dest = new PostalCodeDTO();
		dest.setPostalCode(postalCode.getPostalCode());
		// dest.setPostalCodeId(postalCode.getPostalCodeId());
		dest.setCity(postalCode.getCity());
		dest.setState(postalCode.getState());
		dest.setFullState(postalCode.getFullState());
		dest.setLatitude(postalCode.getLatitude());
		dest.setLongitude(postalCode.getLongitude());
		return dest;
	}

	public static List<PostalCodeDTO> clonePostalCodeList(List<PostalCode> postalCodes) {
		List<PostalCodeDTO> result = Lists.newArrayList();
		for (PostalCode postalCode : postalCodes) {
			result.add(clone(postalCode));
		}

		return result;
	}

	public static PendingInvitationsDTO clone(PendingInvitations pi) {
		PendingInvitationsDTO resp = new PendingInvitationsDTO();
		resp.setEmail(pi.getEmail());
		resp.setId(pi.getId());
		resp.setZip(pi.getZip());
		return resp;
	}

	public static List<PendingInvitationsDTO>
	    clonePendingInvidationList(List<PendingInvitations> input) {
		List<PendingInvitationsDTO> result = new ArrayList<PendingInvitationsDTO>();
		for (PendingInvitations pi : input) {
			result.add(clone(pi));
		}
		return result;
	}

	public static List<InterestDTO> cloneInterestList(List<Interest> result) {
		List<InterestDTO> resp = new ArrayList<InterestDTO>();
		for (Interest i : result) {
			resp.add(clone(i));
		}
		return resp;
	}

	public static LocationDTO clone(Location loc) {
		LocationDTO resp = new LocationDTO();
		resp.setLocationId(loc.getLocationId());
		resp.setNeighborhood(clone(loc.getNeighborhood()));
		resp.setAddress(loc.getAddress());
		resp.setType(loc.getType());
		resp.setTimeUpdated(loc.getTimeUpdated());
		resp.setTimeCreated(loc.getTimeCreated());
		return resp;
	}

	public static ImageDTO clone(Image image) {
		ImageDTO resp = new ImageDTO();
		resp.setId(image.getId());
		resp.setBlobKey(image.getBlobKey());
		resp.setUrl(image.getUrl());
		resp.setStatus(image.getStatus());
		resp.setTimeCreated(image.getTimeCreated());
		return resp;
	}

	public static CouponDTO clone(Coupon coupon) {
		CouponDTO resp = new CouponDTO();
		resp.setCouponId(coupon.getCouponId());
		resp.setTitle(coupon.getTitle());
		resp.setDescription(coupon.getDescription());
		resp.setStartDate(coupon.getStartDate());
		resp.setEndDate(coupon.getEndDate());
		resp.setCouponPrice(coupon.getCouponPrice());
		resp.setPrice(coupon.getPrice());
		resp.setItemPrice(coupon.getItemPrice());
		resp.setDiscount(coupon.getDiscount());
		resp.setQuanity(coupon.getQuanity());
		resp.setQuantityPurchased(coupon.getQuantityPurchased());
		resp.setNumberAllowerPerIndividual(coupon.getNumberAllowerPerIndividual());
		resp.setTimeCreated(coupon.getTimeCreated());
		return resp;
	}

	public static PurchasedCouponDTO clone(PurchasedCoupon pc) {
		if (pc == null)
			return null;
		PurchasedCouponDTO resp = new PurchasedCouponDTO();
		resp.setPurchasedCouponId(pc.getPurchasedCouponId());
		resp.setQrcode(pc.getQrcode());
		resp.setStatus(pc.getStatus());
		resp.setCoupon(clone(pc.getCoupon()));
		resp.setTransaction(clone(pc.getTransaction()));
		resp.setTimeUpdated(pc.getTimeUpdated());
		resp.setTimeCreated(pc.getTimeCreated());
		return resp;
	}

	public static TransactionDTO clone(Transaction transaction) {
		return clone(transaction, false);
	}

	public static TransactionDTO clone(Transaction transaction, boolean topLevelOnly) {
		TransactionDTO resp = new TransactionDTO();
		resp.setTransactionId(transaction.getTransactionId());
		resp.setStatus(transaction.getStatus());
		resp.setCurrency(transaction.getCurrency());
		resp.setTimeUpdated(transaction.getTimeUpdated());
		resp.setTimeCreated(transaction.getTimeCreated());

		//	Avoid recursion
		if (!topLevelOnly) {
			resp.setBuyer(convert(transaction.getBuyer()));
		}

		return resp;
	}

	public static List<CouponDTO> cloneCouponList(List<Coupon> coupons) {
		List<CouponDTO> result = new ArrayList<CouponDTO>();
		for (Coupon c : coupons) {
			result.add(clone(c));
		}
		return result;
	}

	public static List<TransactionDTO> cloneCouponTransactionList(List<Transaction> transactions) {
		List<TransactionDTO> result = new ArrayList<TransactionDTO>();
		for (Transaction transaction : transactions) {
			result.add(EntityUtil.clone(transaction));
		}

		return result;
	}

	public static List<PurchasedCouponDTO> clonePurchaseCouponList(List<PurchasedCoupon> purchasedCoupons) {
		List<PurchasedCouponDTO> result = new ArrayList<PurchasedCouponDTO>();
		for (PurchasedCoupon pr : purchasedCoupons) {
			result.add(EntityUtil.clone(pr));
		}

		return result;
  }
}
