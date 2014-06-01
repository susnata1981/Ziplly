package com.ziplly.app.dao;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.server.model.jpa.Location;

public interface LocationDAO {
	Location findById(Long locationId) throws NotFoundException;
}
