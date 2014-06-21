package com.ziplly.app.client.widget;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.NeighborhoodType;
import com.ziplly.app.model.PostalCodeDTO;

public class NeighborhoodBuilder {
	private GooglePlacesDataExporter exporter;
	private String EMPTY_STRING = "";
	
	public NeighborhoodBuilder(GooglePlacesDataExporter exporter) {
		this.exporter = exporter;
  }
	
	public NeighborhoodDTO getNeighborhood() {
		Date now = new Date();
		TreeMap<NeighborhoodType, String> addressInfoMap = exporter.getData();
		String state = addressInfoMap.get(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_1);
		String postalCode = addressInfoMap.get(NeighborhoodType.POSTAL_CODE);
		String locality = getLocality(addressInfoMap);

		NeighborhoodDTO curr = null;
		NeighborhoodDTO innerMostNeighborhood = null;

		for (NeighborhoodType type : addressInfoMap.keySet()) {
			NeighborhoodDTO n = null;
			if (type == NeighborhoodType.POSTAL_CODE) {
				continue;
			} else if (type == NeighborhoodType.COUNTRY) {
				// State should be empty for the country
				n = createNeighborhood(addressInfoMap.get(type), EMPTY_STRING, EMPTY_STRING, now, type);
			} else if (type == NeighborhoodType.NEIGHBORHOOD) {
				n = createNeighborhood(addressInfoMap.get(type), locality, state, now, type);
				n.addPostalCode(createPostalCode(postalCode, EMPTY_STRING, state));
			} else {
				n = createNeighborhood(addressInfoMap.get(type), EMPTY_STRING, state, now, type);
			}

			if (curr != null) {
				curr.setParentNeighborhood(n);
			} else {
				innerMostNeighborhood = n;
			}
			curr = n;
		}
		return innerMostNeighborhood;
	}

	public NeighborhoodDTO getTemplateNeighborhoodWithParent() {
		NeighborhoodDTO parentNeighborhood = getNeighborhood();
		NeighborhoodDTO neighborhood = 
				createNeighborhood(
		        null,
		        parentNeighborhood.getName(),
		        parentNeighborhood.getState(),
		        new Date(),
		        NeighborhoodType.NEIGHBORHOOD);
		neighborhood.setParentNeighborhood(parentNeighborhood);
		neighborhood.addPostalCode(createPostalCode(exporter.getData()));
		return neighborhood;
	}

	private static NeighborhoodDTO createNeighborhood(String name,
	    String locality,
	    String state,
	    Date date,
	    NeighborhoodType type) {
		NeighborhoodDTO parent = new NeighborhoodDTO();
		parent.setName(name);
		parent.setCity(locality);
		parent.setState(state);
		parent.setTimeCreated(date);
		parent.setType(type);
		return parent;
	}

	private static PostalCodeDTO createPostalCode(String postalcode, String locality, String state) {
		PostalCodeDTO p = new PostalCodeDTO();
		p.setPostalCode(postalcode);
		p.setState(state);
		p.setCity(locality);
		p.setFullState(state);
		p.setLatitude("");
		p.setLongitude("");
		return p;
	}

//	private static String getLocality(Map<NeighborhoodType, String> addressInfoMap) {
//		boolean flag = false;
//		for (NeighborhoodType ntype : addressInfoMap.keySet()) {
//			if (flag) {
//				return addressInfoMap.get(ntype);
//			}
//
//			if (ntype == NeighborhoodType.NEIGHBORHOOD) {
//				flag = true;
//			}
//		}
//		return "";
//	}

	private static String getLocality(Map<NeighborhoodType, String> addressInfoMap) {
    if (addressInfoMap.containsKey(NeighborhoodType.LOCALITY)) {
      return addressInfoMap.get(NeighborhoodType.LOCALITY);
    }
    else if (addressInfoMap.containsKey(NeighborhoodType.SUB_LOCALITY)) {
      return addressInfoMap.get(NeighborhoodType.SUB_LOCALITY);
    }
    
    throw new RuntimeException("Invalid Address");
  }
	
	private static PostalCodeDTO createPostalCode(TreeMap<NeighborhoodType, String> addressInfoMap) {
		PostalCodeDTO p = new PostalCodeDTO();
		p.setPostalCode(addressInfoMap.get(NeighborhoodType.POSTAL_CODE));
		p.setState(addressInfoMap.get(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_1));
		p.setCity("");
		p.setFullState(addressInfoMap.get(NeighborhoodType.ADMINISTRATIVE_AREA_LEVEL_1));
		p.setLatitude("");
		p.setLongitude("");
		return p;
	}
}
