package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.ziplly.app.model.NeighborhoodDTO;

public class CommunitySummaryWidget extends Composite {

	private static CommunitySummaryWidgetUiBinder uiBinder = GWT
			.create(CommunitySummaryWidgetUiBinder.class);

	interface CommunitySummaryWidgetUiBinder extends UiBinder<Widget, CommunitySummaryWidget> {
	}

	@UiField
	HTMLPanel communitySummaryPanel;
	
	@UiField
	SpanElement communityNameSpan;
	
	@UiField
	HTMLPanel mapPanel;
	
	public CommunitySummaryWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setWidth(String width) {
		communitySummaryPanel.setWidth(width);
	}
	
	public void displaySummaryData(NeighborhoodDTO neighborhood) {
		if (neighborhood != null) {
			communityNameSpan.setInnerText(neighborhood.getName()+","+neighborhood.getCity());
		}
	}
	
	public void displayMap(LatLng ll) {
	    MapOptions myOptions = MapOptions.create();
	    myOptions.setZoom(10.0);
	    myOptions.setCenter(ll);
	    myOptions.setMapMaker(true);
	    myOptions.setMapTypeId(MapTypeId.ROADMAP);

	    GoogleMap map = GoogleMap.create(mapPanel.getElement(), myOptions);
		MarkerOptions markerOpts = MarkerOptions.create();
        markerOpts.setMap(map);
        markerOpts.setPosition(ll);
        Marker.create(markerOpts);
	}
}
