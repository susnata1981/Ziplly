package com.ziplly.app.server.handlers;

import static com.google.common.base.Preconditions.checkArgument;
import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.SpamDAO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.model.jpa.Spam;
import com.ziplly.app.shared.ReportSpamAction;
import com.ziplly.app.shared.ReportSpamResult;
import com.ziplly.app.shared.SpamStatus;

public class ReportSpamActionHandler extends
    AbstractAccountActionHandler<ReportSpamAction, ReportSpamResult> {
	
	private SpamDAO spamDao;

	@Inject
	public ReportSpamActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli,
	    SpamDAO spamDao) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
		this.spamDao = spamDao;
	}

	@Override
	public ReportSpamResult
	    doExecute(ReportSpamAction action, ExecutionContext arg1) throws DispatchException {

	  checkArgument(action.getSpam() != null && action.getSpam().getReporter() != null);
	  
//		if (action == null || action.getSpam() == null) {
//			throw new IllegalArgumentException();
//		}

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
