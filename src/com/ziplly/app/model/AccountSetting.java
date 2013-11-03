package com.ziplly.app.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;

@Entity
@Table(name="account_settings")
public class AccountSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="section")
	private AccountDetailsType section;
	
	@Column(name="setting")
	private ShareSetting setting;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="account_id")
	private Account account;

	public AccountSetting() {
	}
	
	public AccountSetting(AccountSettingDTO asd) {
		this.id = asd.getId();
		this.section = asd.getSection();
		this.setting = asd.getSetting();
		this.account = new Account();
		this.account.setAccountId(asd.getAccount().getAccountId());
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
//	Map<AccountDetailsType, ShareSetting> accountSectionSettings = new HashMap<AccountDetailsType, ShareSetting>();
}
