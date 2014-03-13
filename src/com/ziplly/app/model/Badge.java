package com.ziplly.app.model;

public enum Badge {
	chipmunk("Chipmunk"), badger("Badger"), bobcat("Bobcat"), wilderbeast("Wilder beast");

	private String name;

	Badge(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
