package com.xcite.core.interfaces;

import org.json.JSONObject;

import com.xcite.core.servlet.ParameterMap;

public abstract class IJSONAction extends IServletAction {

	protected int statusCode = -1;

	public Type getType() {
		return Type.JSON;
	}

	public abstract JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable;

	public int getStatusCode() {
		return statusCode;
	}
}
