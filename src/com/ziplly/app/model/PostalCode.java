package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamedQueries({
		@NamedQuery(name = "findPostalCodeById", query = "from PostalCode p where p.postalCodeId = :postalCodeId"),
		@NamedQuery(name = "findAllPostalCodes", query = "from PostalCode") })
@Entity
@Table(name = "postal_code")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class PostalCode extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long postalCodeId;
	
	@Column(name="code")
	private String postalCode;

	@OneToMany(mappedBy = "postalCode")
	List<Neighborhood> neighborhoods = new ArrayList<Neighborhood>();

	public PostalCode() {
	}

	public PostalCode(PostalCodeDTO postalCode) {
		this.setPostalCode(postalCode.getPostalCode());
	}

	public Long getPostalCodeId() {
		return this.postalCodeId;
	}

	public void setPostalCodeId(Long postalCodeId) {
		this.postalCodeId = postalCodeId;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
}