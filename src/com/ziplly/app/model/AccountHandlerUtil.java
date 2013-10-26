package com.ziplly.app.model;



public class AccountHandlerUtil {
	public static <K extends Account> AccountDTO getAccountDTO(K account) {
		if (account instanceof PersonalAccount) {
			return new PersonalAccountDTO((PersonalAccount)account);
		} else if (account instanceof BusinessAccount) {
			BusinessAccountDTO ba = new BusinessAccountDTO((BusinessAccount)account);
			return ba;
		}
		return new AccountDTO(account);
	}
	
	public static <K extends AccountDTO> Account getAccount(K account) {
		if (account instanceof PersonalAccountDTO) {
			return new PersonalAccount((PersonalAccountDTO)account);
		} else if (account instanceof BusinessAccountDTO) {
			return new BusinessAccount((BusinessAccountDTO)account);
		}
		return new Account(account);
	}
}
