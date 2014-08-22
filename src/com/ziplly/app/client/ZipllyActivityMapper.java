package com.ziplly.app.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.AboutActivity;
import com.ziplly.app.client.activities.AccountSwitcherActivity;
import com.ziplly.app.client.activities.AdminActivity;
import com.ziplly.app.client.activities.BusinessAccountActivity;
import com.ziplly.app.client.activities.BusinessAccountSettingsActivity;
import com.ziplly.app.client.activities.BusinessActivity;
import com.ziplly.app.client.activities.BusinessSignupActivity;
import com.ziplly.app.client.activities.CouponReportActivity;
import com.ziplly.app.client.activities.CouponReportActivity.CouponReportView;
import com.ziplly.app.client.activities.EmailVerificationActivity;
import com.ziplly.app.client.activities.LoginActivity;
import com.ziplly.app.client.activities.OAuthActivity;
import com.ziplly.app.client.activities.PasswordRecoveryActivity;
import com.ziplly.app.client.activities.PersonalAccountActivity;
import com.ziplly.app.client.activities.PersonalAccountSettingsActivity;
import com.ziplly.app.client.activities.PrintCouponActivity;
import com.ziplly.app.client.activities.ResidentActivity;
import com.ziplly.app.client.activities.SignupActivity;
import com.ziplly.app.client.activities.TweetDetailsActivity;
import com.ziplly.app.client.conversation.ConversationActivity;
import com.ziplly.app.client.conversation.ConversationView;
import com.ziplly.app.client.dispatcher.CachingDispatcherAsync;
import com.ziplly.app.client.places.AboutPlace;
import com.ziplly.app.client.places.AccountSwitcherPlace;
import com.ziplly.app.client.places.AdminPlace;
import com.ziplly.app.client.places.BusinessAccountPlace;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.BusinessPlace;
import com.ziplly.app.client.places.BusinessSignupPlace;
import com.ziplly.app.client.places.ConversationPlace;
import com.ziplly.app.client.places.CouponReportPlace;
import com.ziplly.app.client.places.EmailVerificationPlace;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.places.OAuthPlace;
import com.ziplly.app.client.places.PasswordRecoveryPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.places.PrintCouponPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.places.TweetDetailsPlace;
import com.ziplly.app.client.view.AboutView;
import com.ziplly.app.client.view.AdminView;
import com.ziplly.app.client.view.BusinessView;
import com.ziplly.app.client.view.EmailVerificationView;
import com.ziplly.app.client.view.LoginAccountView;
import com.ziplly.app.client.view.PasswordRecoveryView;
import com.ziplly.app.client.view.PrintCouponView;
import com.ziplly.app.client.view.TweetDetailsView;
import com.ziplly.app.client.view.account.AccountView;
import com.ziplly.app.client.view.account.BusinessAccountSettingsView;
import com.ziplly.app.client.view.account.BusinessAccountView;
import com.ziplly.app.client.view.account.PersonalAccountSettingsView;
import com.ziplly.app.client.view.community.ResidentsView;
import com.ziplly.app.client.view.home.HomeActivity;
import com.ziplly.app.client.view.home.HomeViewImpl;
import com.ziplly.app.client.view.signup.BusinessSignupView;
import com.ziplly.app.client.view.signup.SignupView;

public class ZipllyActivityMapper implements ActivityMapper {
	private PlaceController placeController;
	private CachingDispatcherAsync dispatcher;
	private EventBus eventBus;
	private ApplicationContext ctx;
	private AsyncProvider<HomeViewImpl> homeView;
	private AsyncProvider<TweetDetailsView> tweetDetailsView;
	private AsyncProvider<LoginAccountView> loginAccountView;
	private AsyncProvider<AccountView> accountView;
	private AsyncProvider<SignupView> signupView;
	private AsyncProvider<BusinessSignupView> businessSignupView;
	private AsyncProvider<BusinessAccountView> businessAccountView;
	private AsyncProvider<ResidentsView> residentsView;
	private AsyncProvider<BusinessView> businessView;
	private AsyncProvider<PersonalAccountSettingsView> personalAccountSettingsView;
	private AsyncProvider<BusinessAccountSettingsView> businessAccountSettingsView;
	private AsyncProvider<ConversationView> conversationView;
	private AsyncProvider<PasswordRecoveryView> passwordRecoveryView;
	private AsyncProvider<EmailVerificationView> emailVerificationView;
	private AsyncProvider<AdminView> adminView;
	private AsyncProvider<AboutView> aboutView;
	private AsyncProvider<PrintCouponView> printCouponView;
	private AsyncProvider<CouponReportView> couponReportView;
	
