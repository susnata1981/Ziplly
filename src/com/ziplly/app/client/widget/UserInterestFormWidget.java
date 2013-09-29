package com.ziplly.app.client.widget;

import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.ziplly.app.model.InterestList;
import com.ziplly.app.model.InterestList.Activity;

public class UserInterestFormWidget extends Composite {
	Modal form = new Modal();

	public UserInterestFormWidget() {
		InterestList interestList = InterestList.getInstance();
		HTMLPanel interestPanel = new HTMLPanel("");
		Map<Activity, List<String>> map = interestList.getInterests();
		Accordion accordion = new Accordion();
		for(Activity activity : map.keySet()) {
			List<String> interests = map.get(activity);
			AccordionGroup ag = new AccordionGroup();
			ag.setHeading(activity.name());
			for(String interest: interests) {
				CheckBox cb = new CheckBox(interest);
				ag.add(cb);
			}
			accordion.add(ag);
		}
		form.add(accordion);
		interestPanel.add(form);
	}
	
	public void show() {
		form.show();
	}
	
	public void hide() {
		form.hide();
	}
}
