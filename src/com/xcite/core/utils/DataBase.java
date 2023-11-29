package com.xcite.core.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONObject;

import com.xcite.core.devtools.JettyUtils;

public class DataBase {

	private static boolean logSql = false;

	public static enum DataBaseMode {
		ORACLE, INMEMMAPSTORE
	}

	private static Map<DataBaseMode, DBInterface> instances = new HashMap<DataBase.DataBaseMode, DBInterface>();

	private static DataBaseMode mode = Configuration.get("datebasemode") != null ? DataBaseMode.valueOf(Configuration.get("datebasemode")) : DataBaseMode.INMEMMAPSTORE;


	public static DBInterface getDBImplementationByMode() {
		switch (mode) {
		case INMEMMAPSTORE:
			if (instances.get(mode) == null) {
				instances.put(mode, InMemMapStore.getInstance());
			}
			return instances.get(mode);
		}
		return null;
	}

	public static DBInterface getDBImplementationByTable(String table) {


			if (instances.get(DataBaseMode.INMEMMAPSTORE) == null) {
				instances.put(DataBaseMode.INMEMMAPSTORE, InMemMapStore.getInstance());
			}
			return instances.get(DataBaseMode.INMEMMAPSTORE);

	}

	public static List<Map<String, Object>> select(SqlQuery query) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(query.table);
		if (!offlineTableName.isEmpty()) {
			query.table = offlineTableName;
		}

		List<Map<String, Object>> result = getDBImplementationByTable(query.table).select(query);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + s + " " + query.table + " " + query.where);
			}
		}
		return result;
	}

	private static String getOfflineTableName(String table) {


		return table;
	}

	public static List<String> selectListIfTableExists(String table, String field) {
		Long s = System.currentTimeMillis();
		if (!ifTableExists(table)) {
			return new LinkedList<String>();

		}
		List<String> result = selectList(table, field);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + s + " " + table + " all");
			}
		}
		return result;
	}

	public static List<String> selectList(String table, String field) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		List<String> result = getDBImplementationByTable(table).selectList(table, field);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + s + " " + table + " all");
			}
		}
		return result;
	}

	private static boolean ifTableExists(String table) {

		return getDBImplementationByTable(table).ifTableExists(table);

	}

	public static JSONObject dbIsExist() {
		return getDBImplementationByMode().dbIsExist();
	}

	public static List<String> getTables() {
		return getDBImplementationByMode().getTables();
	}

	public static Map<String, String> getTableColumns(String tablename) {
		return getDBImplementationByTable(tablename).getTableColumns(tablename);
	}

	public static Map<String, String> selectMapIfTableExists(String table, String keyField, String valueField, Object... where) {
		Long s = System.currentTimeMillis();
		if (!ifTableExists(table)) {
			return new HashMap<String, String>();
		}
		Map<String, String> result = selectMap(table, keyField, valueField, where);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + table + " " + where + "(" + s + "ms)");
			}
		}
		return result;
	}

	public static Map<String, String> selectMap(String table, String keyField, String valueField, Object... where) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		Map<String, String> map = getDBImplementationByTable(table).selectMap(table, keyField, valueField, where);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + table + " " + where + "(" + s + "ms)");
			}
		}
		return map;
	}

	public static int selectUniqueIdByField(String table, String field, String value) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		int i = getDBImplementationByTable(table).selectUniqueIdByField(table, field, value);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + table + " " + field + " " + value + "(" + s + "ms)");
			}
		}
		return i;
	}

	public static Object selectField(String table, int id, String field) {
		Long s = System.currentTimeMillis();
		Object o = selectFieldByField(table, "id", Integer.toString(id), field);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + table + " id " + id + "(" + s + "ms)");
			}
		}
		return o;
	}

	public static Object selectFieldByField(String table, String field, String value, String responseField) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		Object o = getDBImplementationByTable(table).selectFieldByField(table, field, value, responseField);
		s = System.currentTimeMillis() - s;
		if (s > 5 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("SELECT:" + table + " " + field + " " + value + "(" + s + "ms)");
			}
		}
		return o;
	}

	public static Integer insert(String table, Object... fields) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		Integer i = getDBImplementationByTable(table).insert(table, fields);
		s = System.currentTimeMillis() - s;
		if (s > 1 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("INSERT:" + table + "(" + s + "ms)");
			}
		}
		return i;
	}

	public static int insert(SqlQuery query) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(query.table);
		if (!offlineTableName.isEmpty()) {
			query.table = offlineTableName;
		}

		Integer i = getDBImplementationByTable(query.table).insert(query);
		s = System.currentTimeMillis() - s;
		if (s > 1 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("INSERT:" + query.table + "(" + s + "ms)");
			}
		}
		return i;
	}

	//TODO return updated row count

	public static void update(String table, int id, Object... fields) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		getDBImplementationByTable(table).update(table, id, fields);
		s = System.currentTimeMillis() - s;
		if (s > 1 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("UPDATE:" + table + "(" + s + "ms)");
			}
		}
	}

	public static void update(Integer id, SqlQuery query) {
		Long s = System.currentTimeMillis();

		String offlineTableName = getOfflineTableName(query.table);
		if (!offlineTableName.isEmpty()) {
			query.table = offlineTableName;
		}

		getDBImplementationByTable(query.table).update(id, query);
		s = System.currentTimeMillis() - s;
		if (s > 1 && !"true".equals(Configuration.get("disable.query.logging"))) {
			if (logSql) {
				JettyUtils.logLine("UPDATE:" + query.table + "(" + s + "ms)");
			}
		}

	}

	public static void delete(String table, int id) {

		String offlineTableName = getOfflineTableName(table);
		if (!offlineTableName.isEmpty()) {
			table = offlineTableName;
		}

		getDBImplementationByTable(table).delete(table, id);
	}

	public static void initRequiredTables(ServletContext context) {
		getDBImplementationByMode().initRequiredTables(context);

	}

	public static boolean checkTableIsExistBySQL(String table) {
		return getDBImplementationByTable(table).checkTableIsExistBySQL(table);

	}

}