	@Inject
	public ZipllyActivityMapper(AsyncProvider<HomeViewImpl> homeView,
	    AsyncProvider<TweetDetailsView> tweetDetailsView,
	    AsyncProvider<LoginAccountView> loginAccountView,
	    AsyncProvider<AccountView> accountView,
	    AsyncProvider<BusinessAccountView> businessAccountView,
	    AsyncProvider<SignupView> signupView,
	    AsyncProvider<BusinessSignupView> businessSignupView,
	    AsyncProvider<ResidentsView> residentsView,
	    AsyncProvider<BusinessView> businessView,
	    AsyncProvider<PersonalAccountSettingsView> personalAccountSettingsView,
	    AsyncProvider<BusinessAccountSettingsView> businessAccountSettingsView,
	    AsyncProvider<ConversationView> conversationView,
	    AsyncProvider<PasswordRecoveryView> passwordRecoveryView,
	    AsyncProvider<EmailVerificationView> emailVerificationView,
	    AsyncProvider<AdminView> adminView,
	    AsyncProvider<AboutView> aboutView,
	    AsyncProvider<PrintCouponView> printCouponView,
	    AsyncProvider<CouponReportView> couponReportView,
	    CachingDispatcherAsync dispatcher,
	    EventBus eventBus,
	    PlaceController placeController,
	    ApplicationContext ctx) {

		this.homeView = homeView;
		this.tweetDetailsView = tweetDetailsView;
		this.loginAccountView = loginAccountView;
		this.accountView = accountView;
		this.businessAccountView = businessAccountView;
		this.signupView = signupView;
		this.businessSignupView = businessSignupView;
		this.residentsView = residentsView;
		this.businessView = businessView;
		this.personalAccountSettingsView = personalAccountSettingsView;
		this.businessAccountSettingsView = businessAccountSettingsView;
		this.conversationView = conversationView;
		this.passwordRecoveryView = passwordRecoveryView;
		this.emailVerificationView = emailVerificationView;
		this.adminView = adminView;
		this.aboutView = aboutView;
		this.printCouponView = printCouponView;
		this.couponReportView = couponReportView;
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.placeController = placeController;
		this.ctx = ctx;
	}

