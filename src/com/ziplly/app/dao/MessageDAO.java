package com.ziplly.app.dao;

import com.ziplly.app.server.model.jpa.Message;

public interface MessageDAO {
	void save(Message msg);
}
