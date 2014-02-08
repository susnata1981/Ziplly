package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.model.SpamDTO;

public class ReportSpamAction implements Action<ReportSpamResult>{
	private SpamDTO spam;

	public ReportSpamAction() {
	}
	
	public ReportSpamAction(SpamDTO spam) {
		this.setSpam(spam);
	}

	public SpamDTO getSpam() {
		return spam;
	}

	public void setSpam(SpamDTO spam) {
		this.spam = spam;
	}
}
