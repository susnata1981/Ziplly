package com.ziplly.app.client.view.factory;

import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.shared.FieldVerifier;

public class NeighborhoodToStringConverter implements Converter<NeighborhoodDTO, String> {

	@Override
	public String convert(NeighborhoodDTO n) {
		if (n == null) {
			return "";
		}

		if (n.getParentNeighborhood() != null
		    && FieldVerifier.isEmpty(n.getParentNeighborhood().getName())) {
			return n.getName() + ", " + n.getParentNeighborhood().getName();
		} else {
			return n.getName();
		}
	}
}
