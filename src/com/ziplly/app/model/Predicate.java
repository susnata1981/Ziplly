package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Predicate implements Serializable {
	private static final long serialVersionUID = 1L;
	private Field field;
	private Operator op;
	private List<String> values = new ArrayList<String>();
	private Object parent;
	private Class<?> parentEntity;

	public Predicate() {
	}

	public Predicate(Class<?> parentEntity, Operator op, Object o) {
		this.parentEntity = parentEntity;
		if (op.equals(Operator.ANCESTOR)) {
			this.op = op;
			this.parent = o;
		} else {
			throw new RuntimeException("Invalid Predicate!");
		}
	}

	public Predicate(Field field, Operator op, String value) {
		this.setField(field);
		this.setOp(op);
		getValues().add(value);
	}

	public Predicate(Field field, Operator op, Long value) {
		this.setField(field);
		this.setOp(op);
		getValues().add(value.toString());
	}

	public Predicate(Field field, Operator op, List<String> values) {
		this.setField(field);
		this.setOp(op);
		this.getValues().addAll(values);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Operator getOp() {
		return op;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Object getParent() {
		return parent;
	}

	public Class<?> getParentEntity() {
		return parentEntity;
	}
}
