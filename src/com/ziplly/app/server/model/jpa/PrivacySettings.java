package com.ziplly.app.server.model.jpa;

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
import com.ziplly.app.model.PrivacySettingsDTO;

@Entity
@Table(name = "privacy_settings")
public class PrivacySettings extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "section")
	private String section;

	@Column(name = "setting")
	private String setting;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;

	public PrivacySettings() {
	}

	public PrivacySettings(PrivacySettingsDTO asd) {
		this.id = asd.getId();
		this.setSection(asd.getSection());
		this.setSetting(asd.getSetting());
		this.account = new Account();
		if (asd.getAccount() != null) {
			this.account.setAccountId(asd.getAccount().getAccountId());
		}
	}

	public ShareSetting getSetting() {
		return ShareSetting.valueOf(setting);
	}

	public void setSetting(ShareSetting setting) {
		this.setting = setting.name();
	}

	public AccountDetailsType getSection() {
		return AccountDetailsType.valueOf(section);
	}

	public void setSection(AccountDetailsType section) {
		this.section = section.name();
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
}
