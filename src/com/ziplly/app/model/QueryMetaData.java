package com.ziplly.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryMetaData implements Serializable {
	private static final long serialVersionUID = 1L;
	private EntityType entityType;
	private List<Predicate> predicates = new ArrayList<Predicate>();
	private Integer offset, limit;
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	
	public void addPredicate(Field field, Operator op, String value) {
		predicates.add(new Predicate(field, op, Collections.singletonList(value)));
	}
	
	public void addPredicate(Field field, Operator op, List<String> values) {
		if (op != Operator.IN) {
			throw new IllegalArgumentException("Illegal argument to addPredicate function");
		}
		predicates.add(new Predicate(field, op, values));
	}
	
	public boolean hasParams() {
		return getPredicates().size() > 0;
	}

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
