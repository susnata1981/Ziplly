package com.ziplly.app.client.view.coupon;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import com.ziplly.app.model.CouponItemDTO;

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

//  @UiField
//  Heading couponTransactionDetailsHeading;
  @UiField
  HTMLPanel couponTransactionDetailsPanel;
  @UiField
  Button newCouponBtn;
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
  @UiField
  Heading chartTitle;

//  private CouponFormWidgetModal couponFormWidget;
  private CouponFormWidgetModal couponFormModal;
  
  private List<CouponItemDTO> couponItems;
  private static final int couponDataPageSize = 10;
  private List<CouponDTO> coupons;

  @Inject
  public CouponReportSalesView(EventBus eventBus) {
    super(eventBus);
//    this.couponFormWidget =  new CouponFormWidgetModal(eventBus);
    this.couponFormModal = new CouponFormWidgetModal(eventBus);
    pager = new SimplePager();
    couponReportTable = buildCouponTable();
    pager.setDisplay(couponReportTable);
    pager.setPageSize(couponDataPageSize);

    initWidget(uiBinder.createAndBindUi(this));
    setupUi();
    setupHandlers();
  }

  private CellTable<CouponDTO> buildCouponTable() {
    CouponReportTableBuilder builder = new CouponReportTableBuilder();
    return builder
        .with(CouponReportTableBuilder.COUPON_TITLE)
        .with(CouponReportTableBuilder.COUPON_DESCRIPTION)
        .with(CouponReportTableBuilder.COUPON_PRICE)
        .with(CouponReportTableBuilder.COUPON_DISCOUNT)
        .with(CouponReportTableBuilder.COUPON_STATUS)
        .with(CouponReportTableBuilder.COUPON_START_TIME)
        .with(CouponReportTableBuilder.COUPON_END_TIME)
        .setSelectionModel(couponTableSelectionModel)
        .setHover(true)
        .build();
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

  public NoSelectionModel<CouponDTO> getCouponTableSelectionStrategy() {
    return couponTableSelectionModel;
  }

  public void clear() {
    couponReportTable.setRowCount(0);
    message.setText("");
    displayCouponTransactionDetailsPanel(false);
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

  public void displayCouponDetails(List<CouponDTO> coupons, List<CouponItemDTO> couponItems) {
    this.coupons = coupons;
    this.couponItems = couponItems;
    displayCouponTransactionDetailsPanel(true);
    chartTypeListBox.setSelectedIndex(0);
    displayChart(ChartType.values()[0]);
    displayChartTitle(coupons);
  }

  public void displaySummary(TransactionSummary summary) {
    totalCouponsSoldLabel.setText(Long.toString(summary.getTotalCouponsSold()));
    totalSalesLabel.setText(basicDataFormatter.format(
        summary.getTotalSalesAmount(),
        ValueType.PRICE));
    totalCouponsRedeemed.setText(Long.toString(summary.getTotalCouponsRedeemed()));
    totalCouponsUnused.setText(Long.toString(summary.getTotalCouponsUnused()));
    totalFee.setText(basicDataFormatter.format(summary.getTotalFees(), ValueType.PRICE));
  }

  public CellTable<CouponDTO> getCouponReportTable() {
    return couponReportTable;
  }

  public Button getRefreshButton() {
    return refreshBtn;
  }

  public Button getNewCouponButton() {
    return newCouponBtn;
  }
  
  private void displayChart(ChartType chartType) {
    chartsPanel.clear();
    LineChartWidget lineChartWidget =
        new LineChartWidget(
            chartType.getAbstractLineChartBuilder().getAdapter(getCoupons(), getCouponItems()),
            chartType.getTitle(),
            chartType.getXAxisTitle(),
            chartType.getYAxisTitle());
    chartsPanel.add(lineChartWidget);
  }

  private List<CouponItemDTO> getCouponItems() {
    return Collections.unmodifiableList(couponItems);
  }

  private List<CouponDTO> getCoupons() {
    return Collections.unmodifiableList(coupons);
  }

  private void populateChartTypeListBox() {
    for (ChartType type : ChartType.values()) {
      chartTypeListBox.addItem(type.getTitle());
    }
  }

  private void displayCouponTransactionDetailsPanel(boolean b) {
    StyleHelper.show(couponTransactionDetailsPanel.getElement(), b);
  }
  
  private void displayChartTitle(List<CouponDTO> coupons) {
    int totalCoupons = coupons.size();
    if (totalCoupons > 1) {
      chartTitle.setText("All coupons");
    } else if (totalCoupons == 1){
      chartTitle.setText(coupons.get(0).getDescription());
    }
  }

  public void showCreateFormWidget() {
    couponFormModal.show(true);
  }

  public CouponFormWidgetModal getCouponFormWidget() {
    return couponFormModal;
  }

  public void loadCouponSalesData(Map<String, BigDecimal> salesAmountData) {
    chartsPanel.clear();
    ChartType chartType = ChartType.SALES_AMOUNT;
    LineChartWidget lineChartWidget =
        new LineChartWidget(
            chartType.getAbstractLineChartBuilder().getAdapter(salesAmountData),
            chartType.getTitle(),
            chartType.getXAxisTitle(),
            chartType.getYAxisTitle());
    chartsPanel.add(lineChartWidget);
  }
}
