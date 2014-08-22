package com.ziplly.app.client.view.coupon;

import java.util.Arrays;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.account.CouponTransactionViewPresenter;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.CouponItemDTO;

public class CouponTransactionView extends AbstractView implements ICouponTransactionView { //implements View<CouponTransactionViewPresenter> {
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
	private CouponTransactionViewPresenter presenter;
	
	@Inject
	public CouponTransactionView(EventBus eventBus) {
		super(eventBus);
		transactionWidget = new CouponTransactionTableWidget(
		    Arrays.asList(transactionTableColumnDefinitions), pageSize);
		initWidget(uiBinder.createAndBindUi(this));
		transactionPanel.add(transactionWidget);
		setupEventHandler();
		StyleHelper.show(message.getElement(), false);
	}

	private void setupEventHandler() {
		transactionWidget.getTable().addRangeChangeHandler(new RangeChangeEvent.Handler() {

			@Override
      public void onRangeChange(RangeChangeEvent event) {
				start = event.getNewRange().getStart();
				loadCouponTransaction();
      }

		});
		
		Column<CouponItemDTO, String> viewCouponColumn = getColumnIndex(ColumnDefinition.VIEW_COUPON); 
		viewCouponColumn.setFieldUpdater(new FieldUpdater<CouponItemDTO, String>() {

			@Override
      public void update(int index, CouponItemDTO object, String value) {
				presenter.getCouponQRCodeUrl(object.getOrderDetails().getOrder().getId(), object.getCoupon().getCouponId());
      }
			
		});
		
    Column<CouponItemDTO, String> printCouponColumn = getColumnIndex(ColumnDefinition.PRINT_COUPON); 
		printCouponColumn.setFieldUpdater(new FieldUpdater<CouponItemDTO, String>() {

			@Override
      public void update(int index, CouponItemDTO object, String value) {
				presenter.printCoupon(object.getOrderDetails().getOrder().getId(), object.getCoupon().getCouponId());
      }
			
		});
  }

	public void setCouponTransactionCount(Long couponTransactionCount) {
		transactionWidget.setRowCount(couponTransactionCount.intValue());
	}
	
	public void displayPurchasedCoupons(List<CouponItemDTO> transactions) {
		transactionWidget.displayPurchasedCoupons(transactions);
	}
	
//	@Override
//  public void setPresenter(CouponTransactionViewPresenter presenter) {
//		this.presenter = presenter;
//  }
//	
//	@Override
//  public void clear() {
//		transactionWidget.clearTable();
//  }
	
	public void loadCouponTransaction() {
		presenter.getPurchasedCoupons(start, pageSize);
  }
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}
	
  @SuppressWarnings("unchecked")
  private <T> Column<CouponItemDTO, T> getColumnIndex(ColumnDefinition colDefinition) {
  	int index = 0;
  	for(ColumnDefinition colDef : transactionTableColumnDefinitions) {
  		if (colDef == colDefinition) {
  			return (Column<CouponItemDTO, T>) transactionWidget.getTable().getColumn(index);
  		}
  		
  		index++;
  	}
  	
  	return null;
	}

  public void displayQrCode(String url) {
    Window.open(url, "_blank", "");
  }
  
  public void setPresenter(CouponTransactionViewPresenter presenter) {
    this.presenter = presenter;
  }
}
