package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.PendingInvitations;
import com.ziplly.app.model.PendingInvitationsDTO;

public interface PendingInvitationsDAO {
	void save(PendingInvitations pi);
	List<PendingInvitationsDTO> findAll();
}
