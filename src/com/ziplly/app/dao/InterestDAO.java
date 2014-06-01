package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.server.model.jpa.Interest;

public interface InterestDAO {
	Interest findInterestByName(String name);

	void save(Interest interest);

	List<InterestDTO> findAll();
}
