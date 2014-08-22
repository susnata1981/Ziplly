package com.ziplly.app.client.view.account;

import java.util.ArrayList;
import java.util.List;

import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.AccountNotificationDTO;

public class AccountDetails {
  public AccountDTO getAccount() {
    return account;
  }
  public void setAccount(AccountDTO account) {
    this.account = account;
  }
  public int getUnreadMessageCount() {
    return unreadMessageCount;
  }
  public void setUnreadMessageCount(int unreadMessageCount) {
    this.unreadMessageCount = unreadMessageCount;
  }
  public int getTotalTweets() {
    return totalTweets;
  }
  public void setTotalTweets(int totalTweets) {
    this.totalTweets = totalTweets;
  }
  public int getTotalComments() {
    return totalComments;
  }
  public void setTotalComments(int totalComments) {
    this.totalComments = totalComments;
  }
  public int getTotalLikes() {
    return totalLikes;
  }
  public void setTotalLikes(int totalLikes) {
    this.totalLikes = totalLikes;
  }
  public List<AccountNotificationDTO> getNotifications() {
    return notifications;
  }
  public void setNotifications(List<AccountNotificationDTO> notifications) {
    this.notifications = notifications;
  }

  public AccountDTO account;
  public int unreadMessageCount;
  public int totalTweets;
  public int totalComments;
  public int totalLikes;
  public List<AccountNotificationDTO> notifications = new ArrayList<AccountNotificationDTO>();
}
