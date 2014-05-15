package com.ziplly.app.client.view.coupon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
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
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.googlecode.gwt.charts.client.ColumnType;
import com.ziplly.app.client.activities.CouponReportActivity.TransactionSummary;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.chart.ChartColumn;
import com.ziplly.app.client.widget.chart.DataTableAdapter;
import com.ziplly.app.client.widget.chart.PieChartWidget;
import com.ziplly.app.client.widget.chart.Value;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.PurchasedCouponDTO;
import com.ziplly.app.model.PurchasedCouponStatus;
import com.ziplly.app.shared.GetCouponTransactionResult;

public class CouponReportSalesView extends AbstractView {

	private static CouponReportSalesViewUiBinder uiBinder = GWT
	    .create(CouponReportSalesViewUiBinder.class);

	interface CouponReportSalesViewUiBinder extends UiBinder<Widget, CouponReportSalesView> {
	}

	private static ColumnDefinition[] columnDefinitions = new ColumnDefinition[] {
	    ColumnDefinition.DESCRIPTION, ColumnDefinition.PRICE, ColumnDefinition.DISCOUNT,
	    ColumnDefinition.STATUS, ColumnDefinition.TIME_PURCHASED, };

	private List<ChartColumn> chartColumDefinitions = new ArrayList<ChartColumn>();

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
	@UiField(provided = true)
	CouponTransactionTableWidget couponTransactionTableWidget;
	@UiField
	Button refreshBtn;
	@UiField
	Label totalCouponsSoldLabel;
	@UiField
	Label totalCouponsRedeemed;
	@UiField
	Label totalCouponsUnused;
	@UiField
	HTMLPanel chartsPanel;

	private static final int couponDataPageSize = 10;

	private static final String COUPON_SALES_TITLE = "Coupon Sales";

	// private int couponDataStart;

	@Inject
	public CouponReportSalesView(EventBus eventBus) {
		super(eventBus);
		pager = new SimplePager();
		couponReportTable = new CellTable<CouponDTO>();
		couponReportTable.setHover(true);
		pager.setDisplay(couponReportTable);
		pager.setPageSize(couponDataPageSize);
		buildChartColumDefinitions();
		buildTable();
		createCouponTransactionTable();
		initWidget(uiBinder.createAndBindUi(this));
		StyleHelper.show(message.getElement(), false);
		displayCouponTransactionDetailsPanel(false);
	}

	private void buildChartColumDefinitions() {
		chartColumDefinitions.add(new ChartColumn("Usage", ColumnType.STRING));
		chartColumDefinitions.add(new ChartColumn("Count", ColumnType.NUMBER));
	}

	private void buildChart(GetCouponTransactionResult result) {
		if (result.getPurchasedCoupons().size() > 0) {
			DataTableAdapter<String, Integer> pieChartAdapter = createDataTableAdapter(result);
			chartsPanel.clear();
			PieChartWidget chartWidget =
			    new PieChartWidget(chartsPanel, pieChartAdapter, COUPON_SALES_TITLE);
		}
	}

	private DataTableAdapter<String, Integer>
	    createDataTableAdapter(final GetCouponTransactionResult result) {
		DataTableAdapter<String, Integer> adapter = new DataTableAdapter<String, Integer>() {

			@Override
			public List<ChartColumn> getColumns() {
				return chartColumDefinitions;
			}

			@Override
			public Map<ChartColumn, Value<Integer>> getValueMap() {
				Map<ChartColumn, Value<Integer>> valueMap = new HashMap<ChartColumn, Value<Integer>>();
				List<PurchasedCouponDTO> purchasedCoupons = result.getPurchasedCoupons();
				valueMap.put(chartColumDefinitions.get(0), new Value<Integer>("Used label", 10));
				// getCouponCount(purchasedCoupons, PurchasedCouponStatus.USED)));
				valueMap.put(chartColumDefinitions.get(1), new Value<Integer>(
				    "Unused label",
				    getCouponCount(purchasedCoupons, PurchasedCouponStatus.UNUSED)));
				return valueMap;
			}
		};

		return adapter;
	}

	private int getCouponCount(List<PurchasedCouponDTO> coupons, PurchasedCouponStatus status) {
		int total = 0;
		for (PurchasedCouponDTO pc : coupons) {
			total += pc.getStatus() == status ? 1 : 0;
		}
		return total;
	}

	// private void setupEventHandler() {
	// couponReportTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
	//
	// @Override
	// public void onRangeChange(RangeChangeEvent event) {
	// couponDataStart = event.getNewRange().getStart();
	// loadCouponData();
	// }
	//
	// });
	// }

	public void setRowCount(Long couponCount) {
		couponReportTable.setRowCount(couponCount.intValue(), true);
	}

	public void displayCoupons(int start, List<CouponDTO> coupons) {
		StyleHelper.show(message.getElement(), false);

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
	}

	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}

	public void setTotalCouponCount(Long totalCouponCount) {
		couponReportTable.setRowCount(totalCouponCount.intValue(), true);
	}

	public void displayCouponDetails(GetCouponTransactionResult result) {
		couponTransactionTableWidget.displayPurchasedCoupons(result.getPurchasedCoupons());
		couponTransactionTableWidget.setRowCount(result.getTotalTransactions().intValue());
		displayCouponTransactionDetailsPanel(true);
		buildChart(result);
	}

	public void displaySummary(TransactionSummary summary) {
		totalCouponsSoldLabel.setText(summary.getTotalCouponsSold().toString());
		totalSalesLabel.setText(basicDataFormatter.format(summary.getTotalSales(), ValueType.PRICE));
		totalCouponsRedeemed.setText(summary.getTotalCouponsRedeemed().toString());
		totalCouponsUnused.setText(summary.getTotalCouponsUnused().toString());
	}

	public CellTable<CouponDTO> getCouponReportTable() {
		return couponReportTable;
	}

	public Button getRefreshButton() {
		return refreshBtn;
	}

	private void createCouponTransactionTable() {
		couponTransactionTableWidget =
		    new CouponTransactionTableWidget(
		        Arrays.asList(columnDefinitions),
		        couponDataPageSize,
		        basicDataFormatter);

		SelectionModel<PurchasedCouponDTO> noSelectionModel =
		    new NoSelectionModel<PurchasedCouponDTO>();
		final CellTable<PurchasedCouponDTO> table = couponTransactionTableWidget.getTable();
		table.setSelectionModel(noSelectionModel);
	}

	private void displayCouponTransactionDetailsPanel(boolean b) {
		StyleHelper.show(couponTransactionDetailsPanel.getElement(), b);
	}
}
