package com.ziplly.app.server;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ziplly.app.client.ZipllyService;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

public class MockZipllyServiceImpl extends RemoteServiceServlet implements ZipllyService {
	private static final long serialVersionUID = 1L;
	
	List<AccountDTO> accounts = Lists.newArrayList();
	List<TweetDTO> posts = Lists.newArrayList();
	private String[] firstnames = { "harry", "sally", "alicia", "erica",
			"shaan" };
	private String[] lastnames = { "potter", "maur", "keys", "basak", "basak" };
	private String[] imageUrls = {
			"http://www.desicomments.com/dc3/01/201272/201272.jpg",
			"http://www.desicomments.com/graphics/cartoons/100.gif",
			"http://www.desicomments.com/graphics/cartoons/66.gif",
			"http://www.desicomments.com/graphics/cartoons/81.gif",
			"http://www.desicomments.com/graphics/cartoons/70.gif"
	};
	
	private String[] tweets = {
			"Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
			"Lorem Ipsum has been the industry's standard dummy text ever since the 1500s",
			"When an unknown printer took a galley of type and scrambled it to make a type specimen book.",
			"It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.",
			"It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum." 
	};
	
	public MockZipllyServiceImpl() {
		setup();
	}

	void setup() {
		int N = accounts.size();
		for (int i = 0; i < N; i++) {
			AccountDTO a = new AccountDTO();
			String fn = firstnames[i % N], ln = lastnames[i % N];
			a.setFirstName(fn);
			a.setLastName(ln);
			a.setEmail(fn + "@yahoo.com");
			a.setIntroduction(String.format(
					"hello this is %s %s, check out my profile", fn, ln));
			a.setUrl("http://www.yahoo.com");
			a.setId(new Long(i));
			a.setZip(98199);
			a.setImageUrl(imageUrls[i%N]);
			createTweets(a);
			accounts.add(a);
		}
	}

	void createTweets(AccountDTO a) {
		int counter = 0;
		int N = tweets.length;
		for (int i = 0; i < 10; i++) {
			TweetDTO t = new TweetDTO();
			t.setContent(tweets[i % N]);
			t.setTime_created(new Date());
			t.setSender(a);
			t.setType(TweetType.GENERAL);
			t.setTweet_id(counter++);
		}
	}

	Random rnd = new Random(accounts.size());
	
	@Override
	public AccountDTO register(AccountDTO a) {
		a.setImageUrl(imageUrls[rnd.nextInt(imageUrls.length)]);
		a.setLastLoginTime(new Date());
		a.setTimeCreated(new Date());
		accounts.add(a);
		return a;
	}
	
	@Override
	public AccountDTO doLogin(String code) {
		return accounts.get(rnd.nextInt(accounts.size()));
	}
	
	@Override
	public AccountDetails getAccessToken(String code) throws Exception {
		return new AccountDetails();
	}

	@Override
	public AccountDTO getLoggedInUser() {
		return accounts.get(rnd.nextInt());
	}

	@Override
	public AccountDTO loginAccountById(long accountId) {
		for(AccountDTO a : accounts) {
			if (a.getId() == accountId) {
				return a;
			}
		}
		return null;
	}

	@Override
	public AccountDTO getAccountById(long accountId) {
		for(AccountDTO a : accounts) {
			if (a.getId() == accountId) {
				return a;
			}
		}
		return null;
	}

	@Override
	public void logoutAccount() {
		// TODO Auto-generated method stub
	}

}
