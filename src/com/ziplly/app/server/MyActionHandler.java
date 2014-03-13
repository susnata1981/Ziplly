package com.ziplly.app.server;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.ziplly.app.shared.MyAction;
import com.ziplly.app.shared.MyResult;

public class MyActionHandler implements ActionHandler<MyAction, MyResult> {

	@Override
	public MyResult execute(MyAction action, ExecutionContext arg1) throws DispatchException {
		return new MyResult("Got message:" + action.getMessage());
	}

	@Override
	public Class<MyAction> getActionType() {
		return MyAction.class;
	}

	@Override
	public void
	    rollback(MyAction arg0, MyResult arg1, ExecutionContext arg2) throws DispatchException {
		// TODO Auto-generated method stub

	}
}
