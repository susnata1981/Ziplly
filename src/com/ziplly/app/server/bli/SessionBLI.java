package com.ziplly.app.server.bli;

import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.model.Session;

public interface SessionBLI {
	public Session validateSession() throws NeedsLoginException;
}
