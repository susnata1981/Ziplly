package com.ziplly.app.client.widget;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.ziplly.app.client.activities.Presenter;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.view.AbstractView;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.model.AccountDTO;

public class ProfileListWidget extends AbstractView {

	private static ProfileListWidgetUiBinder uiBinder = GWT.create(ProfileListWidgetUiBinder.class);

	interface ProfileListWidgetUiBinder extends UiBinder<Widget, ProfileListWidget> {
	}

	public interface Style extends CssResource {
		String row();
		String name();
	}
	
	@UiField
  Style style;
  
  @UiField
  Alert message;
  
  @UiField
  FlowPanel profileListPanel;

  private Presenter presenter;
  
	@Inject
	public ProfileListWidget(com.google.web.bindery.event.shared.EventBus eventBus) {
		super(eventBus);
		initWidget(uiBinder.createAndBindUi(this));
		message.setVisible(false);
	}

	private void addProfile(final AccountDTO account) {
		HPanel hpanel = new HPanel();
		hpanel.addStyleName(style.row());
		Image image = new Image(); 
		image.setUrl(accountFormatter.format(account, ValueType.PROFILE_IMAGE_URL));
		image.setSize("40px", "40px");
		image.addClickHandler(new ClickHandler() {

			@Override
      public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(account.getAccountId()));
      }
			
		});
		
		profileListPanel.add(image);
		hpanel.add(image);
		Anchor nameAnchor = new Anchor(account.getDisplayName());
		nameAnchor.addStyleName(style.name());
		hpanel.add(nameAnchor);
		nameAnchor.addClickHandler(new ClickHandler() {

			@Override
      public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(account.getAccountId()));
      }
			
		});
		profileListPanel.add(hpanel);
	}
	
	public void displayProfiles(List<AccountDTO> accounts) {
		profileListPanel.clear();
		for(AccountDTO account : accounts) {
			addProfile(account);
		}
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	public void displayMessage(String msg, AlertType type) {
	  message.setText(msg);
	  message.setType(type);
	  message.setVisible(true);
	}
}
