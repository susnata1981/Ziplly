package com.ziplly.app.client.view.coupon;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.NoSelectionModel;
import com.ziplly.app.client.view.business.GenericColumnDefinition;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.CouponDTO;

public class CouponReportTableBuilder {
  private List<GenericColumnDefinition<CouponDTO, String>> columnDefinitions = 
      new ArrayList<GenericColumnDefinition<CouponDTO, String>>();
  private int pageSize;
  private NoSelectionModel<CouponDTO> selectionModel;
  private boolean hover;
  private static BasicDataFormatter basicDataFormatter = new BasicDataFormatter();

  public static final Column<CouponDTO, String> COUPON_TITLE_COLUMN =
      new TextColumn<CouponDTO>() {

        @Override
        public String getValue(CouponDTO c) {
          return c.getTitle();
        }
      };

  private static final Header<String> COUPON_TITLE_HEADER =
      new Header<String>(new TextCell()) {
        @Override
        public String getValue() {
          return "Title";
        }
      };
      
  public static final Column<CouponDTO, String> COUPON_DESCRIPTION_COLUMN =
      new TextColumn<CouponDTO>() {

        @Override
        public String getValue(CouponDTO c) {
          return c.getDescription();
        }
      };

  private static final Header<String> COUPON_DESCRIPTION_HEADER =
      new Header<String>(new TextCell()) {
        @Override
        public String getValue() {
          return "Description";
        }
      };

  public static final Column<CouponDTO, String> COUPON_PRICE_COLUMN = new TextColumn<CouponDTO>() {

    @Override
    public String getValue(CouponDTO c) {
      return basicDataFormatter.format(c.getDiscountedPrice(), ValueType.PRICE);
    }
  };

  private static final Header<String> COUPON_PRICE_HEADER = new Header<String>(new TextCell()) {
    @Override
    public String getValue() {
      return "Price";
    }
  };

  public static final Column<CouponDTO, String> COUPON_DISCOUNT_COLUMN =
      new TextColumn<CouponDTO>() {

        @Override
        public String getValue(CouponDTO c) {
          return basicDataFormatter.format(
              c.getItemPrice().subtract(c.getDiscountedPrice()),
              ValueType.PRICE);
        }
      };

  private static final Header<String> COUPON_DISCOUNT_HEADER = new Header<String>(new TextCell()) {
    @Override
    public String getValue() {
      return "Discount";
    }
  };

  public static final Column<CouponDTO, String> COUPON_STATUS_COLUMN = new TextColumn<CouponDTO>() {

    @Override
    public String getValue(CouponDTO c) {
      return "Active";
    }
  };

  private static final Header<String> COUPON_STATUS_HEADER = new Header<String>(new TextCell()) {
    @Override
    public String getValue() {
      return "Status";
    }
  };

  public static final Column<CouponDTO, String> COUPON_START_TIME_COLUMN =
      new TextColumn<CouponDTO>() {

        @Override
        public String getValue(CouponDTO c) {
          return basicDataFormatter.format(c.getStartDate(), ValueType.DATE_VALUE_FULL);
        }
      };

  private static final Header<String> COUPON_START_TIME_HEADER =
      new Header<String>(new TextCell()) {
        @Override
        public String getValue() {
          return "Start time";
        }
      };

  public static final Column<CouponDTO, String> COUPON_END_TIME_COLUMN =
      new TextColumn<CouponDTO>() {

        @Override
        public String getValue(CouponDTO c) {
          return basicDataFormatter.format(c.getEndDate(), ValueType.DATE_VALUE_FULL);
        }
      };

  public static final Header<String> COUPON_END_TIME_HEADER = new Header<String>(new TextCell()) {
    
    @Override
    public String getValue() {
      return "End time";
    }
  };

  public static final Column<CouponDTO, String> COUPON_CREATION_TIME_COLUMN = new TextColumn<CouponDTO>() {

    @Override
    public String getValue(CouponDTO c) {
      return basicDataFormatter.format(c.getTimeCreated(), ValueType.DATE_VALUE_FULL);
    }
  };

  private static final Header<String> COUPON_CREATION_TIME_HEADER = new Header<String>(new TextCell()) {
    @Override
    public String getValue() {
      return "Creation time";
    }
  };

  public static GenericColumnDefinition<CouponDTO, String> COUPON_TITLE =
      new GenericColumnDefinition<CouponDTO, String>(
          COUPON_TITLE_HEADER,
          COUPON_TITLE_COLUMN, 
          new FieldUpdater<CouponDTO, String>() {

            @Override
            public void update(int index, CouponDTO object, String value) {
              Window.alert("Clicked on "+object.getDescription());
            }
          });

  public static GenericColumnDefinition<CouponDTO, String> COUPON_DESCRIPTION =
      new GenericColumnDefinition<CouponDTO, String>(
          COUPON_DESCRIPTION_HEADER,
          COUPON_DESCRIPTION_COLUMN, 
          new FieldUpdater<CouponDTO, String>() {

            @Override
            public void update(int index, CouponDTO object, String value) {
              Window.alert("Clicked on "+object.getDescription());
            }
          });
  
  public static GenericColumnDefinition<CouponDTO, String> COUPON_PRICE =
      new GenericColumnDefinition<CouponDTO, String>(COUPON_PRICE_HEADER, COUPON_PRICE_COLUMN);
  public static GenericColumnDefinition<CouponDTO, String> COUPON_DISCOUNT =
      new GenericColumnDefinition<CouponDTO, String>(COUPON_DISCOUNT_HEADER, COUPON_DISCOUNT_COLUMN);
  public static GenericColumnDefinition<CouponDTO, String> COUPON_STATUS =
      new GenericColumnDefinition<CouponDTO, String>(COUPON_STATUS_HEADER, COUPON_STATUS_COLUMN);
  public static GenericColumnDefinition<CouponDTO, String> COUPON_START_TIME =
      new GenericColumnDefinition<CouponDTO, String>(
          COUPON_START_TIME_HEADER,
          COUPON_START_TIME_COLUMN);
  public static GenericColumnDefinition<CouponDTO, String> COUPON_END_TIME =
      new GenericColumnDefinition<CouponDTO, String>(COUPON_END_TIME_HEADER, COUPON_END_TIME_COLUMN);
  public static GenericColumnDefinition<CouponDTO, String> COUPON_CREATION_TIME =
      new GenericColumnDefinition<CouponDTO, String>(COUPON_CREATION_TIME_HEADER, COUPON_CREATION_TIME_COLUMN);

  public CouponReportTableBuilder with(GenericColumnDefinition<CouponDTO, String> column) {
    columnDefinitions.add(column);
    return this;
  }

  public CouponReportTableBuilder setPageSize(int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  public CouponReportTableBuilder
      setSelectionModel(NoSelectionModel<CouponDTO> selectionModel) {
    this.selectionModel = selectionModel;
    return this;
  }

  public CouponReportTableBuilder setHover(boolean b) {
    this.hover = b;
    return this;
  }
  
  public CellTable<CouponDTO> build() {
    CellTable<CouponDTO> couponReportTable = new CellTable<CouponDTO>(pageSize);
    for (GenericColumnDefinition<CouponDTO, String> colDef : columnDefinitions) {
      couponReportTable.addColumn(colDef.getColumn(), colDef.getHeader());
    }
    
    if (selectionModel != null) {
      couponReportTable.setSelectionModel(selectionModel);
    }
    
    couponReportTable.setHover(true);
    return couponReportTable;
  }

}
