package com.ziplly.app.client.view.coupon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.CouponReportActivity.CouponReportView;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.widget.ConfirmationModalWidget;
import com.ziplly.app.client.widget.MessageModal;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.CouponItemDTO;

public class CouponReportViewImpl extends AbstractView implements CouponReportView {

	public static interface CouponReportPresenter extends Presenter {
		void loadCouponData(int couponDataStart, int coupondatapagesize);

		void loadCouponDetails(CouponDTO coupon, int start, int pageSize);

		void sendTweet(CouponDTO coupon);
		
		BusinessAccountDTO getAccount();
	}

	public interface Style extends CssResource {
	  String selectedLink();
	}
	
	private static CouponReportViewUiBinder uiBinder = GWT.create(CouponReportViewUiBinder.class);

	interface CouponReportViewUiBinder extends UiBinder<Widget, CouponReportViewImpl> {
	}

	@UiField
	Alert message;

	@UiField(provided = true)
	CouponReportSalesView couponSalesView;
	CouponFormWidgetModal couponFormWidget;

	@UiField
	NavLink viewCouponLink;
	@UiField
	NavLink couponSalesLink;
	
	@UiField
	Style style;
	
	private Map<NavLink, Composite> linkToViewMap;
	private static final int couponDataPageSize = 10;
	private int couponDataStart;
	private CouponReportPresenter presenter;
	private ConfirmationModalWidget confirmationWidget;

	@Inject
	public CouponReportViewImpl(EventBus eventBus) {
		super(eventBus);
		linkToViewMap = new HashMap<NavLink, Composite>();
		couponSalesView = new CouponReportSalesView(eventBus);
		couponFormWidget = couponSalesView.getCouponFormWidget();
		ZResources.IMPL.style().ensureInjected();
		initWidget(uiBinder.createAndBindUi(this));
		setupEventHandler();
		message.setVisible(false);
		buildNavLinkMap();
		showView(couponSalesLink);
	}

	private void buildNavLinkMap() {
//		linkToViewMap.put(couponFormLink, couponFormWidget);
		linkToViewMap.put(couponSalesLink, couponSalesView);
	}

	private void setupEventHandler() {
		couponSalesView.getCouponReportTable().addRangeChangeHandler(new RangeChangeEvent.Handler() {

			@Override
			public void onRangeChange(RangeChangeEvent event) {
				couponDataStart = event.getNewRange().getStart();
				loadCouponData();
			}
		});

		couponSalesView.getCouponTableSelectionStrategy().addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				internalDisplayCouponDetails(couponSalesView
				    .getCouponTableSelectionStrategy()
				    .getLastSelectedObject());
			}
		});

		couponSalesView.getRefreshButton().addClickHandler(new ClickHandler() {

			@Override
      public void onClick(ClickEvent event) {
				couponSalesView.clear();
				presenter.loadCouponData(0, couponDataPageSize);
      }
			
		});
		
		couponSalesView.getNewCouponButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        couponSalesView.showCreateFormWidget();
      }
		  
		});
		
		couponFormWidget.getCancelButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			}

		});

		couponFormWidget.getPreviewButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!couponFormWidget.validate()) {
					return;
				}

				couponFormWidget.showPreview();
			}
		});

		couponFormWidget.getTweetButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (confirmationWidget == null) {
					confirmationWidget =
					    new ConfirmationModalWidget(
					        StringConstants.PUBLISH_COUPON_CONFIRMATION,
					        new ConfirmationModalWidget.ConfirmationModalCallback() {

						        @Override
						        public void confirm() {
							        confirmationWidget.show(false);
							        doPublishCoupon();
							        couponFormWidget.showButtons(true);
//							        eventBus.fireEvent(new LoadingEventStart());
						        }

						        @Override
						        public void cancel() {
							        confirmationWidget.show(false);
						        }
					        });
				}
				confirmationWidget.show(true);
			}
		});
	}

	private void doPublishCoupon() {
	  CouponDTO coupon = couponFormWidget.getCoupon();
	  if (coupon == null) {
	    return;
	  }
		presenter.sendTweet(coupon);
	}

	public void setRowCount(Long couponCount) {
		couponSalesView.setRowCount(couponCount);
	}

	@Override
	public void displayCoupons(int start, List<CouponDTO> coupons) {
		couponSalesView.displayCoupons(start, coupons);
	}

	@Override
	public void clear() {
	  message.setText("");
	  message.setVisible(false);
	  couponFormWidget.clear();
		couponSalesView.getCouponReportTable().setRowCount(0);
	}

	public void loadCouponData() {
		presenter.loadCouponData(couponDataStart, couponDataPageSize);
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
    MessageModal modal = new MessageModal();
    modal.setContent(msg);
    modal.show();
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
		// Need to find a better solution, hacky.
    couponFormWidget.setAccount(presenter.getAccount());
	}

	@Override
	public void setTotalCouponCount(long totalCouponCount) {
		couponSalesView.setRowCount(totalCouponCount);
	}

	@Override
	public void displayCouponDetails(List<CouponDTO> coupons, List<CouponItemDTO> couponItems) {
		couponSalesView.displayCouponDetails(coupons, couponItems);
	}

	@Override
	public void displaySummary(TransactionSummary summary) {
		couponSalesView.displaySummary(summary);
	}

	@UiHandler("viewCouponLink")
	public void showCoupon(ClickEvent event) {
	  presenter.goTo(new HomePlace());
	}
	
	@UiHandler("couponSalesLink")
	public void showSalesForm(ClickEvent event) {
		showView(couponSalesLink);
	}

	private void showView(NavLink selectedLink) {
		for (NavLink link : linkToViewMap.keySet()) {
			if (link == selectedLink) {
				StyleHelper.show(linkToViewMap.get(link), true);
				link.addStyleName(style.selectedLink());
			} else {
				StyleHelper.show(linkToViewMap.get(link), false);
				link.removeStyleName(style.selectedLink());
			}
		}
	}
	
	private void internalDisplayCouponDetails(CouponDTO coupon) {
//		couponSalesView.setCouponDetailsTableHeading(coupon);
		presenter.loadCouponDetails(coupon, 0, couponDataPageSize);
	}

  @Override
  public CouponFormWidgetModal getCouponFormWidget() {
    return couponFormWidget;
  }
}
