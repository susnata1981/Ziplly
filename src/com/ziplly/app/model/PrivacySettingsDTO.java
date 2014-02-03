package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.client.widget.AccountDetailsType;
import com.ziplly.app.client.widget.ShareSetting;

public class PrivacySettingsDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String section;
	private String setting;
	private Account account;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public AccountDetailsType getSection() {
		return AccountDetailsType.valueOf(section);
	}
	public void setSection(AccountDetailsType section) {
		this.section = section.name();
	}
	public ShareSetting getSetting() {
		return ShareSetting.valueOf(setting);
	}
	public void setSetting(ShareSetting setting) {
		this.setting = setting.name();
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		result = prime * result + ((setting == null) ? 0 : setting.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		PrivacySettingsDTO other = (PrivacySettingsDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (id.equals(other.id)) {
			return true;
		}
		return false;
	}
	
	public ShareSetting[] getAllowedShareSettings() {
		List<ShareSetting> result = new ArrayList<ShareSetting>();
		AccountDetailsType adt = AccountDetailsType.valueOf(section);
		
		if (adt.isAllowedPublic()) {
			result.add(ShareSetting.PUBLIC);
		}
		
		if (adt.isAllowedCommunity()) {
			result.add(ShareSetting.COMMUNITY);
		}
		
		if (adt.isAllowedPrivate()) {
			result.add(ShareSetting.PRIVATE);
		}
		
		ShareSetting response [] = new ShareSetting[result.size()];
		return result.toArray(response);
	}
}
