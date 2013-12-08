package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.HashtagDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.HashtagDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetHashtagAction;
import com.ziplly.app.shared.GetHashtagResult;

public class GetHashtagActionHandler extends AbstractAccountActionHandler<GetHashtagAction, GetHashtagResult> {

	private HashtagDAO hashtagDao;

	@Inject
	public GetHashtagActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, HashtagDAO hashtagDao) {
		super(accountDao, sessionDao, accountBli);
		this.hashtagDao = hashtagDao;
	}

	@Override
	public GetHashtagResult execute(GetHashtagAction action, ExecutionContext arg1)
			throws DispatchException {
		
		if (action == null) {
			throw new IllegalArgumentException();
		}
		
		int n = action.getSize() != 0 ? action.getSize() : 10;
		
		List<HashtagDTO> hashtags = hashtagDao.findTopHashtag(n);
		GetHashtagResult result = new GetHashtagResult();
		result.setHashtags(hashtags);
		return result;
	}

	@Override
	public Class<GetHashtagAction> getActionType() {
		return GetHashtagAction.class;
	}

}
