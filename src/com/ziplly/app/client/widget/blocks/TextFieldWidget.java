package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.google.gwt.user.client.ui.HasText;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class TextFieldWidget extends AbstractBaseTextWidget {
  private int maxLength = 80;

  public TextFieldWidget(ControlGroup cg, HasText field, HelpInline helpInline) {
    super(cg, field, helpInline);
  }

  @Override
  public ValidationResult internalValidate() {
    return FieldVerifier.validateString(field.getText(), maxLength );
  }

}