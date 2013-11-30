package com.ziplly.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="account_registration")
public class AccountRegistration extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private AccountRegistrationStatus status;
	private String email;
	private long code;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getCode() {
		return code;
	}
	public void setCode(long code2) {
		this.code = code2;
	}
	public AccountRegistrationStatus getStatus() {
		return status;
	}
	public void setStatus(AccountRegistrationStatus status) {
		this.status = status;
	}

	public static enum AccountRegistrationStatus {
		ACTIVE,
		INACTIVE
	}
}
