package com.ziplly.app.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLatLngResult;

public class GetLatLngActionHandler extends AbstractAccountActionHandler<GetLatLngAction, GetLatLngResult> {

	@Inject
	public GetLatLngActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	private final static String GEO_ENCODING_SERVICE_ENDPOINT = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";
	@Override
	public GetLatLngResult execute(GetLatLngAction action, ExecutionContext arg1)
			throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException();
		}
		
		String restUrl = GEO_ENCODING_SERVICE_ENDPOINT + action.getZip();
		String response = callGeoEncodingService(restUrl);
		return  parse(response);
	}

	private GetLatLngResult parse(String json) {
		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject) parser.parse(json);
		JsonArray results = (JsonArray) o.get("results");
		JsonObject geometry = (JsonObject) ((JsonObject) results.get(0)).get("geometry");
		JsonObject location = (JsonObject) geometry.get("location");
		double lat = location.get("lat").getAsDouble();
		double lng = location.get("lng").getAsDouble();
		GetLatLngResult result = new GetLatLngResult();
		result.setLat(lat);
		result.setLng(lng);
		return result;
	}

	private String callGeoEncodingService(String restUrl) throws InternalError {
		URL url;
		try {
			url = new URL(restUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException();
		}
		try {
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line=br.readLine()) != null) {
				response.append(line);
			}
			br.close();
			return response.toString();
		} catch (IOException e) {
			throw new InternalError();
		} 
	}

	@Override
	public Class<GetLatLngAction> getActionType() {
		return GetLatLngAction.class;
	}

	public static void main(String[] args) {
		
	}
}
