package com.ziplly.app.client.resource;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale("en_US")
public interface DynamicStringDefinitions extends Messages {
	String accountAlreadyExists(String email);
}
