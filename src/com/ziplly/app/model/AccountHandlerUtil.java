package com.ziplly.app.model;

import com.ziplly.app.dao.EntityUtil;



public class AccountHandlerUtil {
	public static <K extends Account> AccountDTO getAccountDTO(K account) {
		if (account instanceof PersonalAccount) {
			return EntityUtil.convert(account);
		} else if (account instanceof BusinessAccount) {
			return EntityUtil.convert(account);
		}
		throw new IllegalArgumentException();
	}

	public static <K extends AccountDTO> Account getAccount(K account) {
		if (account instanceof PersonalAccountDTO) {
			return new PersonalAccount((PersonalAccountDTO)account);
		} else if (account instanceof BusinessAccountDTO) {
			return new BusinessAccount((BusinessAccountDTO)account);
		}
//		return new Account(account);
		throw new IllegalArgumentException();
	}
}
