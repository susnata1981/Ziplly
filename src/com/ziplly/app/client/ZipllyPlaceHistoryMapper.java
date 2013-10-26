package com.ziplly.app.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.SignupPlace;

@WithTokenizers({
	HomePlace.Tokenizer.class, LoginPlace.Tokenizer.class, SignupPlace.Tokenizer.class, BusinessSignupPlace.Tokenizer.class, PersonalAccountPlace.Tokenizer.class
})
public interface ZipllyPlaceHistoryMapper extends PlaceHistoryMapper {

}
