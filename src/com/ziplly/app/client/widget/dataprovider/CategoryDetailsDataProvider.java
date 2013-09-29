package com.ziplly.app.client.widget.dataprovider;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.literati.app.client.LiteratiServiceAsync;
import com.literati.app.shared.CategoryDetails;

public class CategoryDetailsDataProvider extends AbstractDataProvider<CategoryDetails>{

	private LiteratiServiceAsync service;

	public CategoryDetailsDataProvider(LiteratiServiceAsync service) {
		this.service = service;
	}
	
	@Override
	protected void onRangeChanged(HasData<CategoryDetails> display) {
		service.getPopularCategoryWithAccounts(new AsyncCallback<List<CategoryDetails>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get category details data:"+caught.getMessage());

			}

			@Override
			public void onSuccess(List<CategoryDetails> result) {
				updateRowData(0, result);
			}
		});	
	}

}
