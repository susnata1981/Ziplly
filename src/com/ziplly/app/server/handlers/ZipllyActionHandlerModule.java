package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;

import com.ziplly.app.server.MyActionHandler;
import com.ziplly.app.shared.AddInvitationAction;
import com.ziplly.app.shared.CheckBuyerEligibilityForCouponAction;
import com.ziplly.app.shared.CheckEmailRegistrationAction;
import com.ziplly.app.shared.CheckSubscriptionEligibilityAction;
import com.ziplly.app.shared.CommentAction;
import com.ziplly.app.shared.CreateNeighborhoodAction;
import com.ziplly.app.shared.CreateRegistrationAction;
import com.ziplly.app.shared.DeleteImageAction;
import com.ziplly.app.shared.DeleteNeighborhoodAction;
import com.ziplly.app.shared.DeleteTweetAction;
import com.ziplly.app.shared.EmailAdminAction;
import com.ziplly.app.shared.GetAccountByIdAction;
import com.ziplly.app.shared.GetAccountDetailsAction;
import com.ziplly.app.shared.GetAccountNotificationAction;
import com.ziplly.app.shared.GetAllSubscriptionPlanAction;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetConversationsAction;
import com.ziplly.app.shared.GetCouponQRCodeUrlAction;
import com.ziplly.app.shared.GetCouponTransactionAction;
import com.ziplly.app.shared.GetCouponsAction;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.GetEnvironmentAction;
import com.ziplly.app.shared.GetFacebookDetailsAction;
import com.ziplly.app.shared.GetFacebookRedirectUriAction;
import com.ziplly.app.shared.GetHashtagAction;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetInterestAction;
import com.ziplly.app.shared.GetLatLngAction;
import com.ziplly.app.shared.GetLoggedInUserAction;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodDetailsAction;
import com.ziplly.app.shared.GetNewMemberAction;
import com.ziplly.app.shared.GetPublicAccountDetailsAction;
import com.ziplly.app.shared.GetTweetCategoryDetailsAction;
import com.ziplly.app.shared.GetTweetForUserAction;
import com.ziplly.app.shared.GetTweetsAction;
import com.ziplly.app.shared.LikeTweetAction;
import com.ziplly.app.shared.LogoutAction;
import com.ziplly.app.shared.MyAction;
import com.ziplly.app.shared.PurchasedCouponAction;
import com.ziplly.app.shared.RedeemCouponAction;
import com.ziplly.app.shared.RegisterAccountAction;
import com.ziplly.app.shared.ReportSpamAction;
import com.ziplly.app.shared.ResendEmailVerificationAction;
import com.ziplly.app.shared.ResetPasswordAction;
import com.ziplly.app.shared.SearchAccountAction;
import com.ziplly.app.shared.SendEmailAction;
import com.ziplly.app.shared.SendMessageAction;
import com.ziplly.app.shared.SendPasswordRecoveryEmailAction;
import com.ziplly.app.shared.SwitchLocationAction;
import com.ziplly.app.shared.TweetAction;
import com.ziplly.app.shared.UpdateAccountAction;
import com.ziplly.app.shared.UpdateAccountLocationAction;
import com.ziplly.app.shared.UpdateCommentAction;
import com.ziplly.app.shared.UpdateNeighborhoodAction;
import com.ziplly.app.shared.UpdatePasswordAction;
import com.ziplly.app.shared.UpdateTweetAction;
import com.ziplly.app.shared.ValidateLoginAction;
import com.ziplly.app.shared.VerifyEmailAction;
import com.ziplly.app.shared.VerifyPasswordRecoveryHashAction;
import com.ziplly.app.shared.ViewConversationAction;
import com.ziplly.app.shared.ViewNotificationAction;

