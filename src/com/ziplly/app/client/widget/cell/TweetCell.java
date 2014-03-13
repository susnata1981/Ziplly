package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.TweetDTO;

public class TweetCell extends AbstractCell<TweetDTO> {
	EventBus eventBus;

	public TweetCell(EventBus eventBus) {
		super(BrowserEvents.CLICK);
		this.eventBus = eventBus;
	}

	@Override
	public void render(Context context, TweetDTO tweet, SafeHtmlBuilder sb) {
		StringBuilder resp = new StringBuilder();
		if (tweet != null) {
			AccountDTO acct = tweet.getSender();
			StringBuilder commentsContent = new StringBuilder();
			int count = tweet.getComments().size();
			for (int i = 0; i < Math.min(3, count); i++) {
				commentsContent.append("<div class='tweet_comment'>" + "<div class='tweet_image'>"
				    + "<a href='#account/"
				    + acct.getAccountId()
				    + "'>"
				    + "<img src='"
				    + tweet.getSender().getImageUrl()
				    + "'>"
				    + "</a>"
				    + "</div>"
				    + "<div class='tweet_content'>"
				    + "<div class='comment_text'>"
				    + tweet.getContent()
				    + "</div>"
				    + "<div class='tweet_sender_link'>"
				    + "posted by <a href='#account/"
				    + acct.getAccountId()
				    + "'>"
				    + tweet.getSender().getDisplayName()
				    + "</a>"
				    + ", on "
				    + DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG).format(tweet.getTimeCreated())
				    + "</a>" + "</div>" + "</div>" + "</div>");
			}
			;

			commentsContent.append("<div class='comment_box'>"
			    + "<input class='comment_input' placeholder='enter your comments here...' type='text'/>"
			    + "<a href='#'>Post</a>" + "</div>");

			resp.append("<div class='tweet'>" + "<div class='tweet_block'>" + "<div class='tweet_image'>"
			    + "<a href='#account/"
			    + acct.getAccountId()
			    + "'>"
			    + "<img src='"
			    + tweet.getSender().getImageUrl()
			    + "'>"
			    + "</a>"
			    + "</div>"
			    + "<div class='tweet_content'>"
			    + "<div class='tweet_text'>"
			    + tweet.getContent()
			    + "</div>"
			    + "<div class='tweet_actions'>"
			    + "<div>"
			    + "<div class='tweet_action_link'>"
			    + "<a href=''>Like</a>"
			    + "</div>"
			    + "<div class='tweet_action_link'>"
			    + "<a href=''>Reply</a>"
			    + "</div>"
			    + "<div class='tweet_sender_link'>"
			    + "posted by <a href='#account/"
			    + acct.getAccountId()
			    + "'>"
			    + tweet.getSender().getDisplayName()
			    + "</a>"
			    + ", on "
			    + DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG).format(tweet.getTimeCreated())
			    + "</a>"
			    + "</div>"
			    + "</div>"
			    + "</div>"
			    + "</div>"
			    + "</div>"
			    + "<div class='comments'>" + commentsContent + "</div>" + "</div>");
		}
		sb.appendHtmlConstant(resp.toString());
	}

	@Override
	public void onBrowserEvent(Context context,
	    Element parent,
	    TweetDTO value,
	    NativeEvent event,
	    ValueUpdater<TweetDTO> valueUpdater) {

		// fire event
		EventTarget eventTarget = event.getEventTarget();
		Element as = Element.as(eventTarget);
		if (as.getTagName().matches("INPUT")) {
			// parent.setInnerHTML("<button>Post<button>");
		}
	}
}
