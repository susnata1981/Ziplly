package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.activities.SendMessagePresenter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.SendMessageWidget;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.cell.PersonalAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.NeighborhoodDTO;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetEntityListAction;

public class ResidentsView extends AbstractView implements
    View<ResidentsView.EntityListViewPresenter> {
	private static final int PAGE_SIZE = 10;

	public interface EntityListViewPresenter extends Presenter {
		public void getPersonalAccountList(GetEntityListAction currentEntityListAction);
	}

	private static ResidentsViewUiBinder uiBinder = GWT.create(ResidentsViewUiBinder.class);

	interface ResidentsViewUiBinder extends UiBinder<Widget, ResidentsView> {
	}

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	CellList<PersonalAccountDTO> residentList;

	@UiField
	ListBox genderListBox;

	@UiField
	ListBox neighborhoodListBox;

	@UiField
	Button searchBtn;

	@UiField
	Button resetBtn;

	@UiField
	Alert status;

	@UiField
	Alert message;

	private EntityListViewPresenter presenter;
	private SendMessageWidget smw;
	private ResidentViewState state = new ResidentViewState(EntityType.PERSONAL_ACCOUNT, PAGE_SIZE);
	private List<Gender> genderList = new ArrayList<Gender>();

	private List<NeighborhoodDTO> neighborhoods;

	@Inject
	public ResidentsView(EventBus eventBus) {
		super(eventBus);
		residentList = new CellList<PersonalAccountDTO>(new PersonalAccountCell());
		residentList.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		pager.setDisplay(residentList);
		initWidget(uiBinder.createAndBindUi(this));
		message.setAnimation(true);
		message.setClose(false);
		StyleHelper.show(status.getElement(), false);
		status.setClose(false);
		setupHandlers();

		genderList.clear();
		genderListBox.clear();
		for (Gender g : Gender.getValuesForSearch()) {
			genderListBox.addItem(basicDataFormatter.format(g, ValueType.GENDER));
			genderList.add(g);
		}
		genderListBox.setSelectedIndex(genderList.indexOf(state.getGender()));
	}

	private void setupHandlers() {
		residentList.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				state.setRange(event.getNewRange());
				presenter.getPersonalAccountList(state.getCurrentEntityListAction());
			}
		});
	}

	public void displayNeighborhoodFilters(List<NeighborhoodDTO> neighborhoods) {
		if (neighborhoods != null) {
			neighborhoodListBox.clear();
			this.neighborhoods = neighborhoods;
			for (NeighborhoodDTO n : neighborhoods) {
				neighborhoodListBox.addItem(n.getName());
			}
		}
	}

	public void display(List<PersonalAccountDTO> input) {
		resetUiElements();
		if (input.size() == 0) {
			displayNoResult();
		}
		residentList.setRowData(state.getStart(), input);
	}

	private void resetUiElements() {
		enableButtonIfRequired();
		StyleHelper.show(message.getElement(), false);
		StyleHelper.show(status.getElement(), false);
	}

	private void enableButtonIfRequired() {
		if (!searchBtn.isEnabled()) {
			searchBtn.setEnabled(true);
		}

		if (!resetBtn.isEnabled()) {
			resetBtn.setEnabled(true);
		}
	}

	private void displayNoResult() {
		residentList.setRowCount(0);
		message.setText(StringConstants.NO_RESULT_FOUND);
		message.setType(AlertType.WARNING);
		StyleHelper.show(message.getElement(), true);
	}

	public void setTotalRowCount(Long count) {
		residentList.setRowCount(count.intValue(), true);
	}

	@UiHandler("searchBtn")
	void search(ClickEvent event) {
		searchBtn.setEnabled(false);
		Gender gender = genderList.get(genderListBox.getSelectedIndex());
		state.searchByGender(gender);
		state.setNeighborhood(neighborhoods
		    .get(neighborhoodListBox.getSelectedIndex())
		    .getNeighborhoodId());
		presenter.getPersonalAccountList(state.getCurrentEntityListAction());
	}

	@UiHandler("resetBtn")
	void resetSearch(ClickEvent event) {
		resetBtn.setEnabled(false);
		state.reset();
		genderListBox.setSelectedIndex(state.getGender().ordinal());
		presenter.getPersonalAccountList(state.getCurrentEntityListAction());
	}

	@Override
	public void setPresenter(EntityListViewPresenter presenter) {
		this.presenter = presenter;
	}

	public int getPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void clear() {
		genderListBox.setSelectedIndex(Gender.ALL.ordinal());
		resetUiElements();
	}

	public void displaySendMessageWidget(Long receiverAccountId) {
		AccountDTO receiver = new AccountDTO();
		receiver.setAccountId(receiverAccountId);
		smw = new SendMessageWidget(receiver);
		smw.setPresenter((SendMessagePresenter) presenter);
		smw.show();
		System.out.println("SHOWING MODAL");
	}

	public void updateMessageWidget(AccountDTO account) {
		if (smw != null) {
			smw.updateAccountInformation(account);
		}
	}

	public void displayMessage(String msg, AlertType type) {
		status.setText(msg);
		status.setType(type);
		StyleHelper.show(status.getElement(), true);
	}

	public void setNeighborhoodId(Long neighborhoodId) {
		state.setNeighborhood(neighborhoodId);
	}

	public void setBackground(NeighborhoodDTO neighborhood) {
		StyleHelper.setBackgroundImage(basicDataFormatter.format(
		    neighborhood,
		    ValueType.NEIGHBORHOOD_IMAGE));
	}
}
