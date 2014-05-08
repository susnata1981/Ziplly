package com.ziplly.app.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.ziplly.app.client.places.AboutPlace;
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
import com.ziplly.app.client.places.PasswordRecoveryPlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PersonalAccountSettingsPlace;
import com.ziplly.app.client.places.PrintCouponPlace;
import com.ziplly.app.client.places.ResidentPlace;
import com.ziplly.app.client.places.SignupPlace;
import com.ziplly.app.client.places.TweetDetailsPlace;

@WithTokenizers({ SignupPlace.Tokenizer.class, HomePlace.Tokenizer.class,
    TweetDetailsPlace.Tokenizer.class, LoginPlace.Tokenizer.class,
    BusinessSignupPlace.Tokenizer.class, BusinessAccountSettingsPlace.Tokenizer.class,
    PersonalAccountPlace.Tokenizer.class, BusinessAccountPlace.Tokenizer.class,
    PersonalAccountSettingsPlace.Tokenizer.class, ConversationPlace.Tokenizer.class,
    PasswordRecoveryPlace.Tokenizer.class, AdminPlace.Tokenizer.class, AboutPlace.Tokenizer.class,
    ResidentPlace.Tokenizer.class, BusinessPlace.Tokenizer.class,
    EmailVerificationPlace.Tokenizer.class, PrintCouponPlace.Tokenizer.class,
    CouponReportPlace.Tokenizer.class,})
public interface ZipllyPlaceHistoryMapper extends PlaceHistoryMapper {

}
