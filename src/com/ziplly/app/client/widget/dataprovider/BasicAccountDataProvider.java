package com.ziplly.app.client.widget.dataprovider;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.literati.app.client.LiteratiServiceAsync;
import com.literati.app.client.view.AccountListView;
import com.literati.app.shared.AccountDetails;
import com.literati.app.shared.api.request.GetAccountDetailsRequest;
import com.literati.app.shared.api.response.GetAccountDetailsResponse;

public class BasicAccountDataProvider extends AbstractDataProvider<AccountDetails>{
	private LiteratiServiceAsync service;
	private GetAccountDetailsRequest gadr;
	private AccountListView alv;

	public BasicAccountDataProvider(
			AccountListView alv) {
		this.alv = alv;
		this.service = alv.getService();
	}
	
	@Override
	protected void onRangeChanged(HasData<AccountDetails> display) {
		if (gadr == null) {
			throw new IllegalArgumentException("Request object not set in BasicAccountDataProvider object");
		}
		
		final Range range = display.getVisibleRange();
		gadr.start = range.getStart();
		gadr.end = range.getStart() + range.getLength();
		service.getAccountDetails(gadr, 
				new AsyncCallback<GetAccountDetailsResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO
				Window.alert("Failed to get account data:"+caught.getMessage());
			}

			@Override
			public void onSuccess(GetAccountDetailsResponse adr) {
				int size = adr.accounts.size();
				if (size == 0) {
					alv.showErrorMessage();
				}
				
				alv.getCellList().setRowCount(size, true);
				updateRowData(range.getStart(), adr.accounts);
			}
		});
	}

	public GetAccountDetailsRequest getGadr() {
		return gadr;
	}

	public void setGadr(GetAccountDetailsRequest gadr) {
		this.gadr = gadr;
	}
}
