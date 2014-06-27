package com.ziplly.app.client.view.business;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.ziplly.app.model.overlay.SubscriptionDTO;

public class SubscriptionTransactionTable {
    private static Header<String> PLAN_NAME_HEADER = new Header<String>(new TextCell()) {
      @Override
      public String getValue() {
        return "Plan Name";
      }
    };
    
    private static TextColumn<SubscriptionDTO> PLAN_NAME_COLUMN = new TextColumn<SubscriptionDTO>() {
      @Override
      public String getValue(SubscriptionDTO subscription) {
        return subscription.getSubscriptionPlan().getName();
      }
    };
    
    private static TextColumn<SubscriptionDTO> PLAN_DESCRIPTION_COLUMN = new TextColumn<SubscriptionDTO>() {
      @Override
      public String getValue(SubscriptionDTO subscription) {
        return subscription.getSubscriptionPlan().getDescription();
      }
    };
    
    private static Header<String> PLAN_DESCRIPTION_HEADER = new Header<String>(new TextCell()) {
      @Override
      public String getValue() {
        return "Plan Description";
      }
    };
    
    private static TextColumn<SubscriptionDTO> STATUS_COLUMN = new TextColumn<SubscriptionDTO>() {
      @Override
      public String getValue(SubscriptionDTO subscription) {
        return subscription.getStatus().name();
      }
    };
    
    private static Header<String> STATUS_HEADER = new Header<String>(new TextCell()) {
      @Override
      public String getValue() {
        return "Plan Status";
      }
    };
    
    private static TextColumn<SubscriptionDTO> RECURRING_AMOUNT_COLUMN = new TextColumn<SubscriptionDTO>() {
      @Override
      public String getValue(SubscriptionDTO subscription) {
        return subscription.getSubscriptionPlan().getFee().toString();
      }
    };
    
    private static Header<String> RECURRING_AMOUNT_HEADER = new Header<String>(new TextCell()) {
      @Override
      public String getValue() {
        return "Recurring Amount (monthly)";
      }
    };
    
    private static TextColumn<SubscriptionDTO> TIME_CREATED_COLUMN = new TextColumn<SubscriptionDTO>() {
      @Override
      public String getValue(SubscriptionDTO subscription) {
        return subscription.getTimeCreated().toString();
      }
    };
    
    private static Header<String> TIME_CREATED_HEADER = new Header<String>(new TextCell()) {
      @Override
      public String getValue() {
        return "Time created";
      }
    };
    
    public static ColumnDefinition<SubscriptionDTO, String> PLAN_NAME = new ColumnDefinition<SubscriptionDTO, String>(
        PLAN_NAME_HEADER, PLAN_NAME_COLUMN);
    public static ColumnDefinition<SubscriptionDTO, String> PLAN_DESCRIPTION = new ColumnDefinition<SubscriptionDTO, String>(
        PLAN_DESCRIPTION_HEADER, PLAN_DESCRIPTION_COLUMN);
    public static ColumnDefinition<SubscriptionDTO, String> STATUS = new ColumnDefinition<SubscriptionDTO, String>(
        STATUS_HEADER, STATUS_COLUMN);
    public static ColumnDefinition<SubscriptionDTO, String> RECURRING_AMOUNT = new ColumnDefinition<SubscriptionDTO, String>(
        RECURRING_AMOUNT_HEADER, RECURRING_AMOUNT_COLUMN);
    public static ColumnDefinition<SubscriptionDTO, String> TIME_CREATED = new ColumnDefinition<SubscriptionDTO, String>(
        TIME_CREATED_HEADER, TIME_CREATED_COLUMN);
    
    
    public static class Builder {
      private List<ColumnDefinition<SubscriptionDTO, String>> columns = new ArrayList<ColumnDefinition<SubscriptionDTO, String>>();
      
      public Builder with(ColumnDefinition<SubscriptionDTO, String> columnDef) {
        columns.add(columnDef);
        return this;
      }
      
      public CellTable<SubscriptionDTO> build() {
        CellTable<SubscriptionDTO> table = new CellTable<SubscriptionDTO>();
        for(ColumnDefinition<SubscriptionDTO, String> col : columns) {
          table.addColumn(col.getColumn(), col.getHeader());
        }
        table.setWidth("96%");
        return table;
      }
    }
}
