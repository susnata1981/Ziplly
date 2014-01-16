package com.ziplly.app.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.widget.cell.BusinessAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.BusinessAccountDTO;

public class BusinessView extends Composite implements View<BusinessView.EntityListViewPresenter> {

	private static final int PAGE_SIZE = 2;

	private static BusinessViewUiBinder uiBinder = GWT.create(BusinessViewUiBinder.class);

	public interface EntityListViewPresenter extends Presenter {
		public void onRangeChangeEvent(int start, int pageSize);
	}
	
	interface BusinessViewUiBinder extends UiBinder<Widget, BusinessView> {
	}

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	CellList<BusinessAccountDTO> businessList;

//	private AccountDTO account;
//
//	public BusinessView() {
//		setBusinessList(new CellList<BusinessAccountDTO>(new BusinessAccountCell()));
//		getBusinessList().setPageSize(PAGE_SIZE);
//		pager = new SimplePager();
//		initWidget(uiBinder.createAndBindUi(this));
//	}
//
//	public void display(List<BusinessAccountDTO> input) {
//		ListDataProvider<BusinessAccountDTO> provider = new ListDataProvider<BusinessAccountDTO>();
//		provider.addDataDisplay(businessList);
//		pager.setDisplay(businessList);
//		businessList.setRowCount(input.size());
//		provider.setList(input);
//	}
//
//	public CellList<BusinessAccountDTO> getBusinessList() {
//		return businessList;
//	}
//
//	public void setBusinessList(CellList<BusinessAccountDTO> residentsList) {
//		this.businessList = residentsList;
//	}
//
//	public void setTotalRowCount(Long count) {
//		// TODO Auto-generated method stub
//		
//	}
	
	private AccountDTO account;

	private EntityListViewPresenter presenter;

	public BusinessView() {
		businessList = new CellList<BusinessAccountDTO>(new BusinessAccountCell());
		businessList.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		pager.setDisplay(businessList);
		initWidget(uiBinder.createAndBindUi(this));
		setupHandlers();
	}

	Range currentRange;
	private void setupHandlers() {
		businessList.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Range r = event.getNewRange();
				currentRange = r;
				System.out.println("start="+r.getStart()+" Length="+r.getLength());
				presenter.onRangeChangeEvent(r.getStart(), PAGE_SIZE);
			}
		});
	}

	public void display(List<BusinessAccountDTO> input) {
//		ListDataProvider<PersonalAccountDTO> provider = new ListDataProvider<PersonalAccountDTO>();
//		provider.addDataDisplay(residentsList);
//		pager.setDisplay(residentsList);
//		residentsList.setRowCount(input.size());
//		provider.setList(input);
		int start = currentRange == null ? 0 : currentRange.getStart();
		businessList.setRowData(start, input);
//		residentsList.setRowCount(totalRowCount, true);
	}

	int totalRowCount;
	public void setTotalRowCount(Long count) {
		totalRowCount = count.intValue();
		businessList.setRowCount(totalRowCount, true);
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
	}
}
