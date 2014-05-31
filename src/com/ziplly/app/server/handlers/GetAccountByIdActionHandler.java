package com.ziplly.app.server.handlers;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NeedsLoginException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.ShareSetting;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.EntityUtil;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccount;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.model.PrivacySettings;
import com.ziplly.app.model.PrivacySettingsDTO;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountByIdResult;

public class GetAccountByIdActionHandler extends
    AbstractAccountActionHandler<GetAccountByIdAction, GetAccountByIdResult> {

	@Inject
	public GetAccountByIdActionHandler(
			Provider<EntityManager> entityManagerProvider,
			AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(entityManagerProvider, accountDao, sessionDao, accountBli);
	}

	@Override
	public GetAccountByIdResult
	    doExecute(GetAccountByIdAction action, ExecutionContext arg1) throws DispatchException {

		if (action == null || action.getAccountId() == null) {
			throw new IllegalArgumentException();
		}

		GetAccountByIdResult result = new GetAccountByIdResult();
		try {
			Account account = accountBli.getAccountById(action.getAccountId());
			if (account instanceof PersonalAccount) {
				applyPrivacySettings((PersonalAccount) account);
			}
			result.setAccount(EntityUtil.convert(account));
			return result;

		} catch (NotFoundException nfe) {
			throw nfe;
		}
	}

	/*
	 * Hides AccountDetailsType based on privacy settings;
	 */
	private void applyPrivacySettings(PersonalAccount account) {
		if (account == null) {
			return;
		}

		for (PrivacySettings ps : account.getPrivacySettings()) {
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

	private boolean isAttributeVisible(Account account, PrivacySettings ps) {

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

	private boolean belongToSameCommunity(Account account, Account loggedInAccount) {
		return account.getLocations().iterator().next().getNeighborhood().getNeighborhoodId() == loggedInAccount
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
