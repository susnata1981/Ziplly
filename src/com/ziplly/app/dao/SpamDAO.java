package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.SpamDTO;
import com.ziplly.app.server.model.jpa.Spam;

public interface SpamDAO {
	void save(Spam spam);

	List<SpamDTO> getAll();
}
