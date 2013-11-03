package com.ziplly.app.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.ziplly.app.client.widget.cell.PersonalAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class ResidentsView extends Composite {

	private static final int PAGE_SIZE = 10;

	private static ResidentsViewUiBinder uiBinder = GWT
			.create(ResidentsViewUiBinder.class);

	interface ResidentsViewUiBinder extends UiBinder<Widget, ResidentsView> {
	}

	@UiField(provided = true)
	SimplePager pager;
	
	@UiField(provided=true)
	CellList<PersonalAccountDTO> residentsList;
	
	private AccountDTO account;

	public ResidentsView() {
		setResidentsList(new CellList<PersonalAccountDTO>(new PersonalAccountCell()));
		getResidentsList().setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void display(List<PersonalAccountDTO> input) {
//		if (account == null) {
//			dataProvider.addDataDisplay(getResidentsList());
//		}
//		this.account = list;
//		dataProvider.setAccount(list);
//		dataProvider.setView(this);
//		pager.setDisplay(getResidentsList());
		ListDataProvider<PersonalAccountDTO> provider = new ListDataProvider<PersonalAccountDTO>();
		provider.addDataDisplay(residentsList);
		pager.setDisplay(residentsList);
		residentsList.setRowCount(input.size());
		provider.setList(input);
	}

	public CellList<PersonalAccountDTO> getResidentsList() {
		return residentsList;
	}

	public void setResidentsList(CellList<PersonalAccountDTO> residentsList) {
		this.residentsList = residentsList;
	}
}
