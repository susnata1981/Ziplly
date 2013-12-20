package com.ziplly.app.client.resource;

import com.github.gwtbootstrap.client.ui.CellTable;


public interface TableResources extends CellTable.Resources {
	
	interface TableStyle extends CellTable.Style {
	}
	
	@Override
	@Source({ CellTable.Style.DEFAULT_CSS, "tablestylesheet.css"})
	TableStyle cellTableStyle();
}
