package com.ziplly.app.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.customware.gwt.dispatch.shared.Action;

public class SendEmailAction implements Action<SendEmailResult> {
	EmailTemplate emailTemplate;
	ArrayList<String> emailList = new ArrayList<String>();
	private HashMap<String, String> data = new HashMap<String, String>();

	public SendEmailAction() {
	}

	public SendEmailAction(EmailTemplate template) {
		this.emailTemplate = template;
	}

	public void addEmail(String email) {
		emailList.add(email);
	}

	public void setEmailList(List<String> list) {
		if (list != null) {
			emailList.addAll(list);
		}
	}

	public ArrayList<String> getEmails() {
		return emailList;
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}

	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate template) {
		this.emailTemplate = template;
	}
}
