package com.xcite.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xcite.core.devtools.JettyUtils;

public class InMemMapStore implements DBInterface {

	private static InMemMapStore instance = null;

	public enum eTableCleaningRole {
		CLEAN_ALL_DELETED, CLEAN_AFTER_DATE, CLEAN_UNALLOWED, CLEAN_ALL_DELETED_BYTABLE
	}

	private InMemMapStore() {
	}

	public static InMemMapStore getInstance() {
		if (instance == null) {
			instance = new InMemMapStore();
		}
		return instance;
	}

	private static Map<String, InMemTable> tables = new HashMap<String, InMemTable>();

	private static String dateformat = "yyyy-MM-dd HH:mm:ss";

	public List<Map<String, Object>> select(SqlQuery query) {
		List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
		try {
			results = tables.get(query.table).selectRows(query.where, query.order);
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		}
		return results;
	}

	public static void clean(String table) {
		tables.get(table).clean();
	}

	public static void truncate(String table) {
		tables.get(table).truncate();
	}

	public List<String> selectListIfTableExists(String table, String field) {
		if (!ifTableExists(table)) {
			return new LinkedList<String>();

		}
		return selectList(table, field);
	}

	public void createTable(String tableName, Map<String, Class<?>> columns) {
		try {
			tables.put(tableName, new InMemTable(tableName, columns));
		} catch (Exception e) {
			JettyUtils.logThrowable(e);
		}
	}

	public List<String> selectList(String table, String field) {
		SqlQuery query = new SqlQuery(table);
		query.fields.add(field);
		List<String> results = new LinkedList<String>();
		try {
			List<Map<String, Object>> selectResult = select(query);
			for (Map<String, Object> row : selectResult) {
				results.add(row.get(field).toString());
			}
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		}
		return results;
	}

	public boolean ifTableExists(String table) {
		return tables.get(table) != null;
	}

	public JSONObject dbIsExist() {
		JSONObject json = new JSONObject();
		if (tables == null) {
			json.put("InMemMapStore", " is not exist");
		} else {
			json.put("InMemMapStore", " is exist");
		}

		return json;
	}

	public List<String> getTables() {

		return new ArrayList<String>(tables.keySet());
	}

	public Map<String, String> getTableColumns(String tablename) {
		Map<String, String> result = new HashMap<String, String>();
		Map<String, Class<?>> columns = tables.get(tablename).getColumns();
		for (String name : columns.keySet()) {
			result.put(name, columns.get(name).getName());
		}
		return result;
	}

	public Map<String, String> selectMapIfTableExists(String table, String keyField, String valueField, Object... where) {
		if (!ifTableExists(table)) {
			return new HashMap<String, String>();
		}
		return selectMap(table, keyField, valueField, where);
	}

	public Map<String, String> selectMap(String table, String keyField, String valueField, Object... where) {
		SqlQuery query = new SqlQuery(table);
		query.fields.add(keyField, valueField);
		Date now = new Date();
		for (int i = 0; i < where.length; i += 2) {
			Object value = where[i + 1];
			if (value instanceof String) {
				query.where.add((String) where[i], (String) value);
			} else if (value instanceof Integer) {
				if (tables.get(table).getColumns().get((String) where[i]).equals(Boolean.class)) {
					query.where.add((String) where[i], ((Integer) value) == 1);
				} else {
					query.where.add((String) where[i], (Integer) value);
				}
			} else if (value instanceof Boolean) {
				query.where.add((String) where[i], (Boolean) value);
			} else if (value instanceof Date) {
				query.where.add((String) where[i], (Date) value);
			} else if (value instanceof Consts && value.equals(NOW)) {
				query.where.add((String) where[i], now);
			} else {
				JettyUtils.logLine("Error: unkown insert field type:" + value.getClass());
			}
		}
		Map<String, String> results = new HashMap<String, String>();
		try {
			List<Map<String, Object>> selectResult = select(query);
			for (Map<String, Object> row : selectResult) {
				results.put(row.get(keyField).toString(), row.get(valueField).toString());
			}
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		}
		return results;
	}

	public int selectUniqueIdByField(String table, String field, String value) {
		Object result = selectFieldByField(table, field, value, "id");
		if (result == null) {
			return -1;
		}
		return (Integer) result;
	}

