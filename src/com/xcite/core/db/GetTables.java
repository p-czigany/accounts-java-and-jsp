package com.xcite.core.db;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.utils.DataBase;

public class GetTables extends IJSONAction {

	@Override
	public JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable {
		JSONObject result = new JSONObject();
		List<String> tables = DataBase.getTables();

		JSONArray tablesArray = new JSONArray();
		for (String table : tables) {
			tablesArray.put(table);
		}
		result.put("DB", DataBase.dbIsExist());
		result.put("tables", tablesArray);
		return result;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}

}
