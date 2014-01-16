package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.DeleteImageAction;
import com.ziplly.app.shared.DeleteImageResult;

public class DeleteImageActionHandler extends AbstractAccountActionHandler<DeleteImageAction, DeleteImageResult>{

	@Inject
	public DeleteImageActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public DeleteImageResult execute(DeleteImageAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkArgument(action.getImageUrl() != null);
		
		validateSession();
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		BlobKey key = new BlobKey(action.getImageUrl());
		blobstoreService.delete(key);
		return new DeleteImageResult();
	}

	@Override
	public Class<DeleteImageAction> getActionType() {
		return DeleteImageAction.class;
	}
}
