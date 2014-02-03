package com.ziplly.app.model;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamedQueries({
		@NamedQuery(name = "findNeighborhoodById", query = "from Neighborhood n where n.neighborhoodId = :neighborhoodId"),
		@NamedQuery(name = "findAllNeighborhoods", query = "from Neighborhood") })
@Entity
@Table(name = "neighborhood")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class Neighborhood extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "neighborhood_id")
	private Long neighborhoodId;

	private String name;
	private String city;
	private String state;

	@ManyToOne
	@JoinColumn(name="postalcode_id")
	private PostalCode postalCode;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "neighborhood")
	private List<Account> accounts = new ArrayList<Account>();

	public Neighborhood() {
	}

	public Neighborhood(NeighborhoodDTO neighborhood) {
		this.setNeighborhoodId(neighborhood.getNeighborhoodId());
		this.setName(neighborhood.getName());
		this.setCity(neighborhood.getCity());
		this.setState(neighborhood.getState());
		this.setPostalCode(new PostalCode(neighborhood.getPostalCode()));
	}

	public Long getNeighborhoodId() {
		return this.neighborhoodId;
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public PostalCode getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(PostalCode postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Neighborhood)) {
			return false;
		}
		
		Neighborhood n = (Neighborhood)o;
		
		return n.getNeighborhoodId() == neighborhoodId;
	}
	
	@Override
	public int hashCode() {
		return neighborhoodId.hashCode();
	}
}