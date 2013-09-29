package com.ziplly.app.dao;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.literati.app.shared.Account;
import com.literati.app.shared.AccountDetails;
import com.literati.app.shared.QueryMetaData;
import com.literati.app.shared.api.request.GetAccountDetailsRequest;

public class FakeAccountDAO implements AccountDAO {

	List<Account> accounts = Lists.newArrayList();
	private String [] firstNames = {
			"Jack","Tom","Harry","Erica","Shaan","Mike","Matt"
	};
	private String [] lastNames = {
		"Kent","Metzger","Basak","Roy","Robbin","Potter"
	};
	
	public FakeAccountDAO() {
		setup();
	}
	
	void setup() {
		int i = 0;
		for(String fn: firstNames) {
			for(String ln : lastNames) {
				Account a = new Account();
				a.setId(13876*(i+1)+2132L);
				a.setFirstName(fn);
				a.setLastName(ln);
				a.setEmail(fn+"."+ln+"@gmail.com");
				a.setUrl("http://graph.facebook.com/susnata.basak");
				a.setImageUrl("http://profile.ak.fbcdn.net/hprofile-ak-prn1/41388_26402175_8091_q.jpg");
				accounts.add(a);
			}
		}
	}
	
	@Override
	public Account findByEmail(String email) {
		Random rn = new Random();
		return accounts.get(rn.nextInt(accounts.size())); 
	}

	@Override
	public Account findById(Long accountId) {
		Random rn = new Random();
		return accounts.get(rn.nextInt(accounts.size())); 
	}

	@Override
	public boolean save(Account user) {
		if (user!=null) {
			accounts.add(user);
		}
		return true;
	}

	@Override
	public List<Account> get(QueryMetaData qmd) {
		return accounts;
	}

	@Override
	public List<Account> getAll(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Account> getAccounts(GetAccountDetailsRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean save(AccountDetails ad) {
		// TODO Auto-generated method stub
		return false;
	}

}
