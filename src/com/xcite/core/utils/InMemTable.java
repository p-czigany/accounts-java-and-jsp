package com.xcite.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xcite.core.devtools.JettyUtils;
import com.xcite.core.utils.DBInterface.Consts;
import com.xcite.core.utils.InMemMapStore.eTableCleaningRole;
import com.xcite.core.utils.SqlQuery.CustomFieldList;
import com.xcite.core.utils.SqlQuery.CustomOrderList;
import com.xcite.core.utils.SqlQuery.CustomWhereList;

//TODO id,update
//TODO index table 

public class InMemTable {
	public Map<Integer, Map<String, Object>> table;
	private String name;
	private Map<String, Class<?>> columns;
	private Integer index = null;
	private eTableCleaningRole cleaningRole = eTableCleaningRole.CLEAN_ALL_DELETED_BYTABLE;
	private String cleanExpireColumn = null;

	public InMemTable(String name, Map<String, Class<?>> columns) throws Exception {
		if (name == null) {
			throw new NullPointerException("name is null");
		}
		if (columns == null) {
			throw new NullPointerException("columns is null");
		}
		if (columns.isEmpty()) {
			throw new Exception("columns is empty");
		}
		this.name = name;
		this.table = new ConcurrentHashMap<Integer, Map<String, Object>>();
		this.columns = columns;
		this.index = 0;
	}

	public synchronized Integer insertRow(Object... fields) {
		Integer id = null;
		if (!checkColumns(fields)) {
			return id;
		}
		Map<String, Object> row = new HashMap<String, Object>();
		Date now = new Date();
		boolean hasDeletedColumn = false;
		for (int i = 0; i < fields.length; i += 2) {
			Object value = fields[i + 1];
			if (value instanceof Consts && value.equals(DBInterface.NOW)) {
				row.put((String) fields[i], now);
			} else {
				row.put((String) fields[i], value);
			}

			if ("deleted".equals(fields[i])) {
				hasDeletedColumn = true;
			}
		}

		if (!hasDeletedColumn) {
			row.put("deleted", false);
		}

		id = ++index;
		row.put("id", id);
		table.put(id, row);
		return id;
	}

	public synchronized Integer insertRow(CustomFieldList fields) {
		return insertRow(fields, true);
	}

	public synchronized Integer insertRow(CustomFieldList fields, Boolean storeInKafka) {
		Integer id = null;
		if (!checkColumns(fields)) {
			return id;
		}
		Map<String, Object> row = new HashMap<String, Object>();

		boolean hasDeletedColumn = false;
		for (String field : fields) {
			row.put(field, fields.get(field));
			if ("deleted".equals(field)) {
				hasDeletedColumn = true;
			}
		}

		if (!hasDeletedColumn) {
			row.put("deleted", false);
		}

		if (!storeInKafka && row.get("id") != null) {
			id = (Integer) row.get("id");
			if (id > index) {
				index = id;
			}

		} else {
			id = ++index;
		}
		row.put("id", id);
		table.put(id, row);
		return id;
	}

	private synchronized Integer updateRow(Integer id, Object... fields) {
		Integer updatedRowCount = 0;
		if (checkColumns(fields)) {
			for (int i = 0; i < fields.length; i += 2) {
				table.get(id).put((String) fields[i], fields[i + 1]);
			}
			updatedRowCount++;
		}
		return updatedRowCount;
	}

	private synchronized Integer updateRow(Integer id, CustomFieldList fields, Boolean storeInKafka) {
		Integer updatedRowCount = 0;
		if (checkColumns(fields)) {
			for (String field : fields) {
				table.get(id).put(field, fields.get(field));
			}
			updatedRowCount++;
		}
		return updatedRowCount;
	}

	public synchronized Integer updateRows(CustomWhereList conditions, Object... fields) {
		Integer updatedRowCount = 0;
		for (Integer id : table.keySet()) {
			if (checkConditions(conditions, table.get(id))) {
				updatedRowCount += updateRow(id, fields);
			}
		}
		return updatedRowCount;
	}

	public synchronized Integer updateRows(CustomWhereList conditions, CustomFieldList fields) {
		return updateRows(conditions, fields, true);
	}

	public synchronized Integer updateRows(CustomWhereList conditions, CustomFieldList fields, Boolean storeInKafka) {
		Integer updatedRowCount = 0;
		for (Integer id : table.keySet()) {
			if (checkConditions(conditions, table.get(id))) {
				updatedRowCount += updateRow(id, fields, storeInKafka);
			}
		}
		return updatedRowCount;
	}

	private boolean checkConditions(CustomWhereList conditions, Map<String, Object> row) {
		Boolean conditionValue = true;
		for (int i = 0; i < conditions.count() && conditionValue; i++) {
			if (row.get(conditions.get(i).field) == null) {
				if (columns.get(conditions.get(i).field) == null && !"true".equals(Configuration.get("disable.query.logging"))) {
					JettyUtils.logLine(name + "?" + conditions.get(i).field + "=null");
				}
				return false;
			}
			if (columns.get(conditions.get(i).field).equals(Boolean.class) && conditions.get(i).value instanceof Integer) {
				conditions.get(i).value = (Integer) conditions.get(i).value == 1;
			}
			conditionValue = conditionValue & row.get(conditions.get(i).field).equals(conditions.get(i).value);
			if (!conditionValue && row.get(conditions.get(i).field).toString().equals(conditions.get(i).value.toString()) && !"true".equals(Configuration.get("disable.query.logging"))) {
				JettyUtils.logLine(name + "?" + row.get(conditions.get(i).field) + "!=" + conditions.get(i).value);
			}
		}
		return conditionValue;
	}

