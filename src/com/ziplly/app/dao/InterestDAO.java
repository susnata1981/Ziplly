package com.ziplly.app.dao;

import com.ziplly.app.model.Interest;

public interface InterestDAO {
	Interest findInterestByName(String name);
	void save(Interest interest);
}
