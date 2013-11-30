package com.ziplly.app.server;

import java.util.List;

import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetSearchCriteria;

public interface AdminBLI {
	List<TweetDTO> getTweets(int start, int end, TweetSearchCriteria tsc);

	Long getTotalTweetCount(TweetSearchCriteria tsc);
}
