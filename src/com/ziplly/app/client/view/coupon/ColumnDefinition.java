package com.ziplly.app.client.view.coupon;

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.TooltipCellDecorator;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.PurchasedCouponDTO;

public class ColumnDefinition implements Comparable<ColumnDefinition> {
	private Column<PurchasedCouponDTO, ?> column;
	private String title;
	private final static BasicDataFormatter basicDataFormatter =
	    (BasicDataFormatter) AbstractValueFormatterFactory
	        .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);
	private final static ButtonCell viewButtonCell = new ButtonCell(IconType.BARCODE);
	private final static ButtonCell printButtonCell = new ButtonCell(IconType.BARCODE);
	private final static TooltipCellDecorator<String> viewButtonDecorator =
	    new TooltipCellDecorator<String>(viewButtonCell);
	private final static TooltipCellDecorator<String> printButtonDecorator =
	    new TooltipCellDecorator<String>(printButtonCell);

	static {
		viewButtonDecorator.setText("View coupon");
		printButtonDecorator.setText("Print coupon");
	}
	
	public static final ColumnDefinition DESCRIPTION = new ColumnDefinition(
	    "Description",
	    new TextColumn<PurchasedCouponDTO>() {

		    @Override
		    public String getValue(PurchasedCouponDTO pr) {
			    return pr.getCoupon().getDescription();
		    }
	    });

	public static final ColumnDefinition PRICE = new ColumnDefinition(
	    "Price",
	    new Column<PurchasedCouponDTO, Number>(new NumberCell()) {
		    ;

		    @Override
		    public Number getValue(PurchasedCouponDTO c) {
			    return c.getCoupon().getPrice().doubleValue();
		    }
	    });

	public static final ColumnDefinition DISCOUNT = new ColumnDefinition(
	    "Discount",
	    new Column<PurchasedCouponDTO, Number>(new NumberCell()) {

		    @Override
		    public Number getValue(PurchasedCouponDTO c) {
			    return c.getCoupon().getDiscount().doubleValue();
		    }
	    });
	public static final ColumnDefinition STATUS = new ColumnDefinition(
	    "Status",
	    new TextColumn<PurchasedCouponDTO>() {

		    @Override
		    public String getValue(PurchasedCouponDTO c) {
			    return c.getStatus().name();
		    }
	    });
	public static final ColumnDefinition TIME_PURCHASED = new ColumnDefinition(
	    "Time purchased",
	    new TextColumn<PurchasedCouponDTO>() {
		    @Override
		    public String getValue(PurchasedCouponDTO c) {
			    return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE);
		    }
	    });
	public static final ColumnDefinition VIEW_COUPON = new ColumnDefinition(
	    "View",
	    new Column<PurchasedCouponDTO, String>(viewButtonDecorator) {

		    @Override
		    public String getValue(PurchasedCouponDTO object) {
			    return "View";
		    }
	    });
	public static final ColumnDefinition PRINT_COUPON = new ColumnDefinition(
	    "Print",
	    new Column<PurchasedCouponDTO, String>(printButtonDecorator) {

		    @Override
		    public String getValue(PurchasedCouponDTO object) {
			    return "Print";
		    }
	    });
	
	private ColumnDefinition(String title, Column<PurchasedCouponDTO, ?> column) {
		this.title = title;
		this.column = column;
	}

	public Column<PurchasedCouponDTO, ?> getColumn() {
		return column;
	}

	public String getTitle() {
		return title;
	}

	@Override
  public int compareTo(ColumnDefinition o) {
		if (o == null) {
			return -1;
		}
		
		return title.compareTo(o.title);
  }
}

// private void buildTable() {
// Column<CouponTransactionDTO, String> couponDescription = new
// TextColumn<CouponTransactionDTO>() {
//
// @Override
// public String getValue(CouponTransactionDTO c) {
// return c.getCoupon().getDescription();
// }
// };
// transactionWidget.addColumn(couponDescription, "Description");
//
// Column<CouponTransactionDTO, Number> couponPrice = new
// Column<CouponTransactionDTO, Number>(new NumberCell()) {
//
// @Override
// public Number getValue(CouponTransactionDTO c) {
// return c.getCoupon().getPrice().doubleValue();
// }
// };
// transactionWidget.addColumn(couponPrice, "Price");
//
// Column<CouponTransactionDTO, Number> discount = new
// Column<CouponTransactionDTO, Number>(new NumberCell()) {
//
// @Override
// public Number getValue(CouponTransactionDTO c) {
// return c.getCoupon().getDiscount().doubleValue();
// }
// };
// transactionWidget.addColumn(discount, "Discount");
//
// Column<CouponTransactionDTO, String> status = new
// TextColumn<CouponTransactionDTO>() {
//
// @Override
// public String getValue(CouponTransactionDTO c) {
// return c.getPurchasedCoupon().getStatus().name();
// }
// };
// transactionWidget.addColumn(status, "Status");
//
// Column<CouponTransactionDTO, String> timePurchased = new
// TextColumn<CouponTransactionDTO>() {
//
// @Override
// public String getValue(CouponTransactionDTO c) {
// return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE);
// }
// };
// transactionWidget.addColumn(timePurchased, "Time Purchased");
//
// ButtonCell viewButtonCell = new ButtonCell(IconType.BARCODE);
// final TooltipCellDecorator<String> decorator = new
// TooltipCellDecorator<String>(viewButtonCell);
// decorator.setText("View coupon");
// Column<CouponTransactionDTO, String> buttonCol = new
// Column<CouponTransactionDTO, String>(decorator) {
//
// @Override
// public String getValue(CouponTransactionDTO object) {
// return "view";
// }
// };
// buttonCol.setFieldUpdater(new FieldUpdater<CouponTransactionDTO, String>() {
//
// @Override
// public void update(int index, CouponTransactionDTO object, String value) {
// presenter.getCouponQRCodeUrl(object.getTransactionId());
// }
//
// });
// transactionWidget.addColumn(buttonCol);
//
// ButtonCell printButtonCell = new ButtonCell(IconType.BARCODE);
// final TooltipCellDecorator<String> printCouponDecorator = new
// TooltipCellDecorator<String>(printButtonCell);
// decorator.setText("Print coupon");
// Column<CouponTransactionDTO, String> printbuttonCol = new
// Column<CouponTransactionDTO, String>(printCouponDecorator) {
//
// @Override
// public String getValue(CouponTransactionDTO object) {
// return "print";
// }
// };
// printbuttonCol.setFieldUpdater(new FieldUpdater<CouponTransactionDTO,
// String>() {
//
// @Override
// public void update(int index, CouponTransactionDTO object, String value) {
// presenter.printCoupon(object.getTransactionId());
// }
//
// });
// transactionWidget.addColumn(printbuttonCol);
// }