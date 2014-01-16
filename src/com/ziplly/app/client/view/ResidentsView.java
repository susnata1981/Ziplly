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
import com.ziplly.app.client.widget.cell.PersonalAccountCell;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public class ResidentsView extends Composite implements View<ResidentsView.EntityListViewPresenter>{
	private static final int PAGE_SIZE = 3;

	public interface EntityListViewPresenter extends Presenter {
		public void onRangeChangeEvent(int start, int pageSize);
	}
	
	private static ResidentsViewUiBinder uiBinder = GWT.create(ResidentsViewUiBinder.class);

	interface ResidentsViewUiBinder extends UiBinder<Widget, ResidentsView> {
	}

	@UiField(provided = true)
	SimplePager pager;

	@UiField(provided = true)
	CellList<PersonalAccountDTO> residentsList;

	private AccountDTO account;

	private EntityListViewPresenter presenter;

	public ResidentsView() {
		residentsList = new CellList<PersonalAccountDTO>(new PersonalAccountCell());
		residentsList.setPageSize(PAGE_SIZE);
		pager = new SimplePager();
		pager.setDisplay(residentsList);
		initWidget(uiBinder.createAndBindUi(this));
		setupHandlers();
	}

	Range currentRange;
	private void setupHandlers() {
		residentsList.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Range r = event.getNewRange();
				currentRange = r;
				System.out.println("start="+r.getStart()+" Length="+r.getLength());
				presenter.onRangeChangeEvent(r.getStart(), PAGE_SIZE);
			}
		});
	}

	public void display(List<PersonalAccountDTO> input) {
		int start = currentRange == null ? 0 : currentRange.getStart();
		residentsList.setRowData(start, input);
	}

	int totalRowCount;
	public void setTotalRowCount(Long count) {
		totalRowCount = count.intValue();
		residentsList.setRowCount(totalRowCount, true);
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
