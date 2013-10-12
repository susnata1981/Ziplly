package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import com.ziplly.app.server.MyActionHandler;
import com.ziplly.app.server.handlers.GetFacebookDetailsHandler;
import com.ziplly.app.server.handlers.GetLoggedInUserActionHandler;
import com.ziplly.app.server.handlers.LogoutActionHandler;
import com.ziplly.app.server.handlers.RegisterAccountActionHandler;
import com.ziplly.app.server.handlers.ValidateLoginActionHandler;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.MyAction;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.ValidateLoginAction;

public class ZipllyActionHandlerModule extends ActionHandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(MyAction.class, MyActionHandler.class);
		bindHandler(GetFacebookDetailsAction.class, GetFacebookDetailsHandler.class);
		bindHandler(GetLoggedInUserAction.class, GetLoggedInUserActionHandler.class);
		bindHandler(RegisterAccountAction.class, RegisterAccountActionHandler.class);
		bindHandler(ValidateLoginAction.class, ValidateLoginActionHandler.class);
		bindHandler(LogoutAction.class, LogoutActionHandler.class);
	}
}
