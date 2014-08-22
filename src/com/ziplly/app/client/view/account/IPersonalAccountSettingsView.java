package com.ziplly.app.client.view.account;

import java.util.List;

import com.ziplly.app.client.view.ISettingsView;
import com.ziplly.app.model.InterestDTO;
import com.ziplly.app.model.PersonalAccountDTO;

public interface IPersonalAccountSettingsView extends ISettingsView<PersonalAccountDTO, PersonalAccountSettingsPresenter> {
  
  void displayAllInterests(List<InterestDTO> interests);
}
