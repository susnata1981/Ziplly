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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@NamedQueries({
    @NamedQuery(name = "findNeighborhoodById",
        query = "from Neighborhood n where n.neighborhoodId = :neighborhoodId"),
    @NamedQuery(name = "findAllNeighborhoods", query = "from Neighborhood") })
@Entity
@Table(name = "neighborhood")
public class Neighborhood extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "neighborhood_id")
	private Long neighborhoodId;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "parent_neighborhood_id")
	private Neighborhood parentNeighborhood;

	@ManyToMany(mappedBy = "targetNeighborhoods", fetch = FetchType.LAZY)
	private Set<Tweet> tweets = new HashSet<Tweet>();

	@Column(updatable = false)
	private String name;
	@Column(updatable = false)
	private String city;
	@Column(updatable = false)
	private String state;
	@Column(updatable = false)
	private String type;

	@OneToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JoinTable(name = "neighborhood_images", joinColumns = { @JoinColumn(name = "neighborhood_id") },
	    inverseJoinColumns = { @JoinColumn(name = "image_id") })
	private Set<Image> images = new HashSet<Image>();

	@Column(name = "image_url")
	private String imageUrl;

	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinTable(name = "neighborhood_postalcode",
	    joinColumns = { @JoinColumn(name = "neighborhood_id") }, inverseJoinColumns = { @JoinColumn(
	        name = "postal_code") })
	private Set<PostalCode> postalCodes = new HashSet<PostalCode>();

	public Neighborhood() {
	}

	public Neighborhood(NeighborhoodDTO neighborhood) {
		this.setNeighborhoodId(neighborhood.getNeighborhoodId());
		this.setName(neighborhood.getName());
		this.type = neighborhood.getType().name();
		this.timeCreated = neighborhood.getTimeCreated();
		
		if (neighborhood.getCity() != null) {
			this.setCity(neighborhood.getCity());
		}
		
		if (neighborhood.getState() != null) {
			this.setState(neighborhood.getState());
		}
		
		if (neighborhood.getParentNeighborhood() != null) {
			this.parentNeighborhood = new Neighborhood(neighborhood.getParentNeighborhood());
		}

		for (PostalCodeDTO p : neighborhood.getPostalCodes()) {
			this.postalCodes.add(new PostalCode(p));
		}

		this.setImageUrl(neighborhood.getImageUrl());

		for (ImageDTO image : neighborhood.getImages()) {
			this.addImage(new Image(image));
		}
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

	// Return empty string in case it's null.
	public String getCity() {
		return city == null ? "" : city;
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

	public Set<Image> getImages() {
		return images;
	}

	public void addImage(Image image) {
		images.add(image);
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Neighborhood)) {
			return false;
		}

		Neighborhood n = (Neighborhood) o;

		return n.getNeighborhoodId() == neighborhoodId;
	}

	@Override
	public int hashCode() {
		return neighborhoodId.hashCode();
	}

	public void addPostalCode(PostalCode p) {
		this.postalCodes.add(p);
	}

	public Set<PostalCode> getPostalCodes() {
		return postalCodes;
	}

	public void setPostalCodes(Set<PostalCode> postalCodes) {
		this.postalCodes = postalCodes;
	}

	public NeighborhoodType getType() {
		return NeighborhoodType.valueOf(type);
	}

	public void setType(NeighborhoodType type) {
		this.type = type.name();
	}
}