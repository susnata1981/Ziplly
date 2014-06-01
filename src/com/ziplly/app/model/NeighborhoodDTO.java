package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ziplly.app.server.model.jpa.Neighborhood;

public class NeighborhoodDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long neighborhoodId;
	private String name;
	private String city;
	private String state;
	private String type;
	private List<ImageDTO> images = new ArrayList<ImageDTO>();
	private String imageUrl;
	private NeighborhoodDTO parentNeighborhood;
	private List<PostalCodeDTO> postalCodes = new ArrayList<PostalCodeDTO>();
	private Date timeCreated;
	
	public NeighborhoodDTO() {
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	@Override
	public String toString() {
		return "(" + this.name + ", " + this.city + ", " + this.state + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof NeighborhoodDTO)) {
			return false;
		}

		NeighborhoodDTO n = (NeighborhoodDTO) o;
		return n.getNeighborhoodId() == this.getNeighborhoodId();
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	// to be overridden
	public String getDisplayName() {
		return "<NAME>";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
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

	public NeighborhoodDTO getParentNeighborhood() {
		return parentNeighborhood;
	}

	public void setParentNeighborhood(NeighborhoodDTO parentNeighborhood) {
		this.parentNeighborhood = parentNeighborhood;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<ImageDTO> getImages() {
		return images;
	}

	public void addImage(ImageDTO image) {
		images.add(image);
	}

	public void setImages(List<ImageDTO> images) {
		this.images = images;
	}

	public List<PostalCodeDTO> getPostalCodes() {
		return postalCodes;
	}

	public void addPostalCode(PostalCodeDTO p) {
		this.postalCodes.add(p);
	}

	public void setPostalCodes(List<PostalCodeDTO> postalCodes) {
		this.postalCodes = postalCodes;
	}

	public NeighborhoodType getType() {
		return NeighborhoodType.valueOf(type);
	}

	public void setType(NeighborhoodType type) {
		this.type = type.name();
	}

	public Date getTimeCreated() {
	  return timeCreated;
  }

	public void setTimeCreated(Date timeCreated) {
	  this.timeCreated = timeCreated;
  }
}