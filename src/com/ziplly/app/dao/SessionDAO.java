package com.ziplly.app.dao;

import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.server.model.jpa.Session;

public interface SessionDAO {
	Session findSessionByUid(Long uid) throws NotFoundException;

	Session findSessionByAccountId(Long accountId) throws NotFoundException;;

	void save(Session session);

	void removeByUid(Long uid) throws NotFoundException;

	void removeByAccountId(Long accountId) throws NotFoundException;
}
