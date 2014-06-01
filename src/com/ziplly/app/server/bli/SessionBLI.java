package com.ziplly.app.server.bli;

import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.server.model.jpa.Session;

public interface SessionBLI {
	public Session validateSession() throws NeedsLoginException;
}
