package com.ziplly.app.client.activities;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public interface Presenter {
	/*
	 * Fetches data for the view
	 */
	void fetchData();
	/*
	 * Attaches the ui
	 */
	void updateUi();
	/*
	 * Display the view on the container
	 */
	void go(AcceptsOneWidget container);
	
	/*
	 * Set a 2 way communication with the view
	 */
	void bind();
	/*
	 * Go to a place
	 */
	void goTo(Place place);
}
