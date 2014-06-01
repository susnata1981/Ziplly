package com.ziplly.app.dao;

import java.util.List;

import com.ziplly.app.model.AccountNotificationDTO;
import com.ziplly.app.server.model.jpa.AccountNotification;

public interface AccountNotificationDAO {
	void save(AccountNotification an);

	List<AccountNotificationDTO> findAccountNotificationByAccountId(Long accountId);

	void update(AccountNotification an);

	AccountNotification findAccountNotificationByConversationId(Long id);
}
