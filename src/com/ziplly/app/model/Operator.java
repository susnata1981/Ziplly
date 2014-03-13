package com.ziplly.app.model;

import java.io.Serializable;

public enum Operator implements Serializable {
	EQUAL("="),
	UNEQUAL("!="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL(">="),
	LESS_THAN("<"),
	LESS_THAN_EQUAL("<="),
	IN("in"),
	ANCESTOR("ancestor"),
	NONE("");

	private String op;

	Operator() {
		this("=");
	}

	Operator(String operator) {
		this.op = operator;
	}

	public String getOperator() {
		return op;
	}
}
