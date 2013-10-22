package com.ziplly.app.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries(
	@NamedQuery(
		name="findInterestByName",
		query = "from Interest i where i.name = :name"
	)
)
@Entity
@Table(name="interest")
public class Interest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="interest_id")
	private Long interestId;

	// Activity name in lowercase
	private String name;
	
	@ManyToMany(mappedBy = "interests", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	private Set<PersonalAccount> accounts = new HashSet<PersonalAccount>();
	private Date timeCreated;	

	public Interest() {
	}
	
	public Interest(InterestDTO i) {
		this.interestId = i.getInterestId();
		this.setName(i.getName());
		this.timeCreated = i.getTimeCreated();
	}
	
	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