	@Override
	public Activity getActivity(final Place place) {
		ctx.setNewPlace(place);
		if (place instanceof HomePlace) {
			return new ActivityProxy<HomeActivity>(new AsyncProvider<HomeActivity>() {

				@Override
				public void get(AsyncCallback<? super HomeActivity> callback) {
					callback.onSuccess(new HomeActivity(
					    dispatcher,
					    eventBus,
					    (HomePlace) place,
					    placeController,
					    ctx,
					    homeView));
				}
			});

		} else if (place instanceof TweetDetailsPlace) {
			return new ActivityProxy<TweetDetailsActivity>(new AsyncProvider<TweetDetailsActivity>() {

				@Override
				public void get(AsyncCallback<? super TweetDetailsActivity> callback) {
					callback.onSuccess(new TweetDetailsActivity(
					    dispatcher,
					    eventBus,
					    (TweetDetailsPlace) place,
					    placeController,
					    ctx,
					    tweetDetailsView));
				}

			});
		} else if (place instanceof LoginPlace) {
			return new LoginActivity(
			    dispatcher,
			    eventBus,
			    (LoginPlace) place,
			    placeController,
			    ctx,
			    loginAccountView);
			// return new ActivityProxy<LoginActivity>(new
			// AsyncProvider<LoginActivity>() {
			//
			// @Override
			// public void get(AsyncCallback<? super LoginActivity> callback) {
			// callback.onSuccess(new LoginActivity(dispatcher, eventBus,
			// (LoginPlace)place, placeController, ctx, loginAccountView));
			// }
			// });
		} else if (place instanceof SignupPlace) {
			return new SignupActivity(
			    dispatcher,
			    eventBus,
			    placeController,
			    (SignupPlace) place,
			    ctx,
			    signupView);
			
		} else if (place instanceof BusinessSignupPlace) {
			return new ActivityProxy<BusinessSignupActivity>(new AsyncProvider<BusinessSignupActivity>() {

				@Override
				public void get(AsyncCallback<? super BusinessSignupActivity> callback) {
					callback.onSuccess(new BusinessSignupActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    (BusinessSignupPlace) place,
					    businessSignupView));
				}
			});
		} else if (place instanceof OAuthPlace) {
			return new ActivityProxy<OAuthActivity>(new AsyncProvider<OAuthActivity>() {

				@Override
				public void get(AsyncCallback<? super OAuthActivity> callback) {
					callback.onSuccess(new OAuthActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    (OAuthPlace) place));
				}
			});
		} else if (place instanceof PersonalAccountPlace) {
			return new ActivityProxy<PersonalAccountActivity>(
			    new AsyncProvider<PersonalAccountActivity>() {

				    @Override
				    public void get(AsyncCallback<? super PersonalAccountActivity> callback) {
					    callback.onSuccess(new PersonalAccountActivity(
					        dispatcher,
					        eventBus,
					        placeController,
					        ctx,
					        accountView,
					        (PersonalAccountPlace) place));
				    }
				    
			    });
		} else if (place instanceof BusinessAccountPlace) {

			return new ActivityProxy<BusinessAccountActivity>(
			    new AsyncProvider<BusinessAccountActivity>() {

				    @Override
				    public void get(AsyncCallback<? super BusinessAccountActivity> callback) {
					    callback.onSuccess(new BusinessAccountActivity(
					        dispatcher,
					        eventBus,
					        placeController,
					        ctx,
					        businessAccountView,
					        (BusinessAccountPlace) place));
				    }
			    });
		} else if (place instanceof PersonalAccountSettingsPlace) {
			return new ActivityProxy<PersonalAccountSettingsActivity>(
			    new AsyncProvider<PersonalAccountSettingsActivity>() {

				    @Override
				    public void get(AsyncCallback<? super PersonalAccountSettingsActivity> callback) {
					    callback.onSuccess(new PersonalAccountSettingsActivity(
					        dispatcher,
					        eventBus,
					        placeController,
					        ctx,
					        personalAccountSettingsView));
				    }
			    });
		} else if (place instanceof BusinessAccountSettingsPlace) {
			return new ActivityProxy<BusinessAccountSettingsActivity>(
			    new AsyncProvider<BusinessAccountSettingsActivity>() {

				    @Override
				    public void get(AsyncCallback<? super BusinessAccountSettingsActivity> callback) {
					    callback.onSuccess(new BusinessAccountSettingsActivity(
					        dispatcher,
					        eventBus,
					        placeController,
					        ctx,
					        businessAccountSettingsView,
					        (BusinessAccountSettingsPlace) place));
				    }
			    });
		} else if (place instanceof ConversationPlace) {
			return new ActivityProxy<ConversationActivity>(new AsyncProvider<ConversationActivity>() {

				@Override
				public void get(AsyncCallback<? super ConversationActivity> callback) {
					callback.onSuccess(new ConversationActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    (ConversationPlace) place,
					    conversationView));
				}
			});
		} else if (place instanceof ResidentPlace) {
			return new ActivityProxy<ResidentActivity>(new AsyncProvider<ResidentActivity>() {

				@Override
				public void get(AsyncCallback<? super ResidentActivity> callback) {
					callback.onSuccess(new ResidentActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    ((ResidentPlace) place),
					    residentsView));
				}
			});
		} else if (place instanceof BusinessPlace) {
			return new ActivityProxy<BusinessActivity>(new AsyncProvider<BusinessActivity>() {

				@Override
				public void get(AsyncCallback<? super BusinessActivity> callback) {
					callback.onSuccess(new BusinessActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    ((BusinessPlace) place),
					    businessView));
				}
			});
		} else if (place instanceof PasswordRecoveryPlace) {
			return new ActivityProxy<PasswordRecoveryActivity>(
			    new AsyncProvider<PasswordRecoveryActivity>() {

				    @Override
				    public void get(AsyncCallback<? super PasswordRecoveryActivity> callback) {
					    callback.onSuccess(new PasswordRecoveryActivity(
					        dispatcher,
					        eventBus,
					        placeController,
					        ctx,
					        (PasswordRecoveryPlace) place,
					        passwordRecoveryView));
				    }
			    });
		} else if (place instanceof EmailVerificationPlace) {
			return new ActivityProxy<EmailVerificationActivity>(
			    new AsyncProvider<EmailVerificationActivity>() {

				    @Override
				    public void get(AsyncCallback<? super EmailVerificationActivity> callback) {
					    callback.onSuccess(new EmailVerificationActivity(
					        dispatcher,
					        eventBus,
					        placeController,
					        ctx,
					        (EmailVerificationPlace) place,
					        emailVerificationView));
				    }

			    });
		} else if (place instanceof AdminPlace) {
			return new ActivityProxy<AdminActivity>(new AsyncProvider<AdminActivity>() {

				@Override
				public void get(AsyncCallback<? super AdminActivity> callback) {
					callback.onSuccess(new AdminActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    adminView));
				}
			});
		} else if (place instanceof AboutPlace) {
			return new ActivityProxy<AboutActivity>(new AsyncProvider<AboutActivity>() {

				@Override
				public void get(AsyncCallback<? super AboutActivity> callback) {
					callback.onSuccess(new AboutActivity(
					    dispatcher,
					    eventBus,
					    placeController,
					    ctx,
					    (AboutPlace) place,
					    aboutView));
				}
			});
		} else if (place instanceof PrintCouponPlace) {
			return new ActivityProxy<PrintCouponActivity>(new AsyncProvider<PrintCouponActivity>() {

				@Override
        public void get(AsyncCallback<? super PrintCouponActivity> callback) {
					callback.onSuccess(new PrintCouponActivity(dispatcher, 
							eventBus, 
							placeController, 
							ctx,
							(PrintCouponPlace) place,
							printCouponView));
        }
				
			});
		} else if (place instanceof CouponReportPlace) {
			return new ActivityProxy<CouponReportActivity>(new AsyncProvider<CouponReportActivity>() {

				@Override
        public void get(AsyncCallback<? super CouponReportActivity> callback) {
					callback.onSuccess(new CouponReportActivity(dispatcher, 
							eventBus, 
							placeController, 
							(CouponReportPlace) place,
							ctx,
							couponReportView));
        }
				
			});
		}  else if (place instanceof AccountSwitcherPlace) {
      return new AccountSwitcherActivity(dispatcher, eventBus, placeController, ctx, (AccountSwitcherPlace)place);
    }
		
		throw new IllegalArgumentException("Invalid place encountered");
	}
}
