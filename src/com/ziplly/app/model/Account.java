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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ziplly.app.shared.BCrypt;

@NamedQueries({
    @NamedQuery(name = "findAccountByEmail", query = "from Account a where a.email = :email"),
    @NamedQuery(name = "findByEmailAndPassword",
        query = "from Account a where a.email = :email and a.password = :password"),
    @NamedQuery(name = "findAccountById", query = "from Account a where a.accountId = :accountId"),
    @NamedQuery(name = "findAllAccounts", query = "from Account") })
@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class Account extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id")
	private Long accountId;

	@Column(name = "facebook_id")
	private String facebookId;

	@Column(name = "access_token")
	private String accessToken;

	@NotNull
	@Column(name = "email", unique = true)
	private String email;

	@NotNull
	@Column(name = "password", updatable = false, insertable = true)
	private String password;

	@Column(name = "profile_url")
	private String url;

	@Column(name = "image_url")
	@Size(max = 1024)
	private String imageUrl;

	@OneToMany(cascade = CascadeType.PERSIST)
	@Fetch(FetchMode.JOIN)
	@JoinTable(name = "account_images", joinColumns = { @JoinColumn(name = "account_id") },
	    inverseJoinColumns = { @JoinColumn(name = "image_id") })
	private Set<Image> images = new HashSet<Image>();

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
	    CascadeType.REMOVE })
	@JoinTable(name = "account_location", joinColumns = { @JoinColumn(name = "account_id") },
	    inverseJoinColumns = { @JoinColumn(name = "location_id") })
	private Set<Location> locations = new HashSet<Location>();

	@Column(name = "role", insertable = true, updatable = false)
	private String role;

	@Column(name = "status")
	private String status;

	@Column(name = "last_login")
	private Date lastLoginTime;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sender")
	private List<Tweet> tweets = new ArrayList<Tweet>();

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	private Set<PrivacySettings> privacySettings = new HashSet<PrivacySettings>();

	@OneToMany(mappedBy = "recipient", cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
	    fetch = FetchType.LAZY)
	private Set<AccountNotification> accountNotifications = new HashSet<AccountNotification>();

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	private Set<AccountNotificationSettings> notificationSettings =
	    new HashSet<AccountNotificationSettings>();

	private Long uid;

	@Transient
	private Location currentLocation;

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
		setRole(account.getRole());
		setStatus(account.getStatus());
		setLastLoginTime(account.getLastLoginTime());
		setTimeCreated(account.getTimeCreated());
		setTimeUpdated(account.getTimeUpdated());
		this.setUid(account.getUid());

		for (LocationDTO location : account.getLocations()) {
			addLocation(new Location(location));
		}

		for (ImageDTO image : account.getImages()) {
			addImage(new Image(image));
		}

		for (AccountNotificationDTO an : account.getAccountNotifications()) {
			addAccountNotification(new AccountNotification(an));
		}

		for (AccountNotificationSettingsDTO an : account.getNotificationSettings()) {
			notificationSettings.add(new AccountNotificationSettings(an));
		}

		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
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

		Account a = (Account) o;
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
		if (this instanceof PersonalAccount) {
			return ((PersonalAccount) this).getName();
		} else if (this instanceof BusinessAccount) {
			return ((BusinessAccount) this).getName();
		}
		return "";
	}

	public Set<AccountNotificationSettings> getNotificationSettings() {
		return notificationSettings;
	}

	public void setNotificationSettings(Set<AccountNotificationSettings> notifications) {
		this.notificationSettings = notifications;
	}

	public Role getRole() {
		return Role.valueOf(role);
	}

	public void setRole(Role role) {
		this.role = role.name();
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
		return AccountStatus.valueOf(status);
	}

	public void setStatus(AccountStatus status) {
		this.status = status.name();
	}

	public Set<AccountNotification> getAccountNotification() {
		return accountNotifications;
	}

	public void setAccountNotification(Set<AccountNotification> accountNotification) {
		this.accountNotifications = accountNotification;
	}

	public void addAccountNotification(AccountNotification an) {
		this.accountNotifications.add(an);
	}

	public void setLocation(Set<Location> locations) {
		this.locations = locations;
	}

	public void addLocation(Location loc) {
		if (loc != null) {
			locations.add(loc);
		}
	}

	public Set<Location> getLocations() {
		return locations;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	private void addImage(Image image) {
		this.images.add(image);
	}
}
