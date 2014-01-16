package com.ziplly.app.shared;

import java.util.List;

import net.customware.gwt.dispatch.shared.Result;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;

public class GetEntityResult implements Result {
	private List<AccountDTO> accounts;
	private EntityType entityType;
	private Long count;
	
	public GetEntityResult(List<AccountDTO> accounts, EntityType type) {
		this.setAccounts(accounts);
		this.entityType = type;
	}

	public GetEntityResult() {
	}

	public List<AccountDTO> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountDTO> accounts) {
		this.accounts = accounts;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public void setEntityCount(Long count) {
		this.setCount(count);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
