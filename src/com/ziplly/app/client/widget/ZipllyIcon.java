package com.ziplly.app.client.widget;

public enum ZipllyIcon {
	REPLY("fa-reply"),
	THUMBS_UP("icon-thumbs-up"),
	EDIT("fa-pencil"),
	COMMENTS("fa-comments"),
	SPINNER("fa fa-repeat");
	
	private String name;

	private ZipllyIcon(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
