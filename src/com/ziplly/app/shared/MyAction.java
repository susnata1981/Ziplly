package com.ziplly.app.shared;

import net.customware.gwt.dispatch.shared.Action;

import com.ziplly.app.client.dispatcher.Cacheable;

public class MyAction implements Action<MyResult>, Cacheable {
	private String message;

	public MyAction() {
	}

	public MyAction(String ms) {
		message = ms;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyAction other = (MyAction) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
