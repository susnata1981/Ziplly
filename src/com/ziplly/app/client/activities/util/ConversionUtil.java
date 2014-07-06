package com.ziplly.app.client.activities.util;

import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.model.AccountDTO;

public class ConversionUtil {
  public static <T extends AccountDTO> List<T> convert(List<AccountDTO> accounts, Class<T> clazz) {
    List<T> result = new ArrayList<T>();
    for (AccountDTO acct : accounts) {
      if (acct.getClass().equals(clazz)) {
        result.add((T) acct);
      }
    }

    return result;
  }
}
