package com.ziplly.app.shared;

import com.ziplly.app.client.ApplicationContext.Environment;

import net.customware.gwt.dispatch.shared.Result;

public class GetEnvironmentResult implements Result {
	private Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
