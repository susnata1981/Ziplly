package com.ziplly.app.client.activities;

import com.ziplly.app.client.view.AccountView;
import com.ziplly.app.client.view.BusinessAccountView;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class AccountViewRendererFactory {
	private static AccountView av = new AccountView();
	private static BusinessAccountView bav = new BusinessAccountView();
	
	// Is there a better solution
	public static <T extends AccountDTO> IAccountViewRenderer<T> getAccountViewRenderer(
			AccountDTO account, 
			AccountActivityPresenter presenter) {
		
		if (account == null) {
			return (IAccountViewRenderer<T>) new PersonalAccountViewRenderer(av);
		}
		
		if (account instanceof PersonalAccountDTO) {
			av.setPresenter(presenter);
			PersonalAccountViewRenderer renderer = new PersonalAccountViewRenderer(av);
			return (IAccountViewRenderer<T>) renderer; 
		} 
		else if (account instanceof BusinessAccountDTO) {
			bav.setPresenter(presenter);
			BusinessAccountViewRenderer renderer = new BusinessAccountViewRenderer(bav);
			return (IAccountViewRenderer<T>) renderer;
		}
		
		return (IAccountViewRenderer<T>) new PersonalAccountViewRenderer(av);
	}
}
