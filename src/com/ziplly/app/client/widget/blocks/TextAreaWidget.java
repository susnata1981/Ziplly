package com.ziplly.app.client.widget.blocks;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class TextAreaWidget extends AbstractBaseTextWidget {
  private int maxLength;
  private int minLength;

  public TextAreaWidget(ControlGroup cg, TextArea field, HelpInline helpInline) {
    this(cg, field, helpInline, FieldVerifier.MAX_TWEET_LENGTH, 0);
  }

  public TextAreaWidget(ControlGroup cg, TextArea field, HelpInline helpInline, int minLength, int maxLength) {
    super(cg, field, helpInline);
    this.minLength = minLength;
    this.maxLength = maxLength;
  }
  
  @Override
  public ValidationResult internalValidate() {
    return FieldVerifier.validateString(field.getText(), minLength, maxLength);
  }
}
