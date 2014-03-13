package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class GetAccountByIdActionHandler extends
    AbstractAccountActionHandler<GetAccountByIdAction, GetAccountByIdResult> {

	@Inject
	public GetAccountByIdActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetAccountByIdResult
	    execute(GetAccountByIdAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getAccountId() == null) {
			throw new IllegalArgumentException();
		}

		GetAccountByIdResult result = new GetAccountByIdResult();
		try {
			AccountDTO account = accountBli.getAccountById(action.getAccountId());
			if (account instanceof PersonalAccountDTO) {
				applyPrivacySettings((PersonalAccountDTO) account);
			}
			result.setAccount(account);
			return result;

		} catch (NotFoundException nfe) {
			throw nfe;
		}
	}

	/*
	 * Hides AccountDetailsType based on privacy settings;
	 */
	private void applyPrivacySettings(PersonalAccountDTO account) {
		if (account == null) {
			return;
		}

		for (PrivacySettingsDTO ps : account.getPrivacySettings()) {
			switch (ps.getSection()) {
				case EMAIL:
					if (!isAttributeVisible(account, ps)) {
						account.setEmail(StringConstants.NOT_SHARED);
					}
					break;
				case OCCUPATION:
					if (!isAttributeVisible(account, ps)) {
						account.setOccupation(StringConstants.NOT_SHARED);
					}
				case TWEETS:
					// nothing to do
			}
		}
	}

	private boolean isAttributeVisible(AccountDTO account, PrivacySettingsDTO ps) {

		try {
			validateSession();
		} catch (NeedsLoginException e) {
			return ps.getSetting() == ShareSetting.PUBLIC;
		}

		if (session == null || session.getAccount() == null) {
			return ps.getSetting() == ShareSetting.PUBLIC;
		}

		if (session.getAccount() != null) {
			if (ps.getSetting() == ShareSetting.PUBLIC) {
				return true;
			}

			Account loggedInAccount = session.getAccount();
			if (belongToSameCommunity(account, loggedInAccount)) {
				return ps.getSetting() == ShareSetting.COMMUNITY;
			}
		}
		return false;
	}

	private boolean belongToSameCommunity(AccountDTO account, Account loggedInAccount) {
		return account.getLocations().get(0).getNeighborhood().getNeighborhoodId() == loggedInAccount
		    .getLocations()
		    .iterator()
		    .next()
		    .getNeighborhood()
		    .getNeighborhoodId();
	}

	@Override
	public Class<GetAccountByIdAction> getActionType() {
		return GetAccountByIdAction.class;
	}
}
