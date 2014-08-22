package com.ziplly.app.client.view.coupon;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.CouponItemDTO;

public class CouponTransactionTableWidget extends Composite {

	interface CouponTransactionTableWidgetUiBinder extends UiBinder<Widget, CouponTransactionTableWidget> {
	}
	
	CouponTransactionTableWidgetUiBinder uiBinder = GWT.create(CouponTransactionTableWidgetUiBinder.class);

	@UiField(provided = true)
	SimplePager pager;
	@UiField(provided = true)
	CellTable<CouponItemDTO> couponTransactionTable;
	@UiField
	Alert message;
	
	private List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
	private final int pageSize;
	private int transactionStart;

	private Handler rangeChangeHandler;
	
	public CouponTransactionTableWidget(
			final List<ColumnDefinition> columnDefinitions, 
			final int pageSize) {
		
		this.columnDefinitions = columnDefinitions;
		this.pageSize = pageSize;
		setupUi();
		initWidget(uiBinder.createAndBindUi(this));
		StyleHelper.show(message.getElement(), false);
	}

	private void setupUi() {
		pager = new SimplePager();
		couponTransactionTable = new CellTable<CouponItemDTO>();
		couponTransactionTable.setEmptyTableWidget(new Label("No transaction"));
//		couponTransactionTable.setRowData(0, new ArrayList<CouponItemDTO>());
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
	  System.out.println("Setting rowcount to "+couponCount);
		couponTransactionTable.setRowCount(couponCount, true);
	}
	
	public void displayPurchasedCoupons(List<CouponItemDTO> purchasedCoupons) {
//	  StyleHelper.show(message.getElement(), false);
		System.out.println("RD = " + purchasedCoupons.size());
		if (purchasedCoupons.size() == 0) {
		  setRowCount(purchasedCoupons.size());
		  return;
		}
		
		couponTransactionTable.setRowData(transactionStart, purchasedCoupons);
	}
	
	private void buildTable() {
		for(ColumnDefinition column : columnDefinitions) {
			couponTransactionTable.addColumn(column.getColumn(), column.getTitle());
		}
  }
	
	public CellTable<CouponItemDTO> getTable() {
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
		StyleHelper.show(message.getElement(), true);
	}

	public void addRangeChangeHandler(com.google.gwt.view.client.RangeChangeEvent.Handler handler) {
		this.rangeChangeHandler = handler;
  }
}
