package com.ziplly.app.client.view.account;

import java.util.List;

import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.model.AccountDTO;

public interface AccountViewPresenter<T extends AccountDTO> extends Presenter {

//  void save(AccountDTO account);

  void displayProfile();

//  void logout();

//  void settingsLinkClicked();

//  void messagesLinkClicked();

  void invitePeople(List<String> emails);
}
