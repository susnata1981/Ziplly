package com.ziplly.app.model;

import java.io.Serializable;

import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;

public class PrivacySettingsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private AccountDetailsType section;
	private ShareSetting setting;
	private Account account;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public AccountDetailsType getSection() {
		return section;
	}
	public void setSection(AccountDetailsType section) {
		this.section = section;
	}
	public ShareSetting getSetting() {
		return setting;
	}
	public void setSetting(ShareSetting setting) {
		this.setting = setting;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
}