	public List<Map<String, Object>> selectRows(CustomWhereList conditions, CustomOrderList order) {

		boolean hasDeletedCondition = false;
		for (int i = 0; i < conditions.count(); i++) {
			if ("deleted".equals(conditions.get(i).field)) {
				hasDeletedCondition = true;
			}
		}

		if (!hasDeletedCondition) {
			conditions.add("deleted", false);
		}

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Integer id : table.keySet()) {
			if (checkConditions(conditions, table.get(id))) {
				result.add(new HashMap<String, Object>(table.get(id)));
			}
		}
		Collections.sort(result, new InMemTableComperator(order));
		return result;
	}

	class InMemTableComperator implements Comparator<Map<String, Object>> {
		private InMemTableComperator() {
		};

		public InMemTableComperator(CustomOrderList orderList) {
			this.orderList = orderList;
		}

		private CustomOrderList orderList = null;

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			for (String column : orderList) {
				if (o2.get(column) == null && o1.get(column) == null) {
					//next iterate
				} else {
					if (orderList.get(column) instanceof InMemMapStore.Consts && orderList.get(column).equals(Consts.DESC)) {
						Map<String, Object> t = o1;
						o1 = o2;
						o2 = t;
					}
					if (o2.get(column) == null) {
						return -1;
					}
					if (o1.get(column) == null) {
						return 1;
					}
					if (columns.get(column).equals(Boolean.class)) {
						if (((Boolean) o1.get(column)).compareTo(((Boolean) o2.get(column))) == 0) {
							//next iterate
						} else {
							return ((Boolean) o1.get(column)).compareTo(((Boolean) o2.get(column)));
						}
					} else if (columns.get(column).equals(Date.class)) {
						if (((Date) o1.get(column)).compareTo(((Date) o2.get(column))) == 0) {
							//next iterate
						} else {
							return ((Date) o1.get(column)).compareTo(((Date) o2.get(column)));
						}
					} else if (columns.get(column).equals(String.class)) {
						if (((String) o1.get(column)).compareTo(((String) o2.get(column))) == 0) {
							//next iterate
						} else {
							return ((String) o1.get(column)).compareTo(((String) o2.get(column)));
						}
					} else if (columns.get(column).equals(Integer.class)) {
						if (((Integer) o1.get(column)).compareTo(((Integer) o2.get(column))) == 0) {
							//next iterate
						} else {
							return ((Integer) o1.get(column)).compareTo(((Integer) o2.get(column)));
						}
					} else if (columns.get(column).equals(Float.class)) {
						if (((Float) o1.get(column)).compareTo(((Float) o2.get(column))) == 0) {
							//next iterate
						} else {
							return ((Float) o1.get(column)).compareTo(((Float) o2.get(column)));
						}
					} else if (columns.get(column).equals(Double.class)) {
						if (((Double) o1.get(column)).compareTo(((Double) o2.get(column))) == 0) {
							//next iterate
						} else {
							return ((Double) o1.get(column)).compareTo(((Double) o2.get(column)));
						}
					}

				}
			}
			return 0;
		}

	}

	protected Boolean checkColumns(Object... fields) {
		if (fields.length % 2 == 0) {
			for (int i = 0; i < fields.length; i += 2) {
				if (columns.get(fields[i]) == null && !"true".equals(Configuration.get("disable.query.logging"))) {
					JettyUtils.logLine(name + " table check columns failed at name(" + fields[i] + ")");
					return false;
				}
				if (!columns.get(fields[i]).equals(fields.getClass()) && !"true".equals(Configuration.get("disable.query.logging"))) {
					JettyUtils.logLine(name + " table check columns failed at type(" + fields[i] + "," + columns.get(fields[i]) + ")");
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	protected Boolean checkColumns(CustomFieldList fields) {
		for (String field : fields) {
			if (columns.get(field) == null && !"true".equals(Configuration.get("disable.query.logging"))) {
				JettyUtils.logLine(name + " table check columns failed at name(" + field + ")");
				return false;
			}
			if (!columns.get(field).equals(fields.get(field).getClass()) && !"true".equals(Configuration.get("disable.query.logging"))) {
				JettyUtils.logLine(name + " table check columns failed at type(" + field + "," + columns.get(field) + ")");
				return false;
			}
		}
		return true;
	}

	public Map<String, Class<?>> getColumns() {

		return columns;
	}

	public synchronized void clean() {
		switch (cleaningRole) {
		case CLEAN_ALL_DELETED_BYTABLE:

			String tablesToClean = Configuration.get("xjfw.tablecleaner.tables.toclean");
			List<String> tablesToCleanList = Arrays.asList(tablesToClean.split(","));

			if (tablesToCleanList.contains(name)) {
				for (Integer id : table.keySet()) {
					if (Boolean.TRUE.equals(table.get(id).get("deleted"))) {
						table.remove(id);
					}
				}
			}
			break;
		case CLEAN_ALL_DELETED:
			for (Integer id : table.keySet()) {
				if (Boolean.TRUE.equals(table.get(id).get("deleted"))) {
					table.remove(id);
				}
			}
			break;
		case CLEAN_AFTER_DATE:
			Date expireDate = new Date(new Date().getTime() - (1000 * 60 * 60 * 24 * 7));
			for (Integer id : table.keySet()) {
				if (Boolean.TRUE.equals(table.get(id).get("deleted")) && (expireDate.after((Date) table.get(id).get(cleanExpireColumn)))) {
					table.remove(id);
				}
			}
			break;

		case CLEAN_UNALLOWED:
			//do nothing
			break;

		default:
			//do nothing
			break;
		}

	}

	public void truncate() {
		table.clear();
	}

}
