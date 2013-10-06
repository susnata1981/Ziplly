package com.ziplly.app.model;

import java.io.Serializable;

import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;

public class AccountSettingsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private AccountDetailsType section;
	private ShareSetting setting;
	AccountDTO account;

	public AccountSettingsDTO(AccountSettings as) {
		this.id = as.getId();
		this.section = as.getSection();
		this.setting = as.getSetting();
	}
	
	public ShareSetting getSetting() {
		return setting;
	}

	public void setSetting(ShareSetting setting) {
		this.setting = setting;
	}

	public AccountDetailsType getSection() {
		return section;
	}

	public void setSection(AccountDetailsType section) {
		this.section = section;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
