package com.ziplly.app.client.widget;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;

public class GoogleMapWidget {
	private GoogleMap map;
	private Geocoder geocoder;

	public void displayMap(Element element, String address) {
	  geocoder = Geocoder.create();
    MapOptions myOptions = MapOptions.create();
    myOptions.setZoom(11.0);
    myOptions.setCenter(LatLng.create(-34.397, 150.644));
    myOptions.setMapTypeId(MapTypeId.ROADMAP);
    myOptions.setMapTypeControl(false);
    myOptions.setNoClear(true);
    map = GoogleMap.create(element, myOptions);
    codeAddress(address);
  }

	public void resize() {
		LatLng center = map.getCenter();
		resizeMap(map);
		map.setCenter(center);
	}
	
	final native void resizeMap(GoogleMap map) /*-{
  		$wnd.google.maps.event.trigger(map, 'resize');
	}-*/;
	
  private void codeAddress(final String address) {
  	if (address == null) {
  		return;
  	}
  	
    GeocoderRequest request = GeocoderRequest.create();
    request.setAddress(address);
    geocoder.geocode(request, new Callback() {
      public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
        if (status == GeocoderStatus.OK) {
          GeocoderResult location = results.get(0);
          map.setCenter(location.getGeometry().getLocation());
          MarkerOptions markerOpts = MarkerOptions.create();
          markerOpts.setMap(map);
          markerOpts.setPosition(location.getGeometry().getLocation());
          Marker.create(markerOpts);
        } else {
          Window.alert("Geocode was not successful for the following reason: "
              + status.getValue());
        }
      }
    });
  }
}
