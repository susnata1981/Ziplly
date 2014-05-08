package com.ziplly.app.client.view.coupon;

import java.util.Arrays;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.CouponReportActivity.CouponReportView;
import com.ziplly.app.client.activities.CouponReportActivity.TransactionSummary;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponTransactionDTO;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class CouponReportViewImpl extends AbstractView implements CouponReportView {
	private static ColumnDefinition [] columnDefinitions = new ColumnDefinition [] {
		ColumnDefinition.DESCRIPTION,
		ColumnDefinition.PRICE,
		ColumnDefinition.DISCOUNT,
		ColumnDefinition.STATUS,
		ColumnDefinition.TIME_PURCHASED,
	};
	
	public static interface CouponReportPresenter extends Presenter {
		void loadCouponData(int couponDataStart, int coupondatapagesize);

		void loadCouponDetails(CouponDTO coupon, int start, int pageSize);
	}
	
	private static CouponReportViewUiBinder uiBinder = GWT.create(CouponReportViewUiBinder.class);

	interface CouponReportViewUiBinder extends UiBinder<Widget, CouponReportViewImpl> {
	}

	@UiField
	Alert message;
	@UiField(provided = true)
	SimplePager pager;
	@UiField(provided = true)
	CellTable<CouponDTO> couponReportTable;
	final NoSelectionModel<CouponDTO> couponTableSelectionModel = new NoSelectionModel<CouponDTO>();
	
	@UiField
	Label totalSalesLabel;
	
	@UiField
	Heading couponTransactionDetailsHeading;
	@UiField
	HTMLPanel couponTransactionDetailsPanel;
	@UiField(provided = true)
	CouponTransactionTableWidget couponTransactionTableWidget;
	
	private static final int couponDataPageSize = 10;
	private int couponDataStart;
	private CouponReportPresenter presenter;
	
	@Inject
	public CouponReportViewImpl(EventBus eventBus) {
		super(eventBus);
		pager = new SimplePager();
		couponReportTable = new CellTable<CouponDTO>();
		couponReportTable.setHover(true);
		pager.setDisplay(couponReportTable);
		pager.setPageSize(couponDataPageSize);
		buildTable();
		createCouponTransactionTable();
		initWidget(uiBinder.createAndBindUi(this));
		
		setupEventHandler();
		StyleHelper.show(message.getElement(), false);
		displayCouponTransactionDetailsPanel(false);
	}

	private void setupEventHandler() {
		couponReportTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {

			@Override
      public void onRangeChange(RangeChangeEvent event) {
				couponDataStart = event.getNewRange().getStart();
				loadCouponData();
      }

		});
  }

	public void setRowCount(Long couponCount) {
		couponReportTable.setRowCount(couponCount.intValue(), true);
	}
	
	@Override
	public void displayCoupons(List<CouponDTO> coupons) {
		StyleHelper.show(message.getElement(), false);
		
		if (coupons == null) {
			return;
		}
		
		if (coupons.isEmpty()) {
			displayMessage(StringConstants.NO_COUPONS, AlertType.WARNING);
			return;
		}
		
		couponReportTable.setRowData(couponDataStart, coupons);
	}
	
	private void buildTable() {
		Column<CouponDTO, String> couponDescription = new TextColumn<CouponDTO>() {

			@Override
      public String getValue(CouponDTO c) {
				return c.getDescription();
      }
		};
		couponReportTable.addColumn(couponDescription, "Description");
		
		Column<CouponDTO, Number> couponPrice = new Column<CouponDTO, Number>(new NumberCell()) {

			@Override
      public Number getValue(CouponDTO c) {
				return c.getPrice().doubleValue();
      }
		};
		couponReportTable.addColumn(couponPrice, "Price");
		
		Column<CouponDTO, Number> discount = new Column<CouponDTO, Number>(new NumberCell()) {

			@Override
      public Number getValue(CouponDTO c) {
				return c.getDiscount().doubleValue();
      }
		};
		couponReportTable.addColumn(discount, "Discount");
		
		Column<CouponDTO, String> status = new TextColumn<CouponDTO>() {

			@Override
      public String getValue(CouponDTO c) {
				return "Active";
      }
		};
		couponReportTable.addColumn(status, "Status");
		
		Column<CouponDTO, String> startTime = new TextColumn<CouponDTO>() {

			@Override
      public String getValue(CouponDTO c) {
				return basicDataFormatter.format(c.getStartDate(), ValueType.DATE_VALUE);
      }
		};
		couponReportTable.addColumn(startTime, "Start time");
		
		Column<CouponDTO, String> endTime = new TextColumn<CouponDTO>() {

			@Override
      public String getValue(CouponDTO c) {
				return basicDataFormatter.format(c.getEndDate(), ValueType.DATE_VALUE);
      }
		};
		couponReportTable.addColumn(endTime, "Expiration time");
		
		Column<CouponDTO, String> creationTime = new TextColumn<CouponDTO>() {

			@Override
      public String getValue(CouponDTO c) {
				return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE);
      }
		};
		couponReportTable.addColumn(creationTime, "Creation time");
		
		setupSelectionHandler();
  }

	private void setupSelectionHandler() {
		couponTableSelectionModel.addSelectionChangeHandler(new Handler() {

			@Override
      public void onSelectionChange(SelectionChangeEvent event) {
				internalDisplayCouponDetails(couponTableSelectionModel.getLastSelectedObject());
      }
		});
		couponReportTable.setSelectionModel(couponTableSelectionModel);
	}

	private void internalDisplayCouponDetails(CouponDTO coupon) {
		couponTransactionDetailsHeading.setText("Displaying details for coupon: \""+coupon.getDescription()+"\"");
		presenter.loadCouponDetails(coupon, 0, couponDataPageSize);
  }

	@Override
  public void clear() {
		couponReportTable.setRowCount(0);
  }
	
	public void loadCouponData() {
		presenter.loadCouponData(couponDataStart, couponDataPageSize);
  }
	
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}

	@Override
  public int getCouponDataStartIndex() {
		return couponDataStart;
  }

	@Override
  public int getCouponDataPageSize() {
		return couponDataPageSize;
  }

	@Override
  public void setPresenter(CouponReportPresenter presenter) {
		this.presenter = presenter;
  }

	@Override
  public void setTotalCouponCount(Long totalCouponCount) {
		couponReportTable.setRowCount(totalCouponCount.intValue(), true);
  }

	@Override
  public void displayCouponDetails(GetCouponTransactionResult result) {
		couponTransactionTableWidget.displayCouponTransactions(result.getTransactions());
		couponTransactionTableWidget.setRowCount(result.getTotalTransactions().intValue());
		displayCouponTransactionDetailsPanel(true);
  }

	@UiField
	Label totalCouponsSoldLabel;
	@UiField
	Label totalCouponsRedeemed;
	@UiField
	Label totalCouponsUnused;
	
	@Override
  public void displaySummary(TransactionSummary summary) {
		totalCouponsSoldLabel.setText(summary.getTotalCouponsSold().toString());
		totalSalesLabel.setText(basicDataFormatter.format(summary.getTotalSales(), ValueType.PRICE));
		totalCouponsRedeemed.setText(summary.getTotalCouponsRedeemed().toString());
		totalCouponsUnused.setText(summary.getTotalCouponsUnused().toString());
  }
	
	private void createCouponTransactionTable() {
		couponTransactionTableWidget = new CouponTransactionTableWidget(
				Arrays.asList(columnDefinitions), 
				couponDataPageSize, 
				basicDataFormatter);
		
		SelectionModel<CouponTransactionDTO> noSelectionModel = new NoSelectionModel<CouponTransactionDTO>();
		final CellTable<CouponTransactionDTO> table = couponTransactionTableWidget.getCouponTransactionTable();
		table.setSelectionModel(noSelectionModel);
		couponTransactionTableWidget.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				presenter.loadCouponDetails(couponTableSelectionModel.getLastSelectedObject(), 
						event.getNewRange().getStart(),
						couponDataPageSize);
			}
			
		});
  }
	
	private void displayCouponTransactionDetailsPanel(boolean b) {
		StyleHelper.show(couponTransactionDetailsPanel.getElement(), b);
  }
}
