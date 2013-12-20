package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SpamDAO;
import com.ziplly.app.model.Spam;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.ReportSpamAction;
import com.ziplly.app.shared.ReportSpamResult;
import com.ziplly.app.shared.SpamStatus;

public class ReportSpamActionHandler extends AbstractAccountActionHandler<ReportSpamAction, ReportSpamResult>{
	private SpamDAO spamDao;

	@Inject
	public ReportSpamActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, SpamDAO spamDao) {
		super(accountDao, sessionDao, accountBli);
		this.spamDao = spamDao;
	}

	@Override
	public ReportSpamResult execute(ReportSpamAction action, ExecutionContext arg1)
			throws DispatchException {
		
		if (action == null || action.getSpam() == null) {
			throw new IllegalArgumentException();
		}
		
		validateSession();
		
		// Just save it for now
		Spam spam = new Spam(action.getSpam());
		spam.setStatus(SpamStatus.PENDING);
		spamDao.save(spam);
		
		return new ReportSpamResult();
	}

	@Override
	public Class<ReportSpamAction> getActionType() {
		return ReportSpamAction.class;
	}

}
