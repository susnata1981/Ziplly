package com.ziplly.app.client.widget.cell;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;
import com.google.gwt.user.client.History;
import com.ziplly.app.model.Account;
import com.ziplly.app.model.CategoryDetails;

public class CategoryDetailsCellUiBinder extends AbstractCell<CategoryDetails>{

	public interface Renderer extends UiRenderer {
		void render(SafeHtmlBuilder sb, String category, Account account);
	}
	
	public CategoryDetailsCellUiBinder() {
		super(BrowserEvents.CLICK);
	}
	
    public void onBrowserEvent(Context context, Element parent, CategoryDetails value,
            NativeEvent event, ValueUpdater<CategoryDetails> valueUpdater) {
	    History.newItem("category/"+value.category.getCategoryType().toLowerCase());
	} 
	
	@Override
	public void render(Context context,
			CategoryDetails value, SafeHtmlBuilder sb) {
		
		if (value != null && value.accounts.size()>0) {
			StringBuilder resp = new StringBuilder();
			resp.append("<h3>"+value.category.getCategoryType().toLowerCase()+"</h3>");
			resp.append("<ul>");
			for(Account a: value.accounts) {
				resp.append("<li class='category_profiles'>");
				resp.append("<img src='"+a.getImageUrl()+"' width='60' height='40'>");
				resp.append("<span class='name'>"+a.getDisplayName()+"</span>");
				resp.append("</li>");
			}
			resp.append("</ul>");
			sb.appendHtmlConstant("<div class='category'>"+resp.toString()+"</div>");
		}
	}

	
}
