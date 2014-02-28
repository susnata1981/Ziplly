package com.ziplly.app.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamedQueries({
		@NamedQuery(name = "findNeighborhoodById", query = "from Neighborhood n where n.neighborhoodId = :neighborhoodId"),
		@NamedQuery(name = "findAllNeighborhoods", query = "from Neighborhood") })
@Entity
@Table(name = "neighborhood")
public class Neighborhood extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "neighborhood_id")
	private Long neighborhoodId;

	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="parent_neighborhood_id")
	private Neighborhood parentNeighborhood;
	
	@ManyToMany(mappedBy="targetNeighborhoods", fetch = FetchType.LAZY)
	private Set<Tweet> tweets = new HashSet<Tweet>();
	
	private String name;
	private String city;
	private String state;
	@Column(name="image_url")
	private String imageUrl;
	
	//TODO : change this to ManyToMany
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="postalcode_id", nullable = false)
	private PostalCode postalCode;

	public Neighborhood() {
	}

	public Neighborhood(NeighborhoodDTO neighborhood) {
		this.setNeighborhoodId(neighborhood.getNeighborhoodId());
		this.setName(neighborhood.getName());
		this.setCity(neighborhood.getCity());
		this.setState(neighborhood.getState());
		
		if (neighborhood.getParentNeighborhood() != null) {
			this.parentNeighborhood = new Neighborhood(neighborhood.getParentNeighborhood());
		}
		
		if (neighborhood.getPostalCode() != null) {
			this.setPostalCode(new PostalCode(neighborhood.getPostalCode()));
		}
		
		this.setImageUrl(neighborhood.getImageUrl());
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

	public Neighborhood getParentNeighborhood() {
		return parentNeighborhood;
	}

	public void setParentNeighborhood(Neighborhood parentNeighborhood) {
		this.parentNeighborhood = parentNeighborhood;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}