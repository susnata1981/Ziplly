package com.ziplly.app.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.LocationDTO;
import com.ziplly.app.model.LocationType;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLatLngResult;
import com.ziplly.app.shared.GetLatLngResult.Status;

public class GetLatLngActionHandler extends
    AbstractAccountActionHandler<GetLatLngAction, GetLatLngResult> {

	@Inject
	public GetLatLngActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao, 
			SessionDAO sessionDao, 
			AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	// TODO remove hardcoding.
	private final static String GEO_ENCODING_SERVICE_ENDPOINT =
	    "http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";

	private Logger logger = Logger.getLogger(GetLatLngActionHandler.class.getCanonicalName());
	
	@Override
	public GetLatLngResult
	    doExecute(GetLatLngAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null) {
			throw new IllegalArgumentException();
		}

		logger.log(Level.INFO, String.format("Calling geo encoding service endpoint %s ", GEO_ENCODING_SERVICE_ENDPOINT));

		AccountDTO account = action.getAccount();
		String restUrl = "";
		try {
			restUrl = getGeoEncodingServiceEndpoint(account);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String response = callGeoEncodingService(restUrl);
		GetLatLngResult parse = parse(response);
		if (parse == null) {
			GetLatLngResult result = new GetLatLngResult();
			result.setStatus(Status.ERROR);
			return result;
		} else {
			parse.setStatus(Status.SUCCESS);
		}
		
		logger.log(Level.INFO, String.format("Received latlng result %f, %f with Status %s ",
				parse.getLat(),
				parse.getLng(),
				parse.getStatus().name()));
		
		return parse;
	}

	private String getGeoEncodingServiceEndpoint(AccountDTO acct) throws UnsupportedEncodingException {
		if (acct == null) {
			throw new IllegalArgumentException();
		}
		StringBuilder response = new StringBuilder("");
		LocationDTO location = getPrimaryLocation(acct);
		if (acct instanceof PersonalAccountDTO) {
			// Changed
			response.append(location.getNeighborhood().getPostalCodes().get(0).getPostalCode());
		} else if (acct instanceof BusinessAccountDTO) {
			// BusinessAccountDTO baccount = (BusinessAccountDTO) acct;
			// LocationDTO location = getCurrentLocation(baccount);
			if (location != null && location.getAddress() != null) {
				response.append(location.getAddress());
			}
			// if (baccount.getStreet1() != null) {
			// response.append(baccount.getStreet1());
			// }
			response.append(" ");

			// if (baccount.getStreet2() != null) {
			// response.append(baccount.getStreet2());
			// }
			// response.append(" " + baccount.getZip());
			response.append(" " + location.getNeighborhood().getPostalCodes().get(0).getPostalCode());
		}
		return GEO_ENCODING_SERVICE_ENDPOINT + URLEncoder.encode(response.toString(), "UTF-8");
	}

	private LocationDTO getPrimaryLocation(AccountDTO acct) {
		if (acct instanceof PersonalAccountDTO) {
			return acct.getLocations().get(0);
		} else {
			for (LocationDTO loc : acct.getLocations()) {
				if (loc.getType() == LocationType.PRIMARY) {
					return loc;
				}
			}
		}

		return null;
	}

	// private LocationDTO getCurrentLocation(BusinessAccountDTO baccount) {
	// long neighborhoodId = session.getNeighborhood().getNeighborhoodId();
	// for (LocationDTO loc : baccount.getLocations()) {
	// if (loc.getNeighborhood().getNeighborhoodId() == neighborhoodId) {
	// return loc;
	// }
	// }
	// return null;
	// }

	private GetLatLngResult parse(String json) {
		JsonParser parser = new JsonParser();
		JsonObject o = (JsonObject) parser.parse(json);
		JsonArray results = (JsonArray) o.get("results");
		if (results.size() > 0) {
			JsonObject jo = (JsonObject) results.get(0);
			String formattedAddress = jo.get("formatted_address").toString();
			JsonObject geometry = (JsonObject) jo.get("geometry");
			JsonObject location = (JsonObject) geometry.get("location");
			double lat = location.get("lat").getAsDouble();
			double lng = location.get("lng").getAsDouble();
			GetLatLngResult result = new GetLatLngResult();
			result.setFormattedAddress(formattedAddress.replaceAll("\"", ""));
			result.setLat(lat);
			result.setLng(lng);
			return result;
		}
		return null;
	}

	private String callGeoEncodingService(String restUrl) throws InternalException {
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
			while ((line = br.readLine()) != null) {
				response.append(line);
			}
			br.close();
			return response.toString();
		} catch (IOException e) {
			throw new InternalException();
		}
	}

	@Override
	public Class<GetLatLngAction> getActionType() {
		return GetLatLngAction.class;
	}

	public static void main(String[] args) {
	}
}
