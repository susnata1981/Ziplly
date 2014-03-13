package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TransactionDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.PayAction;
import com.ziplly.app.shared.PayResult;

public class PayActionHandler extends AbstractAccountActionHandler<PayAction, PayResult> {

	@Inject
	public PayActionHandler(AccountDAO accountDao, SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public PayResult execute(PayAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getTransaction() == null) {
			throw new IllegalArgumentException();
		}

		validateSession();

		AccountDTO accountDto = EntityUtil.convert(session.getAccount());
		action.getTransaction().setSeller(accountDto);
		TransactionDTO txn = accountBli.pay(action.getTransaction());
		PayResult result = new PayResult();
		result.setTransaction(txn);
		return result;
	}

	@Override
	public Class<PayAction> getActionType() {
		return PayAction.class;
	}

}