package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class PositiveNumberTextWidget extends AbstractBaseTextWidget {

  private int maxIntegerLength = 5;

  public PositiveNumberTextWidget(ControlGroup cg, TextBox field, HelpInline helpInline) {
    super(cg, field, helpInline);
  }
  
  public PositiveNumberTextWidget(ControlGroup cg, TextBox field, HelpInline helpInline, boolean requireInstantValidation) {
    super(cg, field, helpInline, requireInstantValidation);
  }

  @Override
  public ValidationResult internalValidate() {
    return FieldVerifier.validatePositiveNumber(field.getText(), maxIntegerLength);
  }
}
