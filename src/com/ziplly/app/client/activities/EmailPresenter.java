package com.ziplly.app.client.activities;

import java.util.List;

public interface EmailPresenter extends Presenter {
	void invitePeople(List<String> emails);
}
