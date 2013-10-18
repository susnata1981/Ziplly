package com.ziplly.app.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.ziplly.app.client.places.AccountPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.SignupPlace;

@WithTokenizers({
	HomePlace.Tokenizer.class, AccountPlace.Tokenizer.class, SignupPlace.Tokenizer.class
})
public interface ZipllyPlaceHistoryMapper extends PlaceHistoryMapper {

}
