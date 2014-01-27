package com.ziplly.app.server.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.inject.Inject;
import com.ziplly.app.client.exceptions.InternalError;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.GetImageUploadUrlAction;
import com.ziplly.app.shared.GetImageUploadUrlResult;

public class GetImageUploadUrlActionHandler extends AbstractAccountActionHandler<GetImageUploadUrlAction, GetImageUploadUrlResult>{
	private Logger logger = Logger.getLogger(GetImageUploadUrlAction.class.getSimpleName());
	
	@Inject
	public GetImageUploadUrlActionHandler(AccountDAO accountDao,
			SessionDAO sessionDao, AccountBLI accountBli) {
		super(accountDao, sessionDao, accountBli);
	}

	@Override
	public GetImageUploadUrlResult execute(GetImageUploadUrlAction action,
			ExecutionContext arg1) throws DispatchException {
		
		String imageUploadUrl = accountBli.getImageUploadUrl();
		if (imageUploadUrl == null) {
			throw new InternalError("Failed to create image upload url");
		}
		logger.log(Level.INFO, String.format("Upload(RESULT) url set to %s", imageUploadUrl));

		GetImageUploadUrlResult result = new GetImageUploadUrlResult(imageUploadUrl);
		return result;
	}

	@Override
	public Class<GetImageUploadUrlAction> getActionType() {
		return GetImageUploadUrlAction.class;
	}
	
}
