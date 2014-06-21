package com.ziplly.app.client.exceptions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.ziplly.app.client.exceptions.ErrorDefinitions.ErrorDefinition;
import com.ziplly.app.client.places.BusinessAccountSettingsPlace;
import com.ziplly.app.client.places.LoginPlace;
import com.ziplly.app.client.view.event.NeedsLoginEvent;
import com.ziplly.app.client.widget.AlertModal;
import com.ziplly.app.client.widget.MessageModal;

public class GlobalErrorHandler {
	AlertModal modal = new AlertModal();
	MessageModal mmodal = new MessageModal();
	PlaceController controller;
	
	private EventBus eventBus;

	@Inject
	public GlobalErrorHandler(EventBus eventBus) {
		this.eventBus = eventBus;
		controller = new PlaceController(eventBus);
  }
	
	public void handlerError(Throwable th) {
		ErrorDefinition<?> errorDef = ErrorDefinitions.getErrorDefinition(th.getClass());
		if (errorDef == null) {
			return;
		}
		
		mmodal.setContent(errorDef.getErrorMessage());
		mmodal.setTitle("Error");
	  mmodal.show();
	  
		if (errorDef.getCode() == ErrorCodes.NeedsLoginError) {
		  controller.goTo(new LoginPlace());
//		  eventBus.fireEvent(new NeedsLoginEvent());
		  return;
		} else if (errorDef.getCode() == ErrorCodes.NeedsSubscriptionError) {
		  FlowPanel panel = new FlowPanel();
		  panel.add(new HTMLPanel(errorDef.getErrorMessage()));
		  Anchor planSettingAnchor = new Anchor();
		  planSettingAnchor.setText("Account Settings");
		  planSettingAnchor.addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          BusinessAccountSettingsPlace settingsPlace = new BusinessAccountSettingsPlace(BusinessAccountSettingsPlace.SettingsTab.SUBSCRIPTION_PLANS);
          mmodal.hide();
          controller.goTo(settingsPlace);
          return;
        }
		    
		  });
		  panel.add(planSettingAnchor);
		  mmodal.setWidget(panel);
      return;
    }
		
		postHandle();
	}

	public void postHandle() {
  }
}
