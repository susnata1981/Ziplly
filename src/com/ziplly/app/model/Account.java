package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
		query = "from Account a where a.accountId = :accountId"
	),
	@NamedQuery(
		name = "findAllAccounts",
		query = "from Account"
	)
})
@Entity
@Table(name="account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
public class Account extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="account_id")
	private Long accountId;
	
	@Column(name="facebook_id")
	private String facebookId;
	
	@Column(name="access_token")
	private String accessToken;

	@NotNull
	@Column(name="email", unique = true)
	private String email;
	
	@NotNull
	@Column(name="password", updatable=false, insertable=true)
	private String password;
	
	@Column(name="profile_url")
	private String url;
	
	@Column(name="image_url")
	private String imageUrl;
	
	@Column(name="zip", nullable=false)
	private int zip;
	private String neighborhood;
	private String city;
	private String state;
	private AccountStatus status;
	
	@Column(name="role", insertable=true, updatable=false)
	private Role role;

	@Column(name="last_login")
	private Date lastLoginTime;
	
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="sender")
	private List<Tweet> tweets = new ArrayList<Tweet>();
	
	@OneToMany(mappedBy="account", cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	private Set<PrivacySettings> privacySettings = new HashSet<PrivacySettings>();
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="account", cascade = CascadeType.ALL)
	private Set<AccountNotificationSettings> notificationSettings = new HashSet<AccountNotificationSettings>();
	
	private Long uid;
	
	public Account() {
	}
	
	public Account(AccountDTO account) {
		this.setAccountId(account.getAccountId());
		setFacebookId(account.getFacebookId());
		email = account.getEmail();
		if (account.getPassword() != null) {
			password = encryptPassword(account.getPassword());
		}
		url = account.getUrl();
		accessToken = account.getAccessToken();
		imageUrl = account.getImageUrl();
		setZip(account.getZip());
		setNeighborhood(account.getNeighborhood());
		setCity(account.getCity());
		setState(account.getState());
		setRole(account.getRole());
		setStatus(account.getStatus());
		setLastLoginTime(account.getLastLoginTime());
		setTimeCreated(account.getTimeCreated());
		setTimeUpdated(account.getTimeUpdated());
		this.setUid(account.getUid());
		
		for(AccountNotificationSettingsDTO an : account.getNotificationSettings()) {
			notificationSettings.add(new AccountNotificationSettings(an));
		}
		
		for(PrivacySettingsDTO ps : account.getPrivacySettings()) {
			addPrivacySettings(new PrivacySettings(ps));
		}
	}
	
	public Long getAccountId() {
		return accountId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "(" + email + ")";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Account)) {
			return false;
		}
		
		Account a = (Account)o;
		return a.getAccountId() == this.getAccountId();
	}

	public String getPassword() {
		return password;
	}

	public void setDirectPassword(String password) {
		this.password = password;
	}
	
	public void setPassword(String password) {
		this.password = encryptPassword(password);
	}
	
	protected String encryptPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
//
//	public Date getTimeCreated() {
//		return timeCreated;
//	}
//
//	public void setTimeCreated(Date timeCreated) {
//		this.timeCreated = timeCreated;
//	}
	
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	public String getName() {
		if (this instanceof PersonalAccount){
			return ((PersonalAccount)this).getName();
		} else if (this instanceof BusinessAccount) {
			return ((BusinessAccount)this).getName();
		}
		return "";
	}

	public Set<AccountNotificationSettings> getNotificationSettings() {
		return notificationSettings;
	}

	public void setNotificationSettings(Set<AccountNotificationSettings> notifications) {
		this.notificationSettings = notifications;
	}

//	public Date getTimeUpdated() {
//		return timeUpdated;
//	}
//
//	public void setTimeUpdated(Date timeUpdated) {
//		this.timeUpdated = timeUpdated;
//	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Set<PrivacySettings> getPrivacySettings() {
		return privacySettings;
	}

	public void setPrivacySettings(Set<PrivacySettings> privacySettings) {
		this.privacySettings = privacySettings;
	}
	
	public void addPrivacySettings(PrivacySettings privacySetting) {
		privacySetting.setAccount(this);
		this.privacySettings.add(privacySetting);
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}
}
