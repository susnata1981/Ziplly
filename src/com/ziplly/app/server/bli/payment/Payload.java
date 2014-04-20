package com.ziplly.app.server.bli.payment;

public class Payload {
	private String iss;
	private String typ;
	private String aud;
	private Request request;
	private int iat;
	private int exp;
	private Response response;

	public String getIss() {
		return iss;
	}

	public String getTyp() {
		return typ;
	}

	public String getAud() {
		return aud;
	}

	public Request getRequest() {
		return request;
	}

	public int getIat() {
		return iat;
	}

	public int getExp() {
		return exp;
	}

	public Response getResponse() {
		return response;
	}
}