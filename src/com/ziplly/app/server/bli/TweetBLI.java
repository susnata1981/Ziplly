package com.ziplly.app.server.bli;

import com.ziplly.app.client.exceptions.AccessException;
import com.ziplly.app.client.exceptions.InternalException;
import com.ziplly.app.client.exceptions.NeedsSubscriptionException;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.client.exceptions.UsageLimitExceededException;
import com.ziplly.app.server.model.jpa.Account;
import com.ziplly.app.server.model.jpa.Tweet;

public interface TweetBLI {

	Tweet sendTweet(Tweet tweet, Account loggedInAccount) throws AccessException,
      NeedsSubscriptionException,
      InternalException,
      UsageLimitExceededException,
      NotFoundException;
}
