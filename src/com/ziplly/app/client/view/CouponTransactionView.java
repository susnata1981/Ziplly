package com.ziplly.app.client.view;

import java.util.List;

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TooltipCellDecorator;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.CouponTransactionDTO;

public class CouponTransactionView extends AbstractView implements View<CouponTransactionView.CouponTransactionPresenter> {
	private static final int pageSize = 5;
	
	public static interface CouponTransactionPresenter extends Presenter {
		void getCouponTransactions(int start, int pageSize);
		
		void getCouponQRCodeUrl(Long couponTransactionId);
	}
	
	private static CouponTransactionViewUiBinder uiBinder = GWT
	    .create(CouponTransactionViewUiBinder.class);

	interface CouponTransactionViewUiBinder extends UiBinder<Widget, CouponTransactionView> {
	}

	@UiField(provided = true)
	SimplePager pager;
	@UiField(provided = true)
	CellTable<CouponTransactionDTO> couponTransactionTable;
	
	private int start;
	private CouponTransactionPresenter presenter;
	
	public CouponTransactionView(EventBus eventBus) {
		super(eventBus);
		pager = new SimplePager();
		couponTransactionTable = new CellTable<CouponTransactionDTO>();
		pager.setDisplay(couponTransactionTable);
		pager.setPageSize(pageSize);
		buildTable();
		initWidget(uiBinder.createAndBindUi(this));
		setupEventHandler();
	}

	private void setupEventHandler() {
		couponTransactionTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {

			@Override
      public void onRangeChange(RangeChangeEvent event) {
				start = event.getNewRange().getStart();
				loadCouponTransaction();
      }

		});
  }

	public void setRowCount(Long couponTransactionCount) {
		couponTransactionTable.setRowCount(couponTransactionCount.intValue(), true);
	}
	
	public void displayCouponTransactions(List<CouponTransactionDTO> transactions) {
		couponTransactionTable.setRowData(start, transactions);
	}
	
	private void buildTable() {
		Column<CouponTransactionDTO, String> couponDescription = new TextColumn<CouponTransactionDTO>() {

			@Override
      public String getValue(CouponTransactionDTO c) {
				return c.getCoupon().getDescription();
      }
		};
		couponTransactionTable.addColumn(couponDescription, "Description");
		
		Column<CouponTransactionDTO, Number> couponPrice = new Column<CouponTransactionDTO, Number>(new NumberCell()) {

			@Override
      public Number getValue(CouponTransactionDTO c) {
				return c.getCoupon().getPrice().doubleValue();
      }
		};
		couponTransactionTable.addColumn(couponPrice, "Price");
		
		Column<CouponTransactionDTO, Number> discount = new Column<CouponTransactionDTO, Number>(new NumberCell()) {

			@Override
      public Number getValue(CouponTransactionDTO c) {
				return c.getCoupon().getDiscount().doubleValue();
      }
		};
		couponTransactionTable.addColumn(discount, "Discount");
		
		Column<CouponTransactionDTO, String> status = new TextColumn<CouponTransactionDTO>() {

			@Override
      public String getValue(CouponTransactionDTO c) {
				return c.getPurchasedCoupon().getStatus().name();
      }
		};
		couponTransactionTable.addColumn(status, "Status");
		
		Column<CouponTransactionDTO, String> timePurchased = new TextColumn<CouponTransactionDTO>() {

			@Override
      public String getValue(CouponTransactionDTO c) {
				return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE);
      }
		};
		couponTransactionTable.addColumn(timePurchased, "Time Purchased");
		
		ButtonCell viewButtonCell = new ButtonCell(IconType.BARCODE);
		final TooltipCellDecorator<String> decorator = new TooltipCellDecorator<String>(viewButtonCell);
		decorator.setText("View coupon");
		Column<CouponTransactionDTO, String> buttonCol = new Column<CouponTransactionDTO, String>(decorator) {

			@Override
      public String getValue(CouponTransactionDTO object) {
				return "view";
      }
		};
		buttonCol.setFieldUpdater(new FieldUpdater<CouponTransactionDTO, String>() {

			@Override
      public void update(int index, CouponTransactionDTO object, String value) {
				Window.alert("txn id="+object.getTransactionId());
				presenter.getCouponQRCodeUrl(object.getTransactionId());
      }
			
		});
		couponTransactionTable.addColumn(buttonCol);
  }
	
	@Override
  public void setPresenter(CouponTransactionPresenter presenter) {
		this.presenter = presenter;
  }
	
	@Override
  public void clear() {
		couponTransactionTable.setRowCount(0);
  }
	
	public void loadCouponTransaction() {
		presenter.getCouponTransactions(start, pageSize);
  }
}
