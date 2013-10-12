package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.ziplly.app.shared.BCrypt;

@NamedQueries({
	@NamedQuery(
		name = "findAccountByEmail",
		query = "from Account a where a.email = :email"
	),
	@NamedQuery(
		name = "findByEmailAndPassword",
		query = "from Account a where a.email = :email and a.password = :password"
	),
	@NamedQuery(
		name = "findAccountById",
		query = "from Account a where a.accountId = :id"
	),
	@NamedQuery(
		name = "findAllAccounts",
		query = "from Account"
	)
})
@Entity
@Table(name="account")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="account_id")
	Long accountId;
	@Column(name="facebook_id")
	private String fId;
	@Column(name="email")
	private String email;
	@Column(name="password")
	private String password;
	@Column(name="first_name")
	private String firstName;
	@Column(name="last_name")
	private String lastName;
	@Column(name="profile_url")
	private String url;
	@Column(name="access_token")
	private String accessToken;
	@Column(name="image_url")
	private String imageUrl;
	private String introduction;
	private String city;
	private String state;
	private int zip;
	private String longitude;
	private String latitude;
	@OneToMany(mappedBy="account", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<AccountSettings> accountSettings = new ArrayList<AccountSettings>();
	
	@Column(name="last_login")
	private Date lastLoginTime;
	@Column(name="time_created")
	private Date timeCreated;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="sender")
	private List<Tweet> tweets = new ArrayList<Tweet>();
	
	public Account() {
	}
	
	public Account(AccountDTO account) {
		accountId = account.getAccountId();
		fId = account.getfId();
		firstName = account.getFirstName();
		lastName = account.getLastName();
		email = account.getEmail();
		password = encryptPassword(account.getPassword());
		url = account.getUrl();
		accessToken = account.getAccessToken();
		imageUrl = account.getImageUrl();
		introduction = account.getIntroduction();
		city = account.getCity();
		state = account.getState();
		zip = account.getZip();
		longitude = account.getLongitude();
		latitude = account.getLatitude();
		for(AccountSettingsDTO asd : account.getAccountSettings()) {
			AccountSettings as = new AccountSettings(asd);
			accountSettings.add(as);
		}
		lastLoginTime = account.getLastLoginTime();
		timeCreated = account.getTimeCreated();
	}
	
	public Long getAccountId() {
		return accountId;
	}

	public void setId(Long id) {
		this.accountId = id;
	}

	public String getDisplayName() {
		return getFirstName() +" "+ getLastName();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return getDisplayName() + " : (" + email + ")";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Account)) {
			return false;
		}
		
		Account a = (Account)o;
		return a.getAccountId() == this.accountId;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date date) {
		this.lastLoginTime = date;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public void setLocation(String location) {
		String [] locationMetadata = location.split(",");
		this.city = locationMetadata[0].trim().toLowerCase();
		this.state = locationMetadata[1].trim().toLowerCase();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude.toString();
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude.toString();
	}
	
	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<AccountSettings> getAccountSettings() {
		return accountSettings;
	}

	public void setAccountSettings(List<AccountSettings> accountSettings) {
		this.accountSettings = accountSettings;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = encryptPassword(password);
	}
	
	protected String encryptPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}
}
