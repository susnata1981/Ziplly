package com.ziplly.app.client.view.coupon;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.chart.ChartType;
import com.ziplly.app.client.widget.chart.LineChartWidget;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class CouponReportSalesView extends AbstractView {

	private static CouponReportSalesViewUiBinder uiBinder = GWT
	    .create(CouponReportSalesViewUiBinder.class);

	interface CouponReportSalesViewUiBinder extends UiBinder<Widget, CouponReportSalesView> {
	}

	public static interface CouponReportPresenter extends Presenter {
		void loadCouponData(int couponDataStart, int coupondatapagesize);

		void loadCouponDetails(CouponDTO coupon, int start, int pageSize);
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
	@UiField
	Button refreshBtn;
	@UiField
	Label totalCouponsSoldLabel;
	@UiField
	Label totalCouponsRedeemed;
	@UiField
	Label totalCouponsUnused;
	@UiField
	Label totalFee;
	@UiField
	HTMLPanel chartsPanel;
	@UiField
	ListBox chartTypeListBox;
	
  private GetCouponTransactionResult couponTransactions;
	private static final int couponDataPageSize = 10;

	@Inject
	public CouponReportSalesView(EventBus eventBus) {
		super(eventBus);
		pager = new SimplePager();
		couponReportTable = new CellTable<CouponDTO>();
		couponReportTable.setHover(true);
		pager.setDisplay(couponReportTable);
		pager.setPageSize(couponDataPageSize);
		buildTable();
		initWidget(uiBinder.createAndBindUi(this));
		setupUi();
		setupHandlers();
	}

	private void setupUi() {
	  populateChartTypeListBox();
	  message.setVisible(false);
    displayCouponTransactionDetailsPanel(false);
  }

  private void setupHandlers() {
	  chartTypeListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        int index = chartTypeListBox.getSelectedIndex();
        displayChart(ChartType.values()[index]);
      }
	  });
  }

	public void setRowCount(Long couponCount) {
		couponReportTable.setRowCount(couponCount.intValue(), true);
	}

	public void displayCoupons(int start, List<CouponDTO> coupons) {
		message.setVisible(false);

		if (coupons == null) {
			return;
		}

		if (coupons.isEmpty()) {
			displayMessage(StringConstants.NO_COUPONS, AlertType.WARNING);
			return;
		}

		couponReportTable.setRowData(start, coupons);
	}

	private void buildTable() {
		Column<CouponDTO, String> couponDescription = new TextColumn<CouponDTO>() {

			@Override
			public String getValue(CouponDTO c) {
				return c.getDescription();
			}
		};
		couponReportTable.addColumn(couponDescription, "Description");

		Column<CouponDTO, String> couponPrice = new TextColumn<CouponDTO>() {

			@Override
			public String getValue(CouponDTO c) {
				return  basicDataFormatter.format(c.getItemPrice(), ValueType.PRICE);
			}
		};
		
		couponReportTable.addColumn(couponPrice, "Price");

		Column<CouponDTO, String> discount = new TextColumn<CouponDTO>() {

			@Override
			public String getValue(CouponDTO c) {
				return basicDataFormatter.format(c.getItemPrice().subtract(c.getDiscountedPrice()), ValueType.PRICE);
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
				return basicDataFormatter.format(c.getStartDate(), ValueType.DATE_VALUE_FULL);
			}
		};
		couponReportTable.addColumn(startTime, "Start time");

		Column<CouponDTO, String> endTime = new TextColumn<CouponDTO>() {

			@Override
			public String getValue(CouponDTO c) {
				return basicDataFormatter.format(c.getEndDate(), ValueType.DATE_VALUE_FULL);
			}
		};
		couponReportTable.addColumn(endTime, "Expiration time");

		Column<CouponDTO, String> creationTime = new TextColumn<CouponDTO>() {

			@Override
			public String getValue(CouponDTO c) {
				return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE_FULL);
			}
		};
		couponReportTable.addColumn(creationTime, "Creation time");

		setupSelectionHandler();
	}

	public NoSelectionModel<CouponDTO> getCouponTableSelectionStrategy() {
		return couponTableSelectionModel;
	}

	private void setupSelectionHandler() {
		couponReportTable.setSelectionModel(couponTableSelectionModel);
	}

	public void setCouponDetailsTableHeading(CouponDTO coupon) {
		couponTransactionDetailsHeading.setText("Displaying details for coupon: \""
		    + coupon.getDescription() + "\"");
	}

	public void clear() {
		couponReportTable.setRowCount(0);
		displayCouponTransactionDetailsPanel(false);
		message.setText("");
		StyleHelper.show(message.getElement(), false);
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		message.setVisible(true);
	}

	public void setTotalCouponCount(Long totalCouponCount) {
		couponReportTable.setRowCount(totalCouponCount.intValue(), true);
	}

	public void displayCouponDetails(GetCouponTransactionResult result) {
	  this.couponTransactions = result;
		displayCouponTransactionDetailsPanel(true);
		chartTypeListBox.setSelectedIndex(0);
		displayChart(ChartType.values()[0]);
	}

	public void displaySummary(TransactionSummary summary) {
		totalCouponsSoldLabel.setText(summary.getTotalCouponsSold().toString());
		totalSalesLabel.setText(basicDataFormatter.format(summary.getTotalSalesAmount(), ValueType.PRICE));
		totalCouponsRedeemed.setText(summary.getTotalCouponsRedeemed().toString());
		totalCouponsUnused.setText(summary.getTotalCouponsUnused().toString());
		totalFee.setText(basicDataFormatter.format(summary.getTotalFees(), ValueType.PRICE));
	}

	public CellTable<CouponDTO> getCouponReportTable() {
		return couponReportTable;
	}

	public Button getRefreshButton() {
		return refreshBtn;
	}

  private void displayChart(ChartType chartType) {
    chartsPanel.clear();
    
//    for (int i = 0; i < 20; i++) {
//      Date now = new Date(5, i, 2014);
//      PurchasedCouponDTO pr1 = new PurchasedCouponDTO();
//      TransactionDTO tr1 = new TransactionDTO();
//      tr1.setStatus(TransactionStatus.ACTIVE);
//      tr1.setAmount(new BigDecimal(100*Math.random()));
//      tr1.setTimeCreated(now);
//      pr1.setTransaction(tr1);
//      couponTransactions.getPurchasedCoupons().add(pr1);
//    }
      
    LineChartWidget lineChartWidget = new LineChartWidget(
      chartType.getAbstractLineChartBuilder().getAdapter(couponTransactions),
      chartType.getTitle(),
      chartType.getXAxisTitle(),
      chartType.getYAxisTitle());
    chartsPanel.add(lineChartWidget);
  }
  
	private void populateChartTypeListBox() {
	  for(ChartType type : ChartType.values()) {
	    chartTypeListBox.addItem(type.getTitle());
	  }
	}
	
	private void displayCouponTransactionDetailsPanel(boolean b) {
		StyleHelper.show(couponTransactionDetailsPanel.getElement(), b);
	}
}
