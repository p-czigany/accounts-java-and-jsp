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

		result.addData("userList", getUserList());

		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}

	private List<Map<String, Object>> getUserList() {
		SqlQuery q = new SqlQuery("xjfw.account");
		q.order.add("createDate");
		return DataBase.select(q);
	}
}
