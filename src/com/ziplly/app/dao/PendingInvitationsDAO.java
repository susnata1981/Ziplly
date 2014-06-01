package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.PendingInvitationsDTO;
import com.ziplly.app.server.model.jpa.PendingInvitations;

public interface PendingInvitationsDAO {
	void save(PendingInvitations pi);

	List<PendingInvitationsDTO> findAll();
}
