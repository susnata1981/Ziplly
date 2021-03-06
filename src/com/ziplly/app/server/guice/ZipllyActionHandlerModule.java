package com.ziplly.app.server.guice;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import com.ziplly.app.client.view.handler.GetJwtTokenActionHandler;
import com.ziplly.app.server.MyActionHandler;
import com.ziplly.app.server.handlers.BusinessTweetActionHandler;
import com.ziplly.app.server.handlers.CheckEmailRegistrationActionHandler;
import com.ziplly.app.server.handlers.CommentActionHandler;
import com.ziplly.app.server.handlers.CreateRegistrationActionHandler;
import com.ziplly.app.server.handlers.DeleteTweetActionHandler;
import com.ziplly.app.server.handlers.GetAccountByIdActionHandler;
import com.ziplly.app.server.handlers.GetAccountDetailsActionHandler;
import com.ziplly.app.server.handlers.GetAllSubscriptionPlanActionHandler;
import com.ziplly.app.server.handlers.GetCommunityWallDataActionHandler;
import com.ziplly.app.server.handlers.GetConversationActionHandler;
import com.ziplly.app.server.handlers.GetFacebookDetailsHandler;
import com.ziplly.app.server.handlers.GetHashtagActionHandler;
import com.ziplly.app.server.handlers.GetImageUploadUrlActionHandler;
import com.ziplly.app.server.handlers.GetLatLngActionHandler;
import com.ziplly.app.server.handlers.GetLoggedInUserActionHandler;
import com.ziplly.app.server.handlers.GetPublicAccountDetailsActionHandler;
import com.ziplly.app.server.handlers.GetResidentsActionHandler;
import com.ziplly.app.server.handlers.GetTweetActionHandler;
import com.ziplly.app.server.handlers.GetTweetForUserActionHandler;
import com.ziplly.app.server.handlers.LikeTweetActionHandler;
import com.ziplly.app.server.handlers.LogoutActionHandler;
import com.ziplly.app.server.handlers.PayActionHandler;
import com.ziplly.app.server.handlers.RegisterAccountActionHandler;
import com.ziplly.app.server.handlers.ResetPasswordActionHandler;
import com.ziplly.app.server.handlers.SearchAccountActionHandler;
import com.ziplly.app.server.handlers.SendEmailActionHandler;
import com.ziplly.app.server.handlers.SendMessageActionHandler;
import com.ziplly.app.server.handlers.SendPasswordRecoveryActionHandler;
import com.ziplly.app.server.handlers.TweetActionHandler;
import com.ziplly.app.server.handlers.UpdateAccountActionHandler;
import com.ziplly.app.server.handlers.UpdatePasswordActionHandler;
import com.ziplly.app.server.handlers.UpdateTweetActionHandler;
import com.ziplly.app.server.handlers.ValidateLoginActionHandler;
import com.ziplly.app.server.handlers.VerifyPasswordRecoveryHashActionHandler;
import com.ziplly.app.server.handlers.ViewConversationActionHandler;
import com.ziplly.app.shared.BusinessTweetAction;
import com.ziplly.app.shared.CheckEmailRegistrationAction;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CreateRegistrationAction;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetHashtagAction;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetJwtTokenAction;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetPublicAccountDetailsAction;
import com.ziplly.app.shared.GetResidentsRequest;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetsAction;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.MyAction;
import com.ziplly.app.shared.PayAction;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.ResetPasswordAction;
import com.ziplly.app.shared.SearchAccountAction;
import com.ziplly.app.shared.SendEmailAction;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendPasswordRecoveryEmailAction;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashAction;
import com.ziplly.app.shared.ViewConversationAction;

public class ZipllyActionHandlerModule extends ActionHandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(MyAction.class, MyActionHandler.class);
		
		bindHandler(GetFacebookDetailsAction.class, GetFacebookDetailsHandler.class);
		bindHandler(GetAccountByIdAction.class, GetAccountByIdActionHandler.class);
		
		bindHandler(RegisterAccountAction.class, RegisterAccountActionHandler.class);
		bindHandler(CreateRegistrationAction.class, CreateRegistrationActionHandler.class);
		bindHandler(UpdatePasswordAction.class, UpdatePasswordActionHandler.class);
		
		bindHandler(ValidateLoginAction.class, ValidateLoginActionHandler.class);
		
		bindHandler(UpdateAccountAction.class, UpdateAccountActionHandler.class);

		bindHandler(GetLoggedInUserAction.class, GetLoggedInUserActionHandler.class);
		bindHandler(LogoutAction.class, LogoutActionHandler.class);
		
		bindHandler(GetImageUploadUrlAction.class, GetImageUploadUrlActionHandler.class);
		
		// Tweet handlers
		bindHandler(TweetAction.class, TweetActionHandler.class);
		bindHandler(BusinessTweetAction.class, BusinessTweetActionHandler.class);
		bindHandler(UpdateTweetAction.class, UpdateTweetActionHandler.class);
		bindHandler(GetCommunityWallDataAction.class, GetCommunityWallDataActionHandler.class);
		bindHandler(CommentAction.class, CommentActionHandler.class);
		bindHandler(LikeTweetAction.class, LikeTweetActionHandler.class);
		bindHandler(GetResidentsRequest.class, GetResidentsActionHandler.class);
		bindHandler(GetTweetForUserAction.class, GetTweetForUserActionHandler.class);
		bindHandler(GetAccountDetailsAction.class, GetAccountDetailsActionHandler.class);
		bindHandler(DeleteTweetAction.class, DeleteTweetActionHandler.class);
		bindHandler(SendEmailAction.class, SendEmailActionHandler.class);
		bindHandler(GetHashtagAction.class, GetHashtagActionHandler.class);
		
		bindHandler(GetPublicAccountDetailsAction.class, GetPublicAccountDetailsActionHandler.class);
		
		bindHandler(SendMessageAction.class, SendMessageActionHandler.class);
		bindHandler(GetConversationsAction.class, GetConversationActionHandler.class);
		bindHandler(ViewConversationAction.class, ViewConversationActionHandler.class);
		
		// Payment service
		bindHandler(GetJwtTokenAction.class, GetJwtTokenActionHandler.class);
		bindHandler(PayAction.class, PayActionHandler.class);
		bindHandler(GetAllSubscriptionPlanAction.class, GetAllSubscriptionPlanActionHandler.class);
		
		bindHandler(GetLatLngAction.class, GetLatLngActionHandler.class);
		
		// password recover
		bindHandler(SendPasswordRecoveryEmailAction.class, SendPasswordRecoveryActionHandler.class);
		bindHandler(VerifyPasswordRecoveryHashAction.class, VerifyPasswordRecoveryHashActionHandler.class);
		bindHandler(ResetPasswordAction.class, ResetPasswordActionHandler.class);
		
		// admin
		bindHandler(GetTweetsAction.class, GetTweetActionHandler.class);
		bindHandler(SearchAccountAction.class, SearchAccountActionHandler.class);
		bindHandler(CheckEmailRegistrationAction.class, CheckEmailRegistrationActionHandler.class);
	}
}
