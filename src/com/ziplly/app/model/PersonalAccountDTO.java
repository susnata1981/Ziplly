package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ziplly.app.shared.StringUtil;

public class PersonalAccountDTO extends AccountDTO {
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private String introduction;
	private String occupation;
	private String gender;
	private String badge;

	private Set<InterestDTO> interests = new HashSet<InterestDTO>();
	private List<AccountSettingDTO> accountSettings = new ArrayList<AccountSettingDTO>();
	private boolean facebookRegistration;

	public PersonalAccountDTO() {
	}

	public List<AccountSettingDTO> getAccountSettings() {
		return accountSettings;
	}

	public void setAccountSettings(List<AccountSettingDTO> accountSettings) {
		this.accountSettings = accountSettings;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public Set<InterestDTO> getInterests() {
		return interests;
	}

	public void setInterests(Set<InterestDTO> interests) {
		this.interests = interests;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getDisplayName() {
		return StringUtil.capitalize(firstName) + " " + lastName;
	}

	public void setFacebookRegistration(boolean b) {
		facebookRegistration = b;
	}

	public boolean getFacebookRegistration() {
		return facebookRegistration;
	}

	public Gender getGender() {
		return Gender.valueOf(gender);
	}

	public void setGender(Gender gender) {
		this.gender = gender.name();
	}

	public Badge getBadge() {
		return Badge.valueOf(badge);
	}

	public void setBadge(Badge badge) {
		this.badge = badge.name();
	}
}
