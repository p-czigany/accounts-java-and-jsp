package com.xcite.javatest.action.web;

import com.xcite.core.interfaces.IWebAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.SqlQuery;

import java.util.List;
import java.util.Map;

public class UserList extends IWebAction {

	@Override
	public ProcessResult processRequest(ParameterMap parameterMap) throws Throwable {
		ProcessResult result = ProcessResult.createProcessDispatchResult("userList");

		// Fetch data from the 'xjfw.account' table
		List<Map<String, Object>> userList = DataBase.select(new SqlQuery("xjfw.account"));

		// Pass the userList to the JSP file
		result.addData("userList", userList);

		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}
}
