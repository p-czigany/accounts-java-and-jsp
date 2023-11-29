package com.xcite.javatest.action.api;

import org.json.JSONObject;

import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.servlet.ParameterMap;

public class TestAjaxCall extends IJSONAction {

	@Override
	public JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable {

		JSONObject result = new JSONObject();

		result.put("TestAjaxCall", "success");
		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}



}
