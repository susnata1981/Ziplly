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
import com.ziplly.app.model.CouponItemDTO;

public class ColumnDefinition implements Comparable<ColumnDefinition> {
	private Column<CouponItemDTO, ?> column;
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
	    new TextColumn<CouponItemDTO>() {

		    @Override
		    public String getValue(CouponItemDTO pr) {
			    return pr.getCoupon().getDescription();
		    }
	    });

	public static final ColumnDefinition PRICE = new ColumnDefinition(
	    "Original Price",
	    new Column<CouponItemDTO, Number>(new NumberCell()) {
		    ;

		    @Override
		    public Number getValue(CouponItemDTO c) {
			    return c.getCoupon().getItemPrice().doubleValue();
		    }
	    });

	public static final ColumnDefinition DISCOUNT = new ColumnDefinition(
	    "Discounted Price",
	    new Column<CouponItemDTO, Number>(new NumberCell()) {

		    @Override
		    public Number getValue(CouponItemDTO c) {
			    return c.getCoupon().getDiscountedPrice().doubleValue();
		    }
	    });
	public static final ColumnDefinition STATUS = new ColumnDefinition(
	    "Status",
	    new TextColumn<CouponItemDTO>() {

		    @Override
		    public String getValue(CouponItemDTO c) {
			    return c.getStatus().name();
		    }
	    });
	public static final ColumnDefinition TIME_PURCHASED = new ColumnDefinition(
	    "Time purchased",
	    new TextColumn<CouponItemDTO>() {
		    @Override
		    public String getValue(CouponItemDTO c) {
			    return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE_FULL);
		    }
	    });
	public static final ColumnDefinition VIEW_COUPON = new ColumnDefinition(
	    "",
	    new Column<CouponItemDTO, String>(viewButtonDecorator) {

		    @Override
		    public String getValue(CouponItemDTO object) {
			    return "View Coupon";
		    }
	    });
	public static final ColumnDefinition PRINT_COUPON = new ColumnDefinition(
	    "",
	    new Column<CouponItemDTO, String>(printButtonDecorator) {

		    @Override
		    public String getValue(CouponItemDTO object) {
			    return "Print Coupon";
		    }
	    });
	
	private ColumnDefinition(String title, Column<CouponItemDTO, ?> column) {
		this.title = title;
		this.column = column;
	}

	public Column<CouponItemDTO, ?> getColumn() {
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
