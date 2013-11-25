package com.ziplly.app.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
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
		
		AccountDTO account = action.getAccount();
		String restUrl ="";
		try {
			restUrl = getGeoEncodingServiceEndpoint(account);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String response = callGeoEncodingService(restUrl);
		return  parse(response);
	}

	private String getGeoEncodingServiceEndpoint(AccountDTO acct) throws UnsupportedEncodingException {
		if (acct == null) {
			throw new IllegalArgumentException();
		}
		StringBuilder response = new StringBuilder("");
		if (acct instanceof PersonalAccountDTO) {
			response.append(acct.getZip());
		} 
		else if (acct instanceof BusinessAccountDTO) {
			BusinessAccountDTO baccount = (BusinessAccountDTO)acct;
			if (baccount.getStreet1() != null) {
				response.append(baccount.getStreet1());
			}
			response.append(" ");
			
			if (baccount.getStreet2() != null) {
				response.append(baccount.getStreet2());
			}
			response.append(" "+baccount.getZip());
		}
		return GEO_ENCODING_SERVICE_ENDPOINT + URLEncoder.encode(response.toString(),"UTF-8"); 
	}

	private GetLatLngResult parse(String json) {
		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject) parser.parse(json);
		JsonArray results = (JsonArray) o.get("results");
		JsonObject jo = (JsonObject) results.get(0);
		String formattedAddress = jo.get("formatted_address").toString();
		JsonObject geometry = (JsonObject) jo.get("geometry");
		JsonObject location = (JsonObject)geometry.get("location");
		double lat = location.get("lat").getAsDouble();
		double lng = location.get("lng").getAsDouble();
		GetLatLngResult result = new GetLatLngResult();
		result.setFormattedAddress(formattedAddress.replaceAll("\"", ""));
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
