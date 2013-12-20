package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.Spam;
import com.ziplly.app.model.SpamDTO;

public interface SpamDAO {
	void save(Spam spam);

	List<SpamDTO> getAll();
}
