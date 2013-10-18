package com.ziplly.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@NamedQueries({
		@NamedQuery(name = "fetchSessionByUid", query = "from Session s where s.uid = :uid"),
		@NamedQuery(name = "fetchSessionByAccountId", query = "from Session s where s.account.accountId = :account_id") })
@Entity
@Table(name = "session")
public class Session {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long uid;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="account_id")
	@NotNull
	private Account account;
	@Column(name = "time_created")
	private Date timeCreated;
	@Column(name = "expired_at")
	private Date expireAt;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uuid) {
		this.uid = uuid;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Date expireAt) {
		this.expireAt = expireAt;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
}
