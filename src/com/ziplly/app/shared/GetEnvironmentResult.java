package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.client.ApplicationContext.Environment;

public class GetEnvironmentResult implements Result {
	private Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
