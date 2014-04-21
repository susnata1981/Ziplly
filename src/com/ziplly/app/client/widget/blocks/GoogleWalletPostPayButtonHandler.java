package com.ziplly.app.client.widget.blocks;

import com.ziplly.app.model.overlay.GoogleWalletFailureResult;
import com.ziplly.app.model.overlay.GoogleWalletSuccessResult;

public interface GoogleWalletPostPayButtonHandler {
	public void onSuccess(GoogleWalletSuccessResult result);
	
	public void onFailure(GoogleWalletFailureResult result);
}
