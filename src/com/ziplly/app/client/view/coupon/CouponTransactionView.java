package com.ziplly.app.client.view.coupon;

import java.util.Arrays;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.View;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.CouponTransactionDTO;

public class CouponTransactionView extends AbstractView implements View<CouponTransactionView.CouponTransactionPresenter> {
	private static final int pageSize = 10;
	private static final ColumnDefinition [] transactionTableColumnDefinitions = new ColumnDefinition [] {
		ColumnDefinition.DESCRIPTION,
		ColumnDefinition.PRICE,
		ColumnDefinition.DISCOUNT,
		ColumnDefinition.STATUS,
		ColumnDefinition.TIME_PURCHASED,
		ColumnDefinition.VIEW_COUPON,
		ColumnDefinition.PRINT_COUPON
	};
	
	public static interface CouponTransactionPresenter extends Presenter {
		void getCouponTransactions(int start, int pageSize);
		
		void getCouponQRCodeUrl(Long couponTransactionId);

		void printCoupon(Long couponId);
	}
	
	private static CouponTransactionViewUiBinder uiBinder = GWT
	    .create(CouponTransactionViewUiBinder.class);

	interface CouponTransactionViewUiBinder extends UiBinder<Widget, CouponTransactionView> {
	}

	@UiField
	HTMLPanel transactionPanel;
	@UiField
	Alert message;
	CouponTransactionTableWidget transactionWidget;
	
	private int start;
	private CouponTransactionPresenter presenter;
	
	public CouponTransactionView(EventBus eventBus) {
		super(eventBus);
		transactionWidget = new CouponTransactionTableWidget(Arrays.asList(transactionTableColumnDefinitions), pageSize, basicDataFormatter);
		initWidget(uiBinder.createAndBindUi(this));
		transactionPanel.add(transactionWidget);
		setupEventHandler();
		StyleHelper.show(message.getElement(), false);
	}

	private void setupEventHandler() {
		transactionWidget.getCouponTransactionTable().addRangeChangeHandler(new RangeChangeEvent.Handler() {

			@Override
      public void onRangeChange(RangeChangeEvent event) {
				start = event.getNewRange().getStart();
				loadCouponTransaction();
      }

		});
		
		Column<CouponTransactionDTO, String> viewCouponColumn = getColumnIndex(ColumnDefinition.VIEW_COUPON); 
		viewCouponColumn.setFieldUpdater(new FieldUpdater<CouponTransactionDTO, String>() {

			@Override
      public void update(int index, CouponTransactionDTO object, String value) {
				presenter.getCouponQRCodeUrl(object.getTransactionId());
      }
			
		});
		
    Column<CouponTransactionDTO, String> printCouponColumn = getColumnIndex(ColumnDefinition.PRINT_COUPON); 
		printCouponColumn.setFieldUpdater(new FieldUpdater<CouponTransactionDTO, String>() {

			@Override
      public void update(int index, CouponTransactionDTO object, String value) {
				presenter.printCoupon(object.getTransactionId());
      }
			
		});
  }

	public void setRowCount(Long couponTransactionCount) {
		transactionWidget.setRowCount(couponTransactionCount.intValue());
	}
	
	public void displayCouponTransactions(List<CouponTransactionDTO> transactions) {
		transactionWidget.displayCouponTransactions(transactions);
	}
	
	@Override
  public void setPresenter(CouponTransactionPresenter presenter) {
		this.presenter = presenter;
  }
	
	@Override
  public void clear() {
		transactionWidget.clearTable();
  }
	
	public void loadCouponTransaction() {
		presenter.getCouponTransactions(start, pageSize);
  }
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}
	
  private <T> Column<CouponTransactionDTO, T> getColumnIndex(ColumnDefinition colDefinition) {
  	int index = 0;
  	for(ColumnDefinition colDef : transactionTableColumnDefinitions) {
  		if (colDef == colDefinition) {
  			return (Column<CouponTransactionDTO, T>) transactionWidget.getCouponTransactionTable().getColumn(index);
  		}
  		index++;
  	}
  	
  	return null;
	}
}
