package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.ziplly.app.client.view.AbstractAccountView;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.event.LoginEvent;
import com.ziplly.app.client.view.event.UserInfoFormClosedEvent;
import com.ziplly.app.client.view.handler.LoginEventHandler;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountDetails;
import com.ziplly.app.model.Category;
import com.ziplly.app.model.InterestList;
import com.ziplly.app.model.InterestList.Activity;

public class AccountInfoFormWidget extends AbstractAccountView {

	private static final int MIN_WORD_COUNT = 5;
	private static final int MIN_CHARACTER_COUNT = 10;
	private Logger logger = Logger.getLogger("AccountInfoFormWidget");
	private static AccountInfoFormWidgetUiBinder uiBinder = GWT
			.create(AccountInfoFormWidgetUiBinder.class);

	interface Style extends CssResource {
		String checkbox();
	}

	interface AccountInfoFormWidgetUiBinder extends
			UiBinder<Widget, AccountInfoFormWidget> {
	}

	Map<String, CheckBox> categoryList = new HashMap<String, CheckBox>();;
	private InterestList interestList;

	@UiField
	Modal categoryListModal;

	@UiField
	HTMLPanel interestPanel;

	@UiField
	Button saveBtn;

	@UiField
	Button closeBtn;

	@UiField
	TextArea introField;

	@UiField
	HelpInline introTextHelp;

	@UiField
	ControlGroup introTextControlGroup;

	@UiField
	TextBox zipTextBox;
	
	@UiField
	ControlGroup zipControlGroup;
	
	@UiField
	HelpInline zipTextHelp;
	
	@UiField
	Style style;

	ListDataProvider<Category> interestDataProvider;
	protected AccountDTO account;

	public AccountInfoFormWidget(SimpleEventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void postInitWidget() {
		populateInterestPanel();
	}

	@Override
	protected void setupUiElements() {
		interestList = InterestList.getInstance();
	}

	public void populateInterestPanel() {
		categoryList.clear();
		interestPanel.clear();
		Map<Activity, List<String>> map = interestList.getInterests();
		Accordion accordion = new Accordion();
		for (Activity activity : map.keySet()) {
			List<String> interests = map.get(activity);
			AccordionGroup ag = new AccordionGroup();
			ag.setHeading(activity.name().toLowerCase());
			for (String interest : interests) {
				CheckBox cb = new CheckBox(interest);
				cb.addStyleName(style.checkbox());
				categoryList.put(interest.toLowerCase(), cb);
				ag.add(cb);
			}
			accordion.add(ag);
		}
		interestPanel.add(accordion);
//		showSelectedInterests(ad.categories);
		updateAccountIntro();
	}

	@Override
	protected void setupHandlers() {
		eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {

			@Override
			public void onEvent(LoginEvent event) {
				AccountInfoFormWidget.this.account = event.getAccount();
			}
		});
	}

	void showSelectedInterests(List<Category> categories) {
		if (categories == null) {
			throw new IllegalArgumentException(
					"Invalid input to showSelectedInterests");
		}

		for (Category category : categories) {
			String interest = category.getCategoryType().toLowerCase();
			if (categoryList.containsKey(interest)) {
				categoryList.get(interest).setValue(true);
			}
		}
	}

	@UiHandler("closeBtn")
	void close(ClickEvent event) {
		closePanel();
	}

	public void closePanel() {
		categoryListModal.hide();
		eventBus.fireEvent(new UserInfoFormClosedEvent());
	}

	boolean validate() {
		if (!validateUserIntroduction()) {
			return false;
		}
		introTextControlGroup.setType(ControlGroupType.SUCCESS);
		
		if (!validateZip()) {
			return false;
		}
		zipControlGroup.setType(ControlGroupType.SUCCESS);
		return true;
	}

	boolean validateUserIntroduction() {
		if (introField.getText() == null) {
			introTextHelp.setText("Can't be empty");
			introTextControlGroup.setType(ControlGroupType.ERROR);
			return false;
		} 
		
		String introduction = SafeHtmlUtils.fromString(introField.getText()).asString();
		String[] words = introduction.split("\\s+");
		if (introduction.length() < MIN_CHARACTER_COUNT) {
			introTextControlGroup.setType(ControlGroupType.ERROR);
			introTextHelp.setText("Can't be less than "+MIN_CHARACTER_COUNT+" characters");
			return false;
		}
		if (words.length < MIN_WORD_COUNT) {
			introTextControlGroup.setType(ControlGroupType.ERROR);
			introTextHelp.setText("Can't be less than "+MIN_WORD_COUNT+" words");
			return false;
		}
		return true;
	}
	
	boolean validateZip() {
		if (zipTextBox.getText() == null) {
			zipTextHelp.setText("Can't be empty");
			zipControlGroup.setType(ControlGroupType.ERROR);
			return false;
		} 
		
		String zip = SafeHtmlUtils.fromString(zipTextBox.getText()).asString();
		try {
			Integer.parseInt(zip);
		} catch(IllegalArgumentException e) {
			zipControlGroup.setType(ControlGroupType.ERROR);
			zipTextBox.setText("");
			return false;
		}
		
		return true;
	}
	
	AccountDetails getAccountPrerefencesFromUi() {
		AccountDetails input = new AccountDetails();
		input.account = new Account();
		String zip = SafeHtmlUtils.htmlEscape(zipTextBox.getText());
		input.account.setZip(Integer.parseInt(zip));
		List<Category> categories = new ArrayList<Category>();
		for (CheckBox cb : categoryList.values()) {
			if (cb.getValue()) {
				Category c = new Category();
				c.setCategory(cb.getText().toLowerCase());
				categories.add(c);
				input.categories.add(c);
			}
		}
		input.account.setIntroduction(introField.getText());
		return input;
	}

	@UiHandler("saveBtn")
	void submit(ClickEvent e) {
		if (!validate()) {
			return;
		}

		// copy account data first
//		AccountDetails input = getAccountPrerefencesFromUi();

//		getService().saveAccountDetails(input, new AsyncCallback<AccountDetails>() {
//			@Override
//			public void onSuccess(AccountDetails result) {
//				if (result != null) {
//					eventBus.fireEvent(new AccountUpdateEvent(result));
//					AccountInfoFormWidget.this.ad = result;
//					AccountInfoFormWidget.this.populateInterestPanel();
//					closePanel();
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				logger.log(Level.SEVERE, "Couldn't save user preferences:"
//						+ caught.getMessage());
//			}
//		});
	}

	void updateAccountIntro() {
		System.out.println("Updating intro:" + account.getIntroduction());
		introField.setText(account.getIntroduction());
	}

	public void show() {
		categoryListModal.show();
	}

	@Override
	protected void internalOnUserLogin() {
		// TODO Auto-generated method stub
		
	}
}
