package com.ziplly.app.client.view.community;

import com.ziplly.app.model.EntityType;
import com.ziplly.app.shared.GetEntityListAction.SearchType;

public class BusinessViewState extends CommunityViewState {
  private String postalCode;
  
  public BusinessViewState(EntityType type, long neighborhoodId, String postalCode) {
    super(type, neighborhoodId);
    this.postalCode = postalCode;
  }

  public void searchByPostalCode(String postalCode) {
    reset();
    this.postalCode = postalCode;
    action.setSearchType(SearchType.BY_POSTALCODE);
    action.setPostalCode(postalCode);
    action.setPage(start);
  }

  public String getPostalCode() {
    return postalCode;
  }
}
