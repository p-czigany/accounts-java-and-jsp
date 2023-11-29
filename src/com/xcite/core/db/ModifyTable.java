package com.xcite.core.db;

import java.util.ArrayList;

import org.json.JSONObject;

import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.SqlQuery;

/*
 * Howto:   /admin/db/modifytable?access_password=ADMwelcome1&operation=insert&table=users&fieldsvalues=lastName:teszt123,firstName:teszt123,test:test,age:32
 * 			/admin/db/modifytable?access_password=ADMwelcome1&operation=update&table=users&id=2&fieldsvalues=lastName:teszt123,firstName:teszt123,test:test,age:32
 * 			/admin/db/modifytable?access_password=ADMwelcome1&operation=delete&table=users&id=2
 */
public class ModifyTable extends IJSONAction {

	@Override
	public JSONObject processRequest(ParameterMap parameterMap, JSONObject request) throws Throwable {

		String operation = parameterMap.get("operation");
		String table = parameterMap.get("table");
		String id = parameterMap.get("id");
		String fieldsvalues = parameterMap.get("fieldsvalues"); // fieldvalues=poiid:abcd,uiid:1234 ...

		SqlQuery query = new SqlQuery(table);
		if (fieldsvalues != null) {
			ArrayList<String> fieldsList = new ArrayList<String>();
			ArrayList<String> valueList = new ArrayList<String>();

			String[] splittedFieldValues = fieldsvalues.split(",");
			for (String fieldvalue : splittedFieldValues) {
				String[] splittedFieldValue = fieldvalue.split(":");
				fieldsList.add(splittedFieldValue[0]);
				valueList.add(splittedFieldValue[1]);
			}

			for (int i = 0; i < fieldsList.size(); i++) {
				query.fields.add(fieldsList.get(i), checkValue(valueList.get(i)));
			}
		}
		switch (operation) {

		case "insert":
			DataBase.insert(query);
			break;
		case "update":
			DataBase.update(Integer.valueOf(id), query);
			break;
		case "delete":
			DataBase.delete(table, Integer.valueOf(id));

		}

		return new JSONObject().put("msg", "sql operation done");

	}

	public Object checkValue(String value) {

		Object object = null;

		try {
			object = Integer.valueOf(value);
		} catch (NumberFormatException e) {

		}

		if (object == null) {

			object = Boolean.valueOf(value);

			if (!String.valueOf(object).equals(value)) {
				return value;
			}

		}

		return object;
	}

	@Override
	public String getAuthenticaionClassname() {
		return null;
	}

}
