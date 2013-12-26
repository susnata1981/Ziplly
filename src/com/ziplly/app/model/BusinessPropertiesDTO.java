//SriAndal
package com.ziplly.app.model;

import java.io.Serializable;

public class BusinessPropertiesDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String sundayStartTime;
	private String sundayEndTime;

	private String mondayStartTime;
	private String mondayEndTime;

	private String tuesdayStartTime;
	private String tuesdayEndTime;

	private String wednesdayStartTime;
	private String wednesdayEndTime;

	private String thursdayStartTime;
	private String thursdayEndTime;

	private String fridayStartTime;
	private String fridayEndTime;

	private String saturdayStartTime;
	private String saturdayEndTime;

	private String holidays;

	private boolean acceptsCreditCard;

	private String partkingFacility;

	private boolean wifiAvailable;

	private boolean goodForKids;

	private PriceRange priceRange;

	public BusinessPropertiesDTO() {
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

	// Wednesday
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

	// Friday
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

	@Override
	public String toString() {
		return "(" + this.id + "," + this.sundayStartTime + ","
				+ this.sundayEndTime + "," + this.mondayStartTime + "," + this.mondayEndTime + ","
				+ this.tuesdayStartTime + "," + this.tuesdayEndTime + "," + this.wednesdayStartTime
				+ "," + this.wednesdayEndTime + "," + this.thursdayStartTime + ","
				+ this.thursdayEndTime + "," + this.fridayStartTime + "," + this.fridayEndTime
				+ "," + this.saturdayStartTime + "," + this.saturdayEndTime + "," + this.holidays
				+ "," + String.valueOf(this.acceptsCreditCard) + "," + this.partkingFacility + ","
				+ String.valueOf(this.wifiAvailable) + "," + String.valueOf(this.goodForKids) + ","
				+ this.getPriceRange() + "," + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof BusinessProperties)) {
			return false;
		}

		BusinessProperties n = (BusinessProperties) o;
		return n.getId() == this.getId();
	}

	public PriceRange getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(PriceRange priceRange) {
		this.priceRange = priceRange;
	}
}