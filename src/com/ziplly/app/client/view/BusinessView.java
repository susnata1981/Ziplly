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
import com.ziplly.app.client.widget.cell.BusinessAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;

public class BusinessView extends Composite {

	private static final int PAGE_SIZE = 10;

	private static BusinessViewUiBinder uiBinder = GWT.create(BusinessViewUiBinder.class);

	interface BusinessViewUiBinder extends UiBinder<Widget, BusinessView> {
	}

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	CellList<BusinessAccountDTO> businessList;

	private AccountDTO account;

	public BusinessView() {
		setBusinessList(new CellList<BusinessAccountDTO>(new BusinessAccountCell()));
		getBusinessList().setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void display(List<BusinessAccountDTO> input) {
		ListDataProvider<BusinessAccountDTO> provider = new ListDataProvider<BusinessAccountDTO>();
		provider.addDataDisplay(businessList);
		pager.setDisplay(businessList);
		businessList.setRowCount(input.size());
		provider.setList(input);
	}

	public CellList<BusinessAccountDTO> getBusinessList() {
		return businessList;
	}

	public void setBusinessList(CellList<BusinessAccountDTO> residentsList) {
		this.businessList = residentsList;
	}
}
