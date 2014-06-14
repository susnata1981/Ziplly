package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.base.TextBoxBase;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public abstract class AbstractBaseTextWidget {
  private ControlGroup cg;
  protected TextBoxBase field;
  private HelpInline helpInline;

  public AbstractBaseTextWidget(ControlGroup cg, TextBoxBase field, HelpInline helpInline) {
    this(cg, field, helpInline, true);
  }

  public AbstractBaseTextWidget(ControlGroup cg, TextBoxBase field, HelpInline helpInline, boolean requireInstantValidation) {
    this.cg = cg;
    this.field = field;
    this.helpInline = helpInline;
    helpInline.setVisible(false);
    if (requireInstantValidation) {
      setupHandler();
    }
  }

  private void setupHandler() {
    field.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        ValidationResult result = internalValidate();
        if (!result.isValid()) {
          setError(result.getErrors().get(0).getErrorMessage());
        } else {
          setValid();
        }
      }

    });
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

  private void setValid() {
    cg.setType(ControlGroupType.SUCCESS);
    helpInline.setText("");
    helpInline.setVisible(false);
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

  public void setError(String msg) {
    cg.setType(ControlGroupType.ERROR);
    helpInline.setText(msg);
    helpInline.setVisible(true);
  }
  
  public String getValue() {
    return FieldVerifier.sanitize(field.getText());
  }

  public abstract ValidationResult internalValidate();
}
