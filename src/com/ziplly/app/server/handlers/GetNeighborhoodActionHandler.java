package com.ziplly.app.server.handlers;

import java.util.List;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.NeighborhoodDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.server.AccountBLI;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetNeighborhoodAction;
import com.ziplly.app.shared.GetNeighborhoodResult;
import com.ziplly.app.shared.ValidationResult;

public class GetNeighborhoodActionHandler extends AbstractAccountActionHandler<GetNeighborhoodAction, GetNeighborhoodResult>{

	private NeighborhoodDAO neighborhoodDao;

	@Inject
	public GetNeighborhoodActionHandler(AccountDAO accountDao, SessionDAO sessionDao,
			AccountBLI accountBli, NeighborhoodDAO neighborhoodDao) {
		super(accountDao, sessionDao, accountBli);
		this.neighborhoodDao = neighborhoodDao;
	}

	@Override
	public GetNeighborhoodResult execute(GetNeighborhoodAction action, ExecutionContext arg1)
			throws DispatchException {
		
		Preconditions.checkArgument(action != null);
		
		ValidationResult validationResult = FieldVerifier.validateZip(action.getPostalCode());
		
		if (!validationResult.isValid()) {
			throw new IllegalArgumentException("Invalid zip code");
		}

		List<NeighborhoodDTO> neighborhoods = neighborhoodDao.findByPostalCode(action.getPostalCode());
		
		GetNeighborhoodResult result = new GetNeighborhoodResult();
		result.setNeighbordhoods(neighborhoods);
		return result;
	}

	@Override
	public Class<GetNeighborhoodAction> getActionType() {
		return GetNeighborhoodAction.class;
	}

}