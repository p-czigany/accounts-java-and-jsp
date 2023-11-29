package com.xcite.core.utils;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONObject;

public interface DBInterface {
	public static enum Consts {
		NOW, DESC
	}

	public static final Consts NOW = Consts.NOW;

	public List<Map<String, Object>> select(SqlQuery query);

	public List<String> selectListIfTableExists(String table, String field);

	public List<String> selectList(String table, String field);

	public JSONObject dbIsExist();

	public List<String> getTables();

	public Map<String, String> getTableColumns(String tablename);

	public Map<String, String> selectMapIfTableExists(String table, String keyField, String valueField, Object... where);

	public Map<String, String> selectMap(String table, String keyField, String valueField, Object... where);

	public int selectUniqueIdByField(String table, String field, String value);

	public Object selectField(String table, int id, String field);

	public Object selectFieldByField(String table, String field, String value, String responseField);

	public Integer insert(String table, Object... fields);

	public Integer insert(SqlQuery query);

	public void update(String table, int id, Object... fields);

	public void update(Integer id, SqlQuery query);

	public void delete(String table, int id);

	public void initRequiredTables(ServletContext context);

	public boolean checkTableIsExistBySQL(String table);

	public boolean ifTableExists(String table);
}
