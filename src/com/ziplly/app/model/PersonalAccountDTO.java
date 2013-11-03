package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonalAccountDTO extends AccountDTO {
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private String introduction;
	private String occupation;

	private Set<InterestDTO> interests = new HashSet<InterestDTO>();
	private List<AccountSettingDTO> accountSettings = new ArrayList<AccountSettingDTO>();

	public PersonalAccountDTO() {
	}

//	public PersonalAccountDTO(PersonalAccount account) {
//		super(account);
//		this.firstName = account.getFirstName();
//		this.lastName = account.getLastName();
//		this.introduction = account.getIntroduction();
//		this.occupation = account.getOccupation();
//
//		for (AccountSetting asd : account.getAccountSettings()) {
//			AccountSettingDTO as = new AccountSettingDTO(asd);
//			getAccountSettings().add(as);
//		}
//
//		for (Interest interest : account.getInterests()) {
//			getInterests().add(new InterestDTO(interest));
//		}
//	}

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
		return firstName + " " + lastName;
	}
}
