package com.ziplly.app.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PostalCodeDTO;

public class CommunitySummaryWidget extends AbstractView implements HasClickHandlers {

	private static CommunitySummaryWidgetUiBinder uiBinder = GWT
			.create(CommunitySummaryWidgetUiBinder.class);

	interface CommunitySummaryWidgetUiBinder extends UiBinder<Widget, CommunitySummaryWidget> {
	}

	@UiField
	HTMLPanel communitySummaryPanel;
	
	@UiField
	HTMLPanel mapPanel;
	
	@UiField
	SpanElement communityNameSpan;
	
	@UiField
	Anchor memberCountSpan;
	@UiField
	Anchor businessCountSpan;

	public CommunitySummaryWidget() {
		super(null);
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setWidth(String width) {
		communitySummaryPanel.setWidth(width);
	}
	
	public void displaySummaryData(NeighborhoodDTO neighborhood) {
		if (neighborhood != null) {
			communityNameSpan.setInnerHTML(getName(neighborhood));
		}
	}
	
	public void displayMap(LatLng ll) {
	    MapOptions myOptions = MapOptions.create();
	    myOptions.setZoom(11.0);
	    myOptions.setCenter(ll);
	    myOptions.setMapMaker(true);
	    myOptions.setMapTypeId(MapTypeId.ROADMAP);
	    
	    GoogleMap map = GoogleMap.create(mapPanel.getElement(), myOptions);
		MarkerOptions markerOpts = MarkerOptions.create();
        markerOpts.setMap(map);
        markerOpts.setPosition(ll);
        Marker.create(markerOpts);
	}

	public void setResidentCount(int count) {
		memberCountSpan.setText(count+" members");
	}
	
	public void setBusinessCount(int count) {
		businessCountSpan.setText(count+" businesses");
	}
	
	public void setNeighborhoodName(String name) {
		communityNameSpan.setInnerHTML(name);
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return memberCountSpan.addClickHandler(handler);
	}
	
	public HandlerRegistration addClickHandlerForBusinessLink(ClickHandler handler) {
		return businessCountSpan.addClickHandler(handler);
	}
	
	// TODO
	private String getName(NeighborhoodDTO n) {
		if (n != null) {
			StringBuilder name = new StringBuilder();
//			PostalCodeDTO postalCode = n.getPostalCodes().get(0);
			name.append(n.getName());
			name.append("<br/>");
			
			if (n.getParentNeighborhood() != null) {
				name.append(n.getParentNeighborhood().getName());
				name.append(",&nbsp;");
				name.append(n.getCity());
			} else {
				name.append(n.getCity());
			}
			return name.toString();
		}
		return "";
	}
}
