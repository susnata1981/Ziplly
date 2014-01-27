package com.ziplly.app.client;

import java.util.List;

public abstract class Fragment<T> {
	protected List<T> arguments;

	public Fragment(T...args) {
		for(T arg : args) {
			getArguments().add(arg);
		}
	}

	public abstract List<T> getArguments();
}
