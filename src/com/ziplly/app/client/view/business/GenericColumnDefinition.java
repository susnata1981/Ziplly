package com.ziplly.app.client.view.business;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

public class GenericColumnDefinition<T,C> {
  private Column<T, C> column;
  private Header<String> header;

  public GenericColumnDefinition(Header<String> header, Column<T, C> column) {
    this.setHeader(header);
    this.setColumn(column);
  }

  public GenericColumnDefinition(Header<String> header, Column<T, C> column, FieldUpdater<T,C> fieldUpdater) {
    this.setHeader(header); 
    this.setColumn(column);
    this.column.setFieldUpdater(fieldUpdater);
  }
  
  public Header<String> getHeader() {
    return header;
  }

  public void setHeader(Header<String> header) {
    this.header = header;
  }

  public Column<T, C> getColumn() {
    return column;
  }

  public void setColumn(Column<T, C> column) {
    this.column = column;
  }
}
