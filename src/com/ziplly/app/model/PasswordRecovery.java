package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
    @NamedQuery(name = "findPasswordRecoverByHash",
        query = "from PasswordRecovery where hash = :hash and status = :status"),
    @NamedQuery(name = "findPasswordRecoverByEmail",
        query = "from PasswordRecovery where email = :email and status = :status") })
@Entity
@Table(name = "password_recovery")
public class PasswordRecovery implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String hash;
	private String email;
	private String status;
	private Date timeCreated;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public PasswordRecoveryStatus getStatus() {
		return PasswordRecoveryStatus.valueOf(status);
	}

	public void setStatus(PasswordRecoveryStatus status) {
		this.status = status.name();
	}
}