	public Object selectField(String table, int id, String field) {
		return selectFieldByField(table, "id", Integer.toString(id), field);
	}

	public Object selectFieldByField(String table, String field, String value, String responseField) {
		SqlQuery query = new SqlQuery(table);
		query.fields.add(responseField);
		if (tables.get(table).getColumns().get(field).equals(Integer.class)) {
			query.where.add(field, Integer.parseInt(value));
		} else if (tables.get(table).getColumns().get(field).equals(Boolean.class)) {
			query.where.add(field, Boolean.parseBoolean(value));
		} else if (tables.get(table).getColumns().get(field).equals(Date.class)) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
			try {
				query.where.add(field, sdf.parse(value));
			} catch (ParseException e) {
				JettyUtils.logThrowable(e);
			}
		} else {
			query.where.add(field, value);
		}
		Object result = null;
		try {
			List<Map<String, Object>> selectResult = select(query);
			if (selectResult.size() > 0) {
				result = selectResult.get(0).get(responseField);
			}
			if (selectResult.size() > 1) {
				JettyUtils.logLine("ERROR: selectUniqueIdByField result is not unique:" + table + " " + field + "=" + value);
				result = null;
			}
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		}

		return result;
	}

	public Integer insert(String table, Object... fields) {
		SqlQuery query = new SqlQuery(table);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		for (int i = 0; i < fields.length; i += 2) {
			Object value = fields[i + 1];
			if (value instanceof String) {
				if (tables.get(table).getColumns().get((String) fields[i]).equals(Date.class)) {
					value = ((String) value).replace("T", " ") + (((String) value).length() < 11 ? " 00:00:00" : "");
					try {
						query.fields.add((String) fields[i], sdf.parse((String) value));
					} catch (ParseException e) {
						JettyUtils.logThrowable(e);
					}
				} else {
					query.fields.add((String) fields[i], (String) value);
				}
			} else if (value instanceof Integer) {
				if (tables.get(table).getColumns().get((String) fields[i]).equals(Boolean.class)) {
					query.fields.add((String) fields[i], ((Integer) value) == 1);
				} else {
					query.fields.add((String) fields[i], (Integer) value);
				}
			} else if (value instanceof Date) {
				query.fields.add((String) fields[i], (Date) value);
			} else if (value instanceof Consts && value.equals(NOW)) {
				query.fields.add((String) fields[i], now);
			} else {
				JettyUtils.logLine("Error: unkown update field type:" + value.getClass());
			}
		}
		return tables.get(table).insertRow(query.fields);

	}

	public Integer insert(SqlQuery query) {
		return insert(query, true);
	}

	public Integer insert(SqlQuery query, Boolean storeInKafka) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

		for (String field : query.fields) {
			if (query.fields.get(field) instanceof String) {
				if (tables.get(query.table).getColumns().get(field).equals(Date.class)) {
					try {
						query.fields.add(field, sdf.parse((String) query.fields.get(field)));
					} catch (ParseException e) {
						JettyUtils.logLine("value:" + query.fields.get(field) + "<-end" + ((String) query.fields.get(field)).length());
						JettyUtils.logThrowable(e);
					}
				}
			} else if (query.fields.get(field).equals(Consts.NOW)) {
				if (tables.get(query.table).getColumns().get(field).equals(Date.class)) {
					query.fields.add(field, new Date());
				}
			}
		}
		Integer insertId = tables.get(query.table).insertRow(query.fields, storeInKafka);
		return insertId;
	}

	public void update(String table, int id, Object... fields) {
		SqlQuery query = new SqlQuery(table);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		for (int i = 0; i < fields.length; i += 2) {
			Object value = fields[i + 1];
			if (value instanceof String) {
				if (tables.get(table).getColumns().get((String) fields[i]).equals(Date.class)) {
					if (((String) value).length() > 0) {
						value = ((String) value).replace("T", " ") + (((String) value).length() < 11 ? " 00:00:00" : "");
						try {
							query.fields.add((String) fields[i], sdf.parse((String) value));
						} catch (Exception e) {
							System.out.println("value:" + value + "<-end" + ((String) value).length());
							JettyUtils.logThrowable(e);
						}
					} else {
						JettyUtils.logLine("Error:" + table + " empty update field(" + fields[i] + ") type date");
					}
				} else {
					query.fields.add((String) fields[i], (String) value);
				}
			} else if (value instanceof Integer) {
				if (tables.get(table).getColumns().get((String) fields[i]).equals(Boolean.class)) {
					query.fields.add((String) fields[i], ((Integer) value) == 1);
				} else {
					query.fields.add((String) fields[i], (Integer) value);
				}
			} else if (value instanceof Date) {
				query.fields.add((String) fields[i], (Date) value);
			} else if (value instanceof Boolean) {
				query.fields.add((String) fields[i], (Boolean) value);
			} else if (value instanceof Consts && value.equals(NOW)) {
				query.fields.add((String) fields[i], now);
			} else {
				JettyUtils.logLine("Error:" + table + " unkown update field type:" + value.getClass());
			}
		}
		query.where.add("id", id);
		tables.get(query.table).updateRows(query.where, query.fields);
	}

	public void update(Integer id, SqlQuery query) {
		update(id, query, true);
	}

	public void update(Integer id, SqlQuery query, Boolean storeInKafka) {
		query.where.add("id", id);
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		for (String field : query.fields) {
			if (query.fields.get(field) instanceof String) {
				if (tables.get(query.table).getColumns().get(field).equals(Date.class)) {
					try {
						query.fields.add(field, sdf.parse((String) query.fields.get(field)));
					} catch (ParseException e) {
						JettyUtils.logLine("value:" + query.fields.get(field) + "<-end" + ((String) query.fields.get(field)).length());
						JettyUtils.logThrowable(e);
					}
				}
			} else if (query.fields.get(field).equals(Consts.NOW)) {
				if (tables.get(query.table).getColumns().get(field).equals(Date.class)) {
					query.fields.add(field, new Date());
				}
			}
		}
		tables.get(query.table).updateRows(query.where, query.fields, storeInKafka);
	}

	public void initRequiredTables(ServletContext context) {
		String[] requiredTables = Configuration.get("xjfw.required.tables").split(",");
		for (String tableName : requiredTables) {
			if (tableName.trim().isEmpty()) {
				continue;
			}
			if (!checkTableIsExistBySQL(tableName)) {
				BufferedReader bufferedReader = null;
				try {
					bufferedReader = new BufferedReader(new FileReader(new File(context.getRealPath("sql/InMemSQL/create_" + tableName + ".InMemSQL"))));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						sb.append(line);
					}
					JSONObject InMemSQL = new JSONObject(sb.toString());

					JSONArray columnsArray = InMemSQL.optJSONArray("columns");
					Map<String, Class<?>> columns = new HashMap<String, Class<?>>();
					for (int i = 0; i < columnsArray.length(); i++) {
						JSONObject column = columnsArray.optJSONObject(i);
						Class<?> cls = Class.forName(column.optString("JavaClassName"));
						columns.put(column.optString("name"), cls);
					}
					createTable(InMemSQL.optString("tableName"), columns);
					JSONArray rowsArray = InMemSQL.optJSONArray("rows");
					SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
					if (rowsArray != null) {
						for (int i = 0; i < rowsArray.length(); i++) {
							JSONObject row = rowsArray.optJSONObject(i);
							SqlQuery query = new SqlQuery(InMemSQL.optString("tableName"));
							for (String field : JSONObject.getNames(row)) {
								Object type = columns.get(field);
								Object value = null;
								if (type.equals(String.class)) {
									value = row.optString(field);
								} else if (type.equals(Integer.class)) {
									value = Integer.parseInt(row.optString(field));
								} else if (type.equals(Boolean.class)) {
									value = Boolean.parseBoolean(row.optString(field));
								} else if (type.equals(Date.class)) {
									value = sdf.parse(row.optString(field));
								} else {
									JettyUtils.logLine("Error: unkown field type:" + type);
								}
								query.fields.add(field, value);
							}

							insert(query);
						}
					}

				} catch (Throwable throwable) {
					JettyUtils.logLine("sql/InMemSQL/create_" + tableName + ".InMemSQL");
					JettyUtils.logThrowable(throwable);
				} finally {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (IOException e) {
							JettyUtils.logThrowable(e);
						}
					}
				}
			}
		}


	}

	public boolean checkTableIsExistBySQL(String table) {
		return ifTableExists(table);

	}

	@Override
	public void delete(String table, int id) {
		update(table, id, "deleted", true);

	}


}
