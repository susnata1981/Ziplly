package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.user.client.ui.HasText;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public abstract class AbstractBaseTextWidget {
  private ControlGroup cg;
  protected HasText field;
  private HelpInline helpInline;

  public AbstractBaseTextWidget(ControlGroup cg, HasText field, HelpInline helpInline) {
    this.cg = cg;
    this.field = field;
    this.helpInline = helpInline;
    helpInline.setVisible(false);
  }

  public boolean validate() {
    resetError();
    ValidationResult result = internalValidate();
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

  public String getValue() {
    return FieldVerifier.sanitize(field.getText());
  }

  public abstract ValidationResult internalValidate();
}
