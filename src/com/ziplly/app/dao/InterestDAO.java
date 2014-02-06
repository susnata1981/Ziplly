package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Interest;
import com.ziplly.app.model.InterestDTO;

public interface InterestDAO {
	Interest findInterestByName(String name);
	void save(Interest interest);
	List<InterestDTO> findAll();
}
