package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.activities.SendMessagePresenter;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.cell.BusinessAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.GetEntityListAction;
import com.ziplly.app.shared.ValidationResult;

public class BusinessView extends Composite implements View<BusinessView.EntityListViewPresenter> {

	private static final int PAGE_SIZE = 10;
	private SendMessageWidget smw;
	private static BusinessViewUiBinder uiBinder = GWT.create(BusinessViewUiBinder.class);

	public interface EntityListViewPresenter extends Presenter {
		public void onRangeChangeEvent(int start, int pageSize);

		public void getBusinessList(GetEntityListAction currentEntityListAction);
	}
	
	interface BusinessViewUiBinder extends UiBinder<Widget, BusinessView> {
	}

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	CellList<BusinessAccountDTO> businessList;
	
	//
	// Search
	//
	@UiField
	ControlGroup zipCg;
	@UiField
	TextBox zipTextBox;
	@UiField
	HelpInline zipError;
	
	@UiField
	Button searchBtn;
	
	@UiField
	Button resetBtn;
	
	@UiField
	Alert message;
	
	private EntityListViewPresenter presenter;
	private CommunityViewState state;
	
	public BusinessView() {
		businessList = new CellList<BusinessAccountDTO>(new BusinessAccountCell());
		businessList.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		pager.setDisplay(businessList);
		initWidget(uiBinder.createAndBindUi(this));
		state = new CommunityViewState(EntityType.BUSINESS_ACCOUNT, PAGE_SIZE);
		StyleHelper.show(message.getElement(), false);
		setupHandlers();
	}

	private void setupHandlers() {
		businessList.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				state.setRange(event.getNewRange());
				presenter.getBusinessList(state.getCurrentEntityListAction());
			}
		});
	}

	public void display(List<BusinessAccountDTO> input) {
		enableButtonIfRequired();
		StyleHelper.show(message.getElement(), false);
		if (input.size() == 0) {
			displayNoResult();
		}
		businessList.setRowData(state.getStart(), input);
	}

	private void displayNoResult() {
		businessList.setRowCount(0);
		message.setAnimation(true);
		message.setClose(false);
		message.setText(StringConstants.NO_RESULT_FOUND);
		StyleHelper.show(message.getElement(), true);
	}

	public void setTotalRowCount(Long count) {
		businessList.setRowCount(count.intValue(), true);
	}

	private boolean validateZip() {
		String zipInput = zipTextBox.getText().trim();
		ValidationResult validateZip = FieldVerifier.validateZip(zipInput);
		if (!validateZip.isValid()) {
			zipError.setText(validateZip.getErrors().get(0).getErrorMessage());
			zipCg.setType(ControlGroupType.ERROR);
			return false;
		}
		return true;
	}

	@UiHandler("searchBtn")
	void search(ClickEvent event) {
		clearErrors();
		boolean valid = validateZip();
		if (!valid) {
			return;
		}
		searchBtn.setEnabled(false);
		state.searchByZip(FieldVerifier.sanitize(zipTextBox.getText()));
		presenter.getBusinessList(state.getCurrentEntityListAction());
	}
	
	@UiHandler("resetBtn")
	void resetSearch(ClickEvent event) {
		state.reset();
		clear();
		presenter.getBusinessList(state.getCurrentEntityListAction());
	}
	
	@Override
	public void setPresenter(EntityListViewPresenter presenter) {
		this.presenter = presenter;
	}

	public int getPageSize() {
		return PAGE_SIZE;
	}
	
	private void enableButtonIfRequired() {
		if (!searchBtn.isEnabled()) {
			searchBtn.setEnabled(true);
		}
		
		if (!resetBtn.isEnabled()) {
			resetBtn.setEnabled(true);
		}
	}
	
	@Override
	public void clear() {
		zipTextBox.setText("");
		clearErrors();
	}
	
	private void clearErrors() {
		zipCg.setType(ControlGroupType.NONE);
		zipError.setText("");
	}

	public void displaySendMessageWidget(Long accountId) {
		AccountDTO receiver = new AccountDTO();
		receiver.setAccountId(accountId);
		smw = new SendMessageWidget(receiver);
		smw.setPresenter((SendMessagePresenter) presenter);
		smw.show();
	}

	public void updateMessageWidget(AccountDTO account) {
		if (smw != null) {
			smw.updateAccountInformation(account);
		}
	}
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}
}
