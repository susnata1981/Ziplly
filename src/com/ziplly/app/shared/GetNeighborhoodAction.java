package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class GetNeighborhoodAction implements Action<GetNeighborhoodResult>{

	private NeighborhoodSearchActionType searchType;
	private String postalCode;
	private int start;
	private int maxResults;
	
	public GetNeighborhoodAction() {
	}
	
	public GetNeighborhoodAction(String postalCode2) {
		this.setPostalCode(postalCode2);
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public NeighborhoodSearchActionType getSearchType() {
		return searchType;
	}

	public void setSearchType(NeighborhoodSearchActionType searchType) {
		this.searchType = searchType;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
