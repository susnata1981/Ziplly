package com.ziplly.app.client.view.factory;

public interface Converter<T,R> {
	R convert(T data);
}
