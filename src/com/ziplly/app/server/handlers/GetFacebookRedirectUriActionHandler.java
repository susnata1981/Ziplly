package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.ziplly.app.client.oauth.OAuthAppProperties;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetFacebookRedirectUriAction;
import com.ziplly.app.shared.GetFacebookRedirectUriResult;

public class GetFacebookRedirectUriActionHandler extends
    AbstractAccountActionHandler<GetFacebookRedirectUriAction, GetFacebookRedirectUriResult> {

	@Inject
	public GetFacebookRedirectUriActionHandler(AccountDAO accountDao,
	    SessionDAO sessionDao,
	    AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetFacebookRedirectUriResult execute(GetFacebookRedirectUriAction arg0,
	    ExecutionContext arg1) throws DispatchException {

		GetFacebookRedirectUriResult result = new GetFacebookRedirectUriResult();
		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Development) {
			result.setRedirectUrl(OAuthAppProperties.REDIRECT_URL_IN_DEVELOPMENT);
		} else {
			String url = GWT.getModuleBaseURL();
			result.setRedirectUrl(url);
		}

		return result;
	}

	@Override
	public Class<GetFacebookRedirectUriAction> getActionType() {
		return GetFacebookRedirectUriAction.class;
	}

}
