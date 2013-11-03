package com.ziplly.app.dao;

import com.ziplly.app.model.Message;
import com.ziplly.app.model.MessageDTO;

public interface MessageDAO {
	void save(Message msg);
}
