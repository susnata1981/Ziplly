package com.ziplly.app.client.places;

import java.util.Map;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.Prefix;
import com.ziplly.app.model.Gender;

public class ResidentPlace extends Place {
  public static final String TOKEN = "residents";
	private long accountId;
	private long neighborhoodId;
	private Gender gender = Gender.ALL;

	public ResidentPlace() {
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getNeighborhoodId() {
		return neighborhoodId;
	}

	public void setNeighborhoodId(long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Prefix("residents")
	public static class Tokenizer extends BaseTokenizer<ResidentPlace> {

	  @Override
    public ResidentPlace getPlace(String token) {
      try {
        ResidentPlace place = new ResidentPlace();
        Map<AttributeKey, AttributeValue> params = parser.parse(token);
        
        for(AttributeKey key: params.keySet()) {
          AttributeValue attrValue = params.get(key);
          if (key.equals(AttributeKey.SEND_MESSAGE_TOKEN)) {
            place.setAccountId(Long.parseLong(attrValue.value()));
          } else if (key.equals(AttributeKey.NEIGHBORHOOD_ID)) {
            place.setNeighborhoodId(Long.parseLong(params.get(key).value()));
          } else if (key.equals(AttributeKey.GENDER_KEY)) {
            place.setGender(Gender.valueOf(attrValue.value()));
          } 
        }
        
        return place;
      } catch (Exception ex) {
        return new ResidentPlace();
      }
    }
		@Override
		public String getToken(ResidentPlace place) {
		  return PlaceUtils.getPlaceToken(place);
		}
	}
}
