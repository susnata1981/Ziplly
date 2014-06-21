package com.ziplly.app.server.model.jpa;

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

import com.ziplly.app.model.Badge;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;

@Entity
@DiscriminatorValue("personal")
public class PersonalAccount extends Account {
	private static final long serialVersionUID = 1L;

	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	private String introduction;
	private String occupation;
	private String gender;
	@Column(name = "badge")
	private String badge;
	@Column(name = "facebook_registration")
	private boolean facebookRegistration;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "account_interest", joinColumns = { @JoinColumn(name = "account_id") },
	    inverseJoinColumns = { @JoinColumn(name = "interest_id", nullable = false) })
	private Set<Interest> interests = new HashSet<Interest>();

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE,
	    CascadeType.PERSIST })
	private List<PrivacySettings> accountSettings = new ArrayList<PrivacySettings>();

	public PersonalAccount() {
	}

	public PersonalAccount(PersonalAccountDTO account) {
		super(account);

		this.setFirstName(account.getFirstName());
		this.setLastName(account.getLastName());
		this.introduction = account.getIntroduction();
		this.occupation = account.getOccupation();
		this.setGender(account.getGender());
		this.setBadge(account.getBadge());
		this.setFacebookRegistration(account.getFacebookRegistration());
		for (InterestDTO interest : account.getInterests()) {
			getInterests().add(new Interest(interest));
		}
	}

	public List<PrivacySettings> getAccountSettings() {
		return accountSettings;
	}

	public void setAccountSettings(List<PrivacySettings> accountSettings) {
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

		PersonalAccount a = (PersonalAccount) o;
		return a.getAccountId() == this.getAccountId();
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public Gender getGender() {
		return Gender.valueOf(gender.toUpperCase());
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

	public boolean isFacebookRegistration() {
		return facebookRegistration;
	}

	public void setFacebookRegistration(boolean facebookRegistration) {
		this.facebookRegistration = facebookRegistration;
	}
}