package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import com.ziplly.app.server.MyActionHandler;
import com.ziplly.app.server.handlers.CommentActionHandler;
import com.ziplly.app.server.handlers.GetAccountByIdActionHandler;
import com.ziplly.app.server.handlers.GetCommunityWallDataActionHandler;
import com.ziplly.app.server.handlers.GetConversationActionHandler;
import com.ziplly.app.server.handlers.GetFacebookDetailsHandler;
import com.ziplly.app.server.handlers.GetImageUploadUrlActionHandler;
import com.ziplly.app.server.handlers.GetLoggedInUserActionHandler;
import com.ziplly.app.server.handlers.GetResidentsActionHandler;
import com.ziplly.app.server.handlers.LikeTweetActionHandler;
import com.ziplly.app.server.handlers.LogoutActionHandler;
import com.ziplly.app.server.handlers.RegisterAccountActionHandler;
import com.ziplly.app.server.handlers.SendMessageActionHandler;
import com.ziplly.app.server.handlers.TweetActionHandler;
import com.ziplly.app.server.handlers.UpdateAccountActionHandler;
import com.ziplly.app.server.handlers.UpdateTweetActionHandler;
import com.ziplly.app.server.handlers.ValidateLoginActionHandler;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetConversationsResult;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetResidentsRequest;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.MyAction;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.ValidateLoginAction;

public class ZipllyActionHandlerModule extends ActionHandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(MyAction.class, MyActionHandler.class);
		
		bindHandler(GetFacebookDetailsAction.class, GetFacebookDetailsHandler.class);
		bindHandler(GetAccountByIdAction.class, GetAccountByIdActionHandler.class);
		
		bindHandler(RegisterAccountAction.class, RegisterAccountActionHandler.class);
		
		bindHandler(ValidateLoginAction.class, ValidateLoginActionHandler.class);
		
		bindHandler(UpdateAccountAction.class, UpdateAccountActionHandler.class);

		bindHandler(GetLoggedInUserAction.class, GetLoggedInUserActionHandler.class);
		bindHandler(LogoutAction.class, LogoutActionHandler.class);
		
		bindHandler(GetImageUploadUrlAction.class, GetImageUploadUrlActionHandler.class);
		
		bindHandler(TweetAction.class, TweetActionHandler.class);
		bindHandler(UpdateTweetAction.class, UpdateTweetActionHandler.class);
		bindHandler(GetCommunityWallDataAction.class, GetCommunityWallDataActionHandler.class);
		bindHandler(CommentAction.class, CommentActionHandler.class);
		bindHandler(LikeTweetAction.class, LikeTweetActionHandler.class);
		bindHandler(GetResidentsRequest.class, GetResidentsActionHandler.class);
		
		bindHandler(SendMessageAction.class, SendMessageActionHandler.class);
		bindHandler(GetConversationsAction.class, GetConversationActionHandler.class);
		
	}
}
