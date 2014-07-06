package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;

public class BusinessPlace extends Place {
	public static final String TOKEN = "business";
	private long accountId;
	private long neighborhoodId;
	private String postalCode = "";

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public Long getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Prefix("business")
	public static class Tokenizer extends BaseTokenizer<BusinessPlace> {
		
	  @Override
    public BusinessPlace getPlace(String token) {
	    try {
	      BusinessPlace place = new BusinessPlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          AttributeValue attrValue = params.get(key);
          if (key.equals(AttributeKey.SEND_MESSAGE_TOKEN)) {
            place.setAccountId(Long.parseLong(attrValue.value()));
          } else if (key.equals(AttributeKey.NEIGHBORHOOD_ID)) {
            place.setNeighborhoodId(Long.parseLong(params.get(key).value()));
          } else if (key.equals(AttributeKey.POSTAL_CODE)) {
            place.setPostalCode(attrValue.value());
          }
        }
        
        return place;
      } catch (Exception ex) {
        return new BusinessPlace();
      }
    }
	  
		@Override
		public String getToken(BusinessPlace place) {
		  return PlaceUtils.getPlaceToken(place);
		}
	}
}
