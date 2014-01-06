package com.ziplly.app.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.widget.MyBundle;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class AboutView extends Composite implements HasClickHandlers , View<AboutView.AboutPresenter>{

	private static AboutViewUiBinder uiBinder = GWT.create(AboutViewUiBinder.class);

	public interface AboutPresenter extends Presenter {
		public void contact(String name, String email, String content);
	}
	
	interface AboutViewUiBinder extends UiBinder<Widget, AboutView> {
	}

	@UiField
	MyBundle resource;
	
	@UiField
	ImageElement tom;

	@UiField
	Alert message;
	
	@UiField
	TextBox subject;
	@UiField
	ControlGroup subjectCg;
	@UiField
	HelpInline subjectError;
	
	@UiField
	TextBox email;
	@UiField
	ControlGroup emailCg;
	@UiField
	HelpInline emailError;
	
	@UiField
	TextArea content;
	@UiField
	ControlGroup contentCg;
	@UiField
	HelpInline contentError;
	
	@UiField
	Button contactBtn;

	private AboutPresenter presenter;
	
	@UiFactory
	MyBundle getResource() {
		MyBundle.INSTANCE.style().ensureInjected();
		return MyBundle.INSTANCE;
	}
	
	public AboutView() {
		initWidget(uiBinder.createAndBindUi(this));
		tom.setSrc(resource.tom().getSafeUri().asString());
		clear();
		message.setVisible(false);
	}
	
	public boolean validate() {
		boolean valid = true;
		String nameInput = subject.getText().trim();
		ValidationResult result = FieldVerifier.validateName(nameInput);
		if (!result.isValid()) {
			subjectCg.setType(ControlGroupType.ERROR);
			subjectError.setText(result.getErrors().get(0).getErrorMessage());
			subjectError.setVisible(true);
			valid = false;
		}
		
		String emailInput = email.getText().trim();
		result = FieldVerifier.validateEmail(emailInput);
		if (!result.isValid()) {
			emailCg.setType(ControlGroupType.ERROR);
			emailError.setText(result.getErrors().get(0).getErrorMessage());
			emailError.setVisible(true);
			valid = false;
		}
		
		String contentInput = content.getText().trim();
		result = FieldVerifier.validateMessage(contentInput);
		if (!result.isValid()) {
			contentCg.setType(ControlGroupType.ERROR);
			contentError.setText(result.getErrors().get(0).getErrorMessage());
			contentError.setVisible(true);
			valid = false;
		}
		
		return valid;
	}
	
	@UiHandler("contactBtn")
	void submit(ClickEvent event) {
		clearError();
		if (!validate()) {
			return;
		}
		String subjectInput = FieldVerifier.sanitize(subject.getText());
		String emailInput = FieldVerifier.sanitize(email.getText());
		String contentInput = FieldVerifier.sanitize(content.getText());
		presenter.contact(subjectInput, emailInput, contentInput);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return super.addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public void setPresenter(AboutPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		subject.setText("");
		email.setText("");
		content.setText("");
	}
	
	void clearError() {
		subjectCg.setType(ControlGroupType.NONE);
		subjectError.setVisible(false);
		emailError.setVisible(false);
		emailCg.setType(ControlGroupType.NONE);
		contentError.setVisible(false);
		contentCg.setType(ControlGroupType.NONE);
	}
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}
}
