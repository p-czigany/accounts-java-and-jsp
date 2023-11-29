package com.xcite.core.servlet.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.xcite.core.interfaces.IWebAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;
import com.xcite.core.utils.DataBase;

public class DatabaseContent extends IWebAction {

	@Override
	public boolean requireRequestURI() {
		return true;
	}

	@Override
	public boolean requireResponseObject() {
		return true;
	}

	@Override
	public ProcessResult processRequest(ParameterMap parameterMap) throws IOException {
		String uri = parameterMap.get("__xjfw_requestURI");
		HttpServletResponse response = (HttpServletResponse) parameterMap.getObject("__xjfw_responseObject");
		String value = (String) DataBase.selectFieldByField("xjfw.core.databasecontent", "key", uri.substring(uri.indexOf('/')), "value");
		response.getWriter().write(value);
		return ProcessResult.createProcessDirectResult();
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}
}
