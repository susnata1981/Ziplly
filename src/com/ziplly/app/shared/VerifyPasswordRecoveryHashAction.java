package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

public class VerifyPasswordRecoveryHashAction implements Action<VerifyPasswordRecoveryHashResult>{
	private String hash;

	public VerifyPasswordRecoveryHashAction() {
	}
	
	public VerifyPasswordRecoveryHashAction(String hash) {
		this.setHash(hash);
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
