package com.ziplly.app.dao;

import com.ziplly.app.server.model.jpa.Image;

public interface ImageDAO {
	public Image save(Image image);

	public Image findById(Long id);
}
