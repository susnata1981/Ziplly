package com.ziplly.app.server;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.ziplly.app.model.Category;
import com.ziplly.app.shared.facebook.FacebookUserInterest;

public class ServiceUtil {
	public static List<Category> transform(FacebookUserInterest fui) {
		List<Category> result = Lists.newArrayList();
		for(String interest : fui.interests) {
			Category c = new Category();
			c.setCategory(SafeHtmlUtils.fromString(interest).asString().trim().toUpperCase());
			result.add(c);
		}
		return result;
	}
	
//	public static Message convert(MessageDO msgDO) {
//		Message msg = new Message();
//		msg.setId(msgDO.getId());
//		msg.setMessage(msgDO.getMessage());
//		msg.setSubject(msgDO.getSubject());
//		return msg;
//	}
//	
//	public static MessageDO convert(Message msg) {
//		MessageDO resp = new MessageDO();
//		resp.setId(msg.getId());
//		resp.setMessage(msg.getMessage());
//		resp.setSubject(msg.getSubject());
//		return resp;
//	}
//	
//	public static Conversation convert(ConversationDO input) {
//		Conversation resp = new Conversation();
//		for(MessageDO m : input.getMessages()) {
//			Message msg = convert(m);
//			resp.add(msg);
//		}
//		resp.setSender(input.getSender());
//		return resp;
//	}
//	
//	public static List<Conversation> convertAll(List<ConversationDO> input) {
//		List<Conversation> resp = Lists.newArrayList();
//		for(ConversationDO c : input) {
//			resp.add(convert(c));
//		}
//		return resp;
//	}
//
//	public static Collection<? extends Message> convert(List<MessageDO> messages) {
//		List<Message> resp = Lists.newArrayList();
//		for(MessageDO m : messages) {
//			resp.add(convert(m));
//		}
//		return resp;
//	}
}
