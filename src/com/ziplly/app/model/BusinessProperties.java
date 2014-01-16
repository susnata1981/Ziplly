package com.ziplly.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@NamedQueries({ 
	@NamedQuery(
		name = "findBusinessById", 
		query = "from BusinessProperties b where b.id = :id") 
})
@Entity
@Table(name = "business_properties")
public class BusinessProperties extends AbstractTimestampAwareEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "sunday_start_time")
	private String sundayStartTime;
	@Column(name = "sunday_end_time")
	private String sundayEndTime;

	@Column(name = "monday_start_time")
	private String mondayStartTime;
	@Column(name = "monday_end_time")
	private String mondayEndTime;

	@Column(name = "tuesday_start_time")
	private String tuesdayStartTime;
	@Column(name = "tuesday_end_time")
	private String tuesdayEndTime;

	@Column(name = "wednesday_start_time")
	private String wednesdayStartTime;
	@Column(name = "wednesday_end_time")
	private String wednesdayEndTime;

	@Column(name = "thursday_start_time")
	private String thursdayStartTime;
	@Column(name = "thursday_end_time")
	private String thursdayEndTime;

	@Column(name = "friday_start_time")
	private String fridayStartTime;
	@Column(name = "friday_end_time")
	private String fridayEndTime;

	@Column(name = "saturday_start_time")
	private String saturdayStartTime;
	@Column(name = "saturday_end_time")
	private String saturdayEndTime;

	@Column(name = "holidays")
	private String holidays;

	@Column(name = "accepts_credit_card", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
	private boolean acceptsCreditCard;

	@Column(name = "parking_facility")
	private String partkingFacility;

	@Column(name = "wifi_available", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
	private boolean wifiAvailable;

	@Column(name = "good_for_kids", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
	private boolean goodForKids;

	@Column(name = "price_range")
	private PriceRange priceRange;

	@OneToOne(mappedBy = "properties")
	private BusinessAccount seller;

	@Column(name = "cuisine")
	private Cuisine cuisine;
	
	public BusinessProperties() {
	}

	public BusinessProperties(BusinessPropertiesDTO businessAccountProperties) {
		if (businessAccountProperties != null) {
			this.setId(businessAccountProperties.getId());
			this.setSundayStartTime(businessAccountProperties.getSundayStartTime());
			this.setSundayEndTime(businessAccountProperties.getSundayEndTime());
			this.setMondayStartTime(businessAccountProperties.getMondayStartTime());
			this.setMondayEndTime(businessAccountProperties.getMondayEndTime());
			this.setTuesdayStartTime(businessAccountProperties.getTuesdayStartTime());
			this.setTuesdayEndTime(businessAccountProperties.getTuesdayEndTime());
			this.setWednesdayStartTime(businessAccountProperties.getWednesdayStartTime());
			this.setWednesdayEndTime(businessAccountProperties.getWednesdayEndTime());
			this.setThursdayStartTime(businessAccountProperties.getThursdayStartTime());
			this.setThursdayEndTime(businessAccountProperties.getThursdayEndTime());
			this.setFridayStartTime(businessAccountProperties.getFridayStartTime());
			this.setFridayEndTime(businessAccountProperties.getFridayEndTime());
			this.setSaturdayStartTime(businessAccountProperties.getSaturdayStartTime());
			this.setSaturdayEndTime(businessAccountProperties.getSaturdayEndTime());
			this.setHolidays(businessAccountProperties.getHolidays());
			this.setAcceptsCreditCard(businessAccountProperties.getAcceptsCreditCard());
			this.setPartkingFacility(businessAccountProperties.getPartkingFacility());
			this.setWifiAvailable(businessAccountProperties.getWifiAvailable());
			this.setGoodForKids(businessAccountProperties.getGoodForKids());
			this.setPriceRange(businessAccountProperties.getPriceRange());
			this.setCuisine(businessAccountProperties.getCuisine());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// Sunday
	public String getSundayStartTime() {
		return sundayStartTime;
	}

	public void setSundayStartTime(String sundayStartTime) {
		this.sundayStartTime = sundayStartTime;
	}

	public String getSundayEndTime() {
		return sundayEndTime;
	}

	public void setSundayEndTime(String sundayEndTime) {
		this.sundayEndTime = sundayEndTime;
	}

	// Monday
	public String getMondayStartTime() {
		return mondayStartTime;
	}

	public void setMondayStartTime(String mondayStartTime) {
		this.mondayStartTime = mondayStartTime;
	}

	public String getMondayEndTime() {
		return mondayEndTime;
	}

	public void setMondayEndTime(String mondayEndTime) {
		this.mondayEndTime = mondayEndTime;
	}

	// Tuesday

	public String getTuesdayStartTime() {
		return tuesdayStartTime;
	}

	public void setTuesdayStartTime(String tuesdayStartTime) {
		this.tuesdayStartTime = tuesdayStartTime;
	}

	public String getTuesdayEndTime() {
		return tuesdayEndTime;
	}

	public void setTuesdayEndTime(String tuesdayEndTime) {
		this.tuesdayEndTime = tuesdayEndTime;
	}

	public String getWednesdayStartTime() {
		return wednesdayStartTime;
	}

	public void setWednesdayStartTime(String wednesdayStartTime) {
		this.wednesdayStartTime = wednesdayStartTime;
	}

	public String getWednesdayEndTime() {
		return wednesdayEndTime;
	}

	public void setWednesdayEndTime(String wednesdayEndTime) {
		this.wednesdayEndTime = wednesdayEndTime;
	}

	// Thursday
	public String getThursdayStartTime() {
		return thursdayStartTime;
	}

	public void setThursdayStartTime(String thursdayStartTime) {
		this.thursdayStartTime = thursdayStartTime;
	}

	public String getThursdayEndTime() {
		return thursdayEndTime;
	}

	public void setThursdayEndTime(String thursdayEndTime) {
		this.thursdayEndTime = thursdayEndTime;
	}

	public String getFridayStartTime() {
		return fridayStartTime;
	}

	public void setFridayStartTime(String fridayStartTime) {
		this.fridayStartTime = fridayStartTime;
	}

	public String getFridayEndTime() {
		return fridayEndTime;
	}

	public void setFridayEndTime(String fridayEndTime) {
		this.fridayEndTime = fridayEndTime;
	}

	// Saturday
	public String getSaturdayStartTime() {
		return saturdayStartTime;
	}

	public void setSaturdayStartTime(String saturdayStartTime) {
		this.saturdayStartTime = saturdayStartTime;
	}

	public String getSaturdayEndTime() {
		return saturdayEndTime;
	}

	public void setSaturdayEndTime(String saturdayEndTime) {
		this.saturdayEndTime = saturdayEndTime;
	}

	// holidays
	public String getHolidays() {
		return holidays;
	}

	public void setHolidays(String holidays) {
		this.holidays = holidays;
	}

	// acceptsCreditCard
	public boolean getAcceptsCreditCard() {
		return acceptsCreditCard;
	}

	public void setAcceptsCreditCard(boolean acceptsCreditCard) {
		this.acceptsCreditCard = acceptsCreditCard;
	}

	// parkingFacility
	public String getPartkingFacility() {
		return partkingFacility;
	}

	public void setPartkingFacility(String partkingFacility) {
		this.partkingFacility = partkingFacility;
	}

	// wifiAvailable
	public boolean getWifiAvailable() {
		return wifiAvailable;
	}

	public void setWifiAvailable(boolean wifiAvailable) {
		this.wifiAvailable = wifiAvailable;
	}

	// goodForKids
	public boolean getGoodForKids() {
		return goodForKids;
	}

	public void setGoodForKids(boolean goodForKids) {
		this.goodForKids = goodForKids;
	}

	public PriceRange getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(PriceRange priceRange) {
		this.priceRange = priceRange;
	}

	public Cuisine getCuisine() {
		return cuisine;
	}

	public void setCuisine(Cuisine cuisine) {
		this.cuisine = cuisine;
	}
}
