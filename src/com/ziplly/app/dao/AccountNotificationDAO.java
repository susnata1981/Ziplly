package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.AccountNotification;
import com.ziplly.app.model.AccountNotificationDTO;

public interface AccountNotificationDAO {
	void save(AccountNotification an);

	List<AccountNotificationDTO> findAccountNotificationByAccountId(Long accountId);

	void update(AccountNotification an);

	AccountNotification findAccountNotificationByConversationId(Long id);
}
