package com.ziplly.app.client.view.coupon;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.model.CouponTransactionDTO;

public class CouponTransactionTableWidget extends Composite {

	interface CouponTransactionTableWidgetUiBinder extends UiBinder<Widget, CouponTransactionTableWidget> {
	}
	
	CouponTransactionTableWidgetUiBinder uiBinder = GWT.create(CouponTransactionTableWidgetUiBinder.class);

	@UiField(provided = true)
	SimplePager pager;
	@UiField(provided = true)
	CellTable<CouponTransactionDTO> couponTransactionTable;
	@UiField
	Alert message;
	
	private List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
	private final int pageSize;
	private int transactionStart;
	private BasicDataFormatter basicDataFormatter;

	private Handler rangeChangeHandler;
	
	public CouponTransactionTableWidget(
			final List<ColumnDefinition> columnDefinitions, 
			final int pageSize, 
			final BasicDataFormatter basicDataFormatter) {
		
		this.columnDefinitions = columnDefinitions;
		this.pageSize = pageSize;
		this.basicDataFormatter = basicDataFormatter;
		setupUi();
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
	}

	private void setupUi() {
		pager = new SimplePager();
		couponTransactionTable = new CellTable<CouponTransactionDTO>();
		couponTransactionTable.setHover(true);
		buildTable();
		pager.setDisplay(couponTransactionTable);
		pager.setPageSize(pageSize);
		setupHandlers();
  }

	private void setupHandlers() {
		couponTransactionTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				CouponTransactionTableWidget.this.transactionStart = event.getNewRange().getStart();
				if (rangeChangeHandler != null) {
					rangeChangeHandler.onRangeChange(event);
				}
			}
		});
  }

	public void setRowCount(int couponCount) {
		couponTransactionTable.setRowCount(couponCount, true);
	}
	
	public void displayCouponTransactions(List<CouponTransactionDTO> transactions) {
		message.setVisible(false);
		if (transactions == null || transactions.size() == 0) {
			displayMessage(StringConstants.NO_COUPON_TRANSACTIONS, AlertType.INFO);
			return;
		}
		
		couponTransactionTable.setRowData(transactionStart, transactions);
	}
	
	private void buildTable() {
		for(ColumnDefinition column : columnDefinitions) {
			couponTransactionTable.addColumn(column.getColumn(), column.getTitle());
		}
  }
	
	public CellTable<CouponTransactionDTO> getCouponTransactionTable() {
		return couponTransactionTable;
	}
	
  public int getCouponDataStartIndex() {
		return transactionStart;
  }

  public void setTotalCouponCount(Long totalCouponCount) {
		couponTransactionTable.setRowCount(totalCouponCount.intValue(), true);
  }

	public void clearTable() {
		couponTransactionTable.setRowCount(0);
  }
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	public void addRangeChangeHandler(com.google.gwt.view.client.RangeChangeEvent.Handler handler) {
		this.rangeChangeHandler = handler;
  }
}
