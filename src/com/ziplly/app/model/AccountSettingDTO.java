package com.ziplly.app.model;

import java.io.Serializable;

import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;

public class AccountSettingDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private AccountDetailsType section;
	private ShareSetting setting;
	private AccountDTO account;

	public AccountSettingDTO() {
	}
	
	public AccountSettingDTO(AccountSetting as) {
		this.id = as.getId();
		this.section = as.getSection();
		this.setting = as.getSetting();
		this.account = new AccountDTO();
		this.account.setAccountId(as.getAccount().getAccountId());
	}
	
	public AccountSettingDTO(AccountSettingDTO as) {
		this.id = as.getId();
		this.section = as.getSection();
		this.setting = as.getSetting();
		this.account = as.getAccount();
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

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}
}
