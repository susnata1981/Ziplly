package com.ziplly.app.client.exceptions;

public class InvalidUrlException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidUrlException(String msg) {
    super(msg);
  }
}
