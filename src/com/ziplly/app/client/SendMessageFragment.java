package com.ziplly.app.client;

import java.util.ArrayList;
import java.util.List;

public class SendMessageFragment extends Fragment<Long> {

	public SendMessageFragment(Long accountId) {
		super(accountId);
	}
	
	@Override
	public List<Long> getArguments() {
		List<Long> result = new ArrayList<Long>();
		result.add(arguments.get(0));
		return result;
	}
}
