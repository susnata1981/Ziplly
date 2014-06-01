package com.ziplly.app.server.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ziplly.app.model.AccountType;
import com.ziplly.app.model.BusinessType;

@Entity
@Table(name = "account_registration")
public class AccountRegistration extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String status;

	@Column(name = "account_type")
	private String accountType;

	@Column(name = "business_type")
	private String businessType;

	private String email;

	@Column(name = "account_id")
	private Long accountId;

	@Column(length = 512)
	private String code;

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

	public AccountRegistrationStatus getStatus() {
		return AccountRegistrationStatus.valueOf(status);
	}

	public void setStatus(AccountRegistrationStatus status) {
		this.status = status.name();
	}

	public AccountType getAccountType() {
		return AccountType.valueOf(accountType);
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType.name();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public BusinessType getBusinessType() {
		return BusinessType.valueOf(businessType);
	}

	public void setBusinessType(BusinessType type) {
		this.businessType = type.name();
	}

	public static enum AccountRegistrationStatus {
		ACTIVE, INACTIVE, UNUSED, USED;
	}
}
