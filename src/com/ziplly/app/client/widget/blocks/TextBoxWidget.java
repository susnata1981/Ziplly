package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.user.client.Window;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class TextBoxWidget {
	private ControlGroup cg;	
	private TextBox field;
	private HelpInline helpInline;
	private int maxLength = FieldVerifier.MAX_TWEET_LENGTH;
	private DataType dataType;
	
	public TextBoxWidget(ControlGroup cg, TextBox field, HelpInline helpInline, DataType dataType) {
		this.cg = cg;
		this.field = field;
		this.helpInline = helpInline;
		this.dataType = dataType;
		helpInline.setVisible(false);
  }

	public void setMaxLength(int maxLength) {
		this.maxLength  = maxLength;
	}
	
	public boolean validate() {
		resetError();
		ValidationResult result = null;
		switch(dataType) {
			case STRING:
				 result = FieldVerifier.validateString(field.getText(), maxLength);
				 break;
			case INTEGER:
				 result = FieldVerifier.validateInteger(field.getText(), maxLength);
				 break;
			default:
				Window.alert("Invalid data type");
				return false;
		}
		
		if (!result.isValid()) {
			cg.setType(ControlGroupType.ERROR);
			helpInline.setText(result.getErrors().get(0).getErrorMessage());
			helpInline.setVisible(true);
			return false;
		}
		
		return true;
	}
	
	public void resetError() {
		cg.setType(ControlGroupType.NONE);
		helpInline.setText("");
		helpInline.setVisible(false);
	}

	public void clear() {
		field.setText("");
		resetError();
  }
}
