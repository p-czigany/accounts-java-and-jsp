package com.xcite.core.db;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.SqlQuery;

public class GetRows extends IJSONAction {

	@Override
	public JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable {
		JSONObject result = new JSONObject();
		if (parameterMap.get("table") != null && !parameterMap.get("table").trim().isEmpty()) {
			if (parameterMap.get("key_name") != null && !parameterMap.get("key_name").trim().isEmpty()) {
				if ("ALL".equals(parameterMap.get("key_name")) || (parameterMap.get("key_value") != null && !parameterMap.get("key_value").trim().isEmpty())) {
					SqlQuery q = new SqlQuery(parameterMap.get("table").toLowerCase());
					if (!"ALL".equals(parameterMap.get("key_name"))) {
						Map<String, String> columnsAndTypes = DataBase.getTableColumns(parameterMap.get("table").trim().toLowerCase());
						String type = columnsAndTypes.get(parameterMap.get("key_name"));
						switch (type) {
						case "java.lang.String":
							q.where.add(parameterMap.get("key_name"), parameterMap.get("key_value"));
							break;
						case "java.lang.Integer":
							q.where.add(parameterMap.get("key_name"), Integer.valueOf(parameterMap.get("key_value")));
							break;
						case "java.lang.Boolean":
							q.where.add(parameterMap.get("key_name"), Boolean.valueOf(parameterMap.get("key_value")));
							break;
						default:
							q.where.add(parameterMap.get("key_name"), parameterMap.get("key_value"));
							break;
						}
					}
					if (parameterMap.get("fields") != null && !parameterMap.get("fields").trim().isEmpty()) {
						for (String f : parameterMap.get("fields").split(",")) {
							q.fields.add(f);
						}
					} else {
						Map<String, String> columns = DataBase.getTableColumns(parameterMap.get("table").trim().toLowerCase());
						for (String f : columns.keySet()) {
							q.fields.add(f);
						}
					}
					q.autoDeletedFiltering = false;
					List<Map<String, Object>> queryresult = DataBase.select(q);
					JSONArray resultArray = new JSONArray();
					for (Map<String, Object> row : queryresult) {
						JSONObject j = new JSONObject();
						for (String key : row.keySet()) {
							j.put(key, row.get(key));
						}
						resultArray.put(j);
					}
					result.put("rows", resultArray);
				} else {
					result.put("error", "(key_value) parameter error");
				}
			} else {
				result.put("error", "(key_name) parameter error");
			}
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
