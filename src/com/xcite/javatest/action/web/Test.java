package com.xcite.javatest.action.web;

import java.util.List;
import java.util.Map;

import com.xcite.core.interfaces.IWebAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.SqlQuery;

public class Test extends IWebAction {

	@Override
	public ProcessResult processRequest(ParameterMap parameterMap) throws Throwable {
		ProcessResult result = ProcessResult.createProcessDispatchResult("test");

		String content = null;
		if (parameterMap.get("key") != null) {
			content = getContentByKey((String) parameterMap.get("key"));
		}
		if (content == null) {
			content = getContentByKey("main");
		}

		result.addData("content", content);

		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}

	private String getContentByKey(String key) {
		SqlQuery q = new SqlQuery("xjfw.core.databasecontent");
		q.where.add("key", key);
		q.fields.add("value");
		List<Map<String, Object>> queryresult = DataBase.select(q);
		if (queryresult.isEmpty()) {
			return null;
		}
		return (String) queryresult.get(0).get("value");
	}
}
