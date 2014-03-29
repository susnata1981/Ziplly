package com.ziplly.app.client.widget;

import java.util.TreeMap;

import com.ziplly.app.model.NeighborhoodType;

public interface GooglePlacesDataExporter {
		String getDataOfType(NeighborhoodType type);
		TreeMap<NeighborhoodType, String> getData();
}