public class ZipllyActionHandlerModule extends ActionHandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(MyAction.class, MyActionHandler.class);

		bindHandler(GetFacebookDetailsAction.class, GetFacebookDetailsHandler.class);
		bindHandler(GetFacebookRedirectUriAction.class, GetFacebookRedirectUriActionHandler.class);
		bindHandler(GetAccountByIdAction.class, GetAccountByIdActionHandler.class);

		// Account registration
		bindHandler(RegisterAccountAction.class, RegisterAccountActionHandler.class);
		bindHandler(CreateRegistrationAction.class, CreateRegistrationActionHandler.class);
		bindHandler(UpdatePasswordAction.class, UpdatePasswordActionHandler.class);
		bindHandler(VerifyEmailAction.class, VerifyEmailActionHandler.class);
		bindHandler(ResendEmailVerificationAction.class, ResendEmailVerificationActionHandler.class);

		// Neighborhood
		bindHandler(GetNeighborhoodAction.class, GetNeighborhoodActionHandler.class);
		bindHandler(UpdateNeighborhoodAction.class, UpdateNeighborhoodActionHandler.class);
		bindHandler(CreateNeighborhoodAction.class, CreateNeighborhoodActionHandler.class);
		bindHandler(DeleteNeighborhoodAction.class, DeleteNeighborhoodActionHandler.class);

		// Location
		bindHandler(SwitchLocationAction.class, SwitchLocationActionHander.class);
		bindHandler(UpdateAccountLocationAction.class, UpdateAccountLocationActionHandler.class);
		
		bindHandler(ValidateLoginAction.class, ValidateLoginActionHandler.class);

		bindHandler(UpdateAccountAction.class, UpdateAccountActionHandler.class);

		bindHandler(GetLoggedInUserAction.class, GetLoggedInUserActionHandler.class);
		bindHandler(LogoutAction.class, LogoutActionHandler.class);

		bindHandler(GetImageUploadUrlAction.class, GetImageUploadUrlActionHandler.class);
		bindHandler(GetNeighborhoodDetailsAction.class, GetNeighborhoodDetailsActionHandler.class);

		// Tweet handlers
		bindHandler(TweetAction.class, TweetActionHandler.class);
		bindHandler(UpdateTweetAction.class, UpdateTweetActionHandler.class);
		bindHandler(GetCommunityWallDataAction.class, GetCommunityWallDataActionHandler.class);
		bindHandler(CommentAction.class, CommentActionHandler.class);
		bindHandler(LikeTweetAction.class, LikeTweetActionHandler.class);
		bindHandler(GetTweetForUserAction.class, GetTweetForUserActionHandler.class);
		bindHandler(GetAccountDetailsAction.class, GetAccountDetailsActionHandler.class);
		bindHandler(DeleteTweetAction.class, DeleteTweetActionHandler.class);
		bindHandler(SendEmailAction.class, SendEmailActionHandler.class);
		bindHandler(GetHashtagAction.class, GetHashtagActionHandler.class);
		bindHandler(GetTweetCategoryDetailsAction.class, GetTweetCategoryDetailsActionHandler.class);
		bindHandler(ReportSpamAction.class, ReportSpamActionHandler.class);
		bindHandler(UpdateCommentAction.class, UpdateCommentActionHandler.class);

		bindHandler(GetPublicAccountDetailsAction.class, GetPublicAccountDetailsActionHandler.class);

		bindHandler(SendMessageAction.class, SendMessageActionHandler.class);
		bindHandler(GetConversationsAction.class, GetConversationActionHandler.class);
		bindHandler(ViewConversationAction.class, ViewConversationActionHandler.class);

		// Interest
		bindHandler(GetInterestAction.class, GetInterestActionHandler.class);

		// Account Notification
		bindHandler(GetAccountNotificationAction.class, GetAccountNotificationActionHandler.class);
		bindHandler(ViewNotificationAction.class, ViewNotificationActionHandler.class);
		bindHandler(GetNewMemberAction.class, GetNewMembersActionHandler.class);
		
		// Payment service
		bindHandler(GetAllSubscriptionPlanAction.class, GetAllSubscriptionPlanActionHandler.class);

		bindHandler(GetLatLngAction.class, GetLatLngActionHandler.class);

		// password recover
		bindHandler(SendPasswordRecoveryEmailAction.class, SendPasswordRecoveryActionHandler.class);
		bindHandler(
		    VerifyPasswordRecoveryHashAction.class,
		    VerifyPasswordRecoveryHashActionHandler.class);
		bindHandler(ResetPasswordAction.class, ResetPasswordActionHandler.class);

		// community
		bindHandler(GetEntityListAction.class, GetEntityActionHandler.class);

		// admin
		bindHandler(GetTweetsAction.class, GetTweetActionHandler.class);
		bindHandler(SearchAccountAction.class, SearchAccountActionHandler.class);
		bindHandler(CheckEmailRegistrationAction.class, CheckEmailRegistrationActionHandler.class);
		bindHandler(EmailAdminAction.class, EmailAdminActionHandler.class);

		// blobstore
		bindHandler(DeleteImageAction.class, DeleteImageActionHandler.class);

		// Coupon transaction
		bindHandler(CheckBuyerEligibilityForCouponAction.class, CheckBuyerEligibilityForCouponActionHandler.class);
		bindHandler(PurchasedCouponAction.class, PurchaseCouponActionHandler.class);
		bindHandler(GetCouponTransactionAction.class, GetCouponTransactionActionHandler.class);
		bindHandler(GetCouponQRCodeUrlAction.class, GetCouponQRCodeUrlActionHandler.class);
		bindHandler(RedeemCouponAction.class, RedeemCouponActionHandler.class);
		bindHandler(GetCouponsAction.class, GetCouponsActionHandler.class);
		
		// Subscription
		bindHandler(CheckSubscriptionEligibilityAction.class, CheckSubscriptionEligibilityActionHandler.class);
		
		// invitation
		bindHandler(AddInvitationAction.class, AddInvitationActionHandler.class);

		// Environment
		bindHandler(GetEnvironmentAction.class, GetEnvironmentActionHandler.class);
	}
}
