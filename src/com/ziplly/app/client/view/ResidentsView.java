package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
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
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.cell.PersonalAccountCell;
import com.ziplly.app.model.EntityType;
import com.ziplly.app.model.Gender;
import com.ziplly.app.model.PersonalAccountDTO;
import com.ziplly.app.shared.GetEntityListAction;

public class ResidentsView extends Composite implements View<ResidentsView.EntityListViewPresenter>{
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
	Button searchBtn;
	
	@UiField
	Button resetBtn;
	
	@UiField
	Alert message;
	
	private EntityListViewPresenter presenter;
	private ResidentViewState state = new ResidentViewState(EntityType.PERSONAL_ACCOUNT, PAGE_SIZE);
	
	public ResidentsView() {
		residentList = new CellList<PersonalAccountDTO>(new PersonalAccountCell());
		residentList.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		pager.setDisplay(residentList);
		initWidget(uiBinder.createAndBindUi(this));
		setupHandlers();
		
		for(Gender g : Gender.getAllValues()) {
			genderListBox.addItem(g.name().toLowerCase());
		}
		
		genderListBox.setSelectedIndex(state.getGender().ordinal());
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

	public void display(List<PersonalAccountDTO> input) {
		enableButtonIfRequired();
		StyleHelper.show(message.getElement(), false);
		if (input.size() == 0) {
			displayNoResult();
		}
		residentList.setRowData(state.getStart(), input);
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
		message.setAnimation(true);
		message.setClose(false);
		message.setText(StringConstants.NO_RESULT_FOUND);
		StyleHelper.show(message.getElement(), true);
	}

	public void setTotalRowCount(Long count) {
		residentList.setRowCount(count.intValue(), true);
	}

	@UiHandler("searchBtn")
	void search(ClickEvent event) {
		searchBtn.setEnabled(false);
		Gender gender = Gender.values()[genderListBox.getSelectedIndex()];
		state.searchByGender(gender);
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
	}
}
