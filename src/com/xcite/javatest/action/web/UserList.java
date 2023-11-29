package com.xcite.javatest.action.web;

import com.xcite.core.interfaces.IWebAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;

public class UserList extends IWebAction {

	@Override
	public ProcessResult processRequest(ParameterMap parameterMap) throws Throwable {
		ProcessResult result = ProcessResult.createProcessDispatchResult("userList");

		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}
}
