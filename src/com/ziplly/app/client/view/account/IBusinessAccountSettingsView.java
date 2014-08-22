package com.ziplly.app.client.view.account;

import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace.SettingsTab;
import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.model.BusinessAccountDTO;
import com.ziplly.app.model.SubscriptionPlanDTO;
import com.ziplly.app.model.overlay.SubscriptionDTO;

public interface IBusinessAccountSettingsView extends ISettingsView<BusinessAccountDTO, BusinessAccountSettingsPresenter> {
  
  void displayTransactionHistory(List<SubscriptionDTO> subscriptions);

  void displaySubscriptionPlans(List<SubscriptionPlanDTO> plans);

  void displayPaymentStatus(String msg, AlertType type);

  void clearPaymentStatus();

  void hideTransactionHistory();

  void initiatePay(Long subscriptionId, String token);

  void setTab(SettingsTab tab);

  void clearPasswords();
}
