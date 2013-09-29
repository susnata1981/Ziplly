package com.ziplly.app.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.ziplly.app.dao.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.TxnWork;
import com.literati.app.shared.Account;

public class ConversationDAOImpl implements ConversationDAO {
	@Override
	public List<ConversationDO> getConversationForAccount(int start, int length, Long accountId) {
		checkNotNull(accountId);
		List<ConversationDO> conversations = ofy().load().type(ConversationDO.class).filter("receiver", 
				Key.create(Account.class, accountId)).list();
		return conversations;
	}

	@Override
	public boolean save(final ConversationDO conversation) {
		checkNotNull(conversation);
		final MessageDO msg = conversation.getMessages().get(0);
		final ConversationDO newConversationDO = new ConversationDO();
		newConversationDO.setSender(conversation.getSenderKey());
		newConversationDO.setReceiver(conversation.getReceiverKey());
		return ofy().transact(new TxnWork<Objectify,Boolean>() {
			public Boolean run(Objectify o) {
				Key<ConversationDO> now = ofy().save().entity(newConversationDO).now();
				msg.setConversation(newConversationDO);
				ofy().save().entity(msg).now();
				newConversationDO.getMessages().add(msg);
				now = ofy().save().entity(newConversationDO).now();
				return now != null;
			}
		});
	}

	@Override
	public boolean save(Account sender, Account receiver, final MessageDO msg) {
		checkNotNull(msg);
		final ConversationDO newConversationDO = new ConversationDO();
		newConversationDO.setSender(sender);
		newConversationDO.setReceiver(receiver);
		return ofy().transact(new TxnWork<Objectify,Boolean>() {
			public Boolean run(Objectify o) {
				Key<ConversationDO> now = ofy().save().entity(newConversationDO).now();
				msg.setConversation(newConversationDO);
				ofy().save().entity(msg).now();
				newConversationDO.getMessages().add(msg);
				now = ofy().save().entity(newConversationDO).now();
				return now != null;
			}
		});
	}
}
