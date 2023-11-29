package com.xcite.core.db;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.utils.DataBase;

public class GetColumns extends IJSONAction {

	@Override
	public JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable {
		JSONObject result = new JSONObject();
		if (parameterMap.get("table") != null && !parameterMap.get("table").trim().isEmpty()) {
			DataBase.checkTableIsExistBySQL(parameterMap.get("table").trim().toLowerCase());
			Map<String, String> columns = DataBase.getTableColumns(parameterMap.get("table").trim().toLowerCase());
			JSONArray columnsArray = new JSONArray();
			for (String key : columns.keySet()) {
				columnsArray.put(key + "(" + columns.get(key) + ")");
			}
			result.put("columns", columnsArray);
		} else {
			result.put("error", "(table) parameter error");
		}

		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}

}
