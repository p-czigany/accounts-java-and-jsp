package com.xcite.core.db;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.InMemMapStore;

public class GetInMemoryTables extends IJSONAction {

	@Override
	public JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable {
		List<String> tableNames = InMemMapStore.getInstance().getTables();
		Collections.sort(tableNames);
		JSONArray tableaArray = new JSONArray();

		for (String name : tableNames) {
			JSONObject table = new JSONObject().put("name", name).put("rowCount", DataBase.selectList(name, "id").size());
			JSONArray columnArray = new JSONArray();

			for (String colName : DataBase.getTableColumns(name).keySet()) {
				columnArray.put(colName);
			}
			table.put("columns", columnArray);
			tableaArray.put(table);
		}
		return new JSONObject().put("tables", tableaArray);
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}

}
