package com.ziplly.app.server.bli;

import java.util.List;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountSearchCriteria;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;

public interface AdminBLI {
	List<TweetDTO> getTweets(int start, int end, TweetSearchCriteria tsc);

	Long getTotalTweetCount(TweetSearchCriteria tsc);

	List<AccountDTO> getAccounts(int start, int end, AccountSearchCriteria asc);

	Long getTotalAccounts(AccountSearchCriteria asc);
}
