package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("personal")
public class PersonalAccount extends Account {
	private static final long serialVersionUID = 1L;
	
	@Column(name="first_name")
	private String firstName;
	@Column(name="last_name")
	private String lastName;
	private String introduction;
	private String occupation;
	
	@ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinTable(name="account_interest", 
		joinColumns = {@JoinColumn(name = "account_id")},
		inverseJoinColumns = {@JoinColumn(name = "interest_id", nullable = false)})
	private Set<Interest> interests = new HashSet<Interest>();
	
	@OneToMany(mappedBy="account", fetch=FetchType.EAGER)
	private List<AccountSetting> accountSettings = new ArrayList<AccountSetting>();

	public PersonalAccount() {
	}
	
	public PersonalAccount(PersonalAccountDTO account) {
		super(account);
		
		this.setFirstName(account.getFirstName());
		this.setLastName(account.getLastName());
		this.introduction = account.getIntroduction();
		this.occupation = account.getOccupation();
		
		for(AccountSettingDTO asd : account.getAccountSettings()) {
			AccountSetting as = new AccountSetting(asd);
			getAccountSettings().add(as);
		}
		
		for(InterestDTO interest : account.getInterests()) {
			getInterests().add(new Interest(interest));
		}
	}

	public List<AccountSetting> getAccountSettings() {
		return accountSettings;
	}

	public void setAccountSettings(List<AccountSetting> accountSettings) {
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

	public Set<Interest> getInterests() {
		return interests;
	}

	public void setInterests(Set<Interest> interests) {
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
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof PersonalAccount)) {
			return false;
		}
		
		PersonalAccount a = (PersonalAccount)o;
		return a.getAccountId() == this.getAccountId();
	}
	
	public String getName() {
		return firstName+" "+lastName;
	}
}
