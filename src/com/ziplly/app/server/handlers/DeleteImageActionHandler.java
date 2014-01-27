package com.ziplly.app.server.handlers;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.DeleteImageAction;
import com.ziplly.app.shared.DeleteImageResult;

public class DeleteImageActionHandler extends AbstractAccountActionHandler<DeleteImageAction, DeleteImageResult>{

	private static final String BLOBSTORE_KEY_STRING = "encoded_gs_key";

	@Inject
	public DeleteImageActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public DeleteImageResult execute(DeleteImageAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkArgument(action.getImageUrl() != null);
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String imgUrl = action.getImageUrl();
		if (imgUrl.indexOf(BLOBSTORE_KEY_STRING) != -1) {
			BlobKey key = new BlobKey(imgUrl.substring(imgUrl.indexOf(BLOBSTORE_KEY_STRING)));
			blobstoreService.delete(key);
		}
		return new DeleteImageResult();
	}

	@Override
	public Class<DeleteImageAction> getActionType() {
		return DeleteImageAction.class;
	}
}
