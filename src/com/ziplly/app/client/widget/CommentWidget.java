package com.ziplly.app.client.widget;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class CommentWidget extends Composite implements HasText {

	private static CommentWidgetUiBinder uiBinder = GWT.create(CommentWidgetUiBinder.class);

	interface CommentWidgetUiBinder extends UiBinder<Widget, CommentWidget> {
	}

	public CommentWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Image profileImage;
	@UiField
	Anchor profileNameAnchor;
	@UiField
	MouseHoverPanel contentPanel;
	@UiField
	SpanElement content;
	@UiField
	SpanElement postingDate;

	@Override
	public String getText() {
		return content.getInnerText();
	}

	@Override
	public void setText(String text) {
		content.setInnerHTML(text);
	}

	public void setProfileName(String name) {
		if (name != null) {
			profileNameAnchor.setHTML(name);
		}
	}
	
	public Anchor getProfileAnchor() {
		return profileNameAnchor;
	}
	
	public void setImage(String url) {
		if (url != null) {
			profileImage.setUrl(url);
		}
	}
	
	public void setPostingDate(String date) {
		postingDate.setInnerHTML(date);
	}
	
	public MouseHoverPanel getContentPanel() {
		return contentPanel;
	}
	
	public SpanElement getContentElement() {
		return content;
	}
}
