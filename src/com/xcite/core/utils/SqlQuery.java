package com.xcite.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xcite.core.utils.DBInterface.Consts;

public class SqlQuery {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public class CustomFieldList implements Iterable<String> {
		protected Map<String, Object> fields = new LinkedHashMap<String, Object>();

		public void add(String... args) {
			for (int i = 0; i < args.length; i += 2) {
				if (i + 1 < args.length) {
					fields.put(args[i], args[i + 1]);
				} else {
					fields.put(args[i], null);
				}
			}
		}

		public void add(String field, int value) {
			fields.put(field, Integer.toString(value));
		}

		public void add(String field, Object value) {
			fields.put(field, value);
		}

		public void add(String field, Consts now) {
			fields.put(field, now);
		}

		public Object get(String field) {
			return fields.get(field);
		}

		public Iterator<String> iterator() {
			return fields.keySet().iterator();
		}

		public int count() {
			return fields.size();
		}

		@Override
		public String toString() {
			return fields.toString();
		}
	}

	public class CustomWhereList {
		private List<SqlCondition> list = new LinkedList<SqlCondition>();

		public void add(String field, String value) {
			list.add(new SqlCondition(field, "=", value));
		}

		public int count() {
			return list.size();
		}

		public SqlCondition get(int index) {
			return list.get(index);
		}

		public void add(String field, Integer value) {
			list.add(new SqlCondition(field, "=", value));
		}

		public void add(String field, Date value) {
			list.add(new SqlCondition(field, "=", value));
		}

		public void add(String field, Boolean value) {
			list.add(new SqlCondition(field, "=", value));
		}

		public void add(String field, String operator, Date value) {
			list.add(new SqlCondition(field, operator, value));
		}

		public void addLike(String field, String value) {
			list.add(new SqlCondition(field, "LIKE", value));
		}

		@Override
		public String toString() {
			return list.toString();
		}

	}

	public class CustomOrderList implements Iterable<String> {
		protected Map<String, Object> orders = new LinkedHashMap<String, Object>();

		public void add(String order) {
			add(order, null);
		}

		public void add(String order, Consts desc) {
			orders.put(order, desc);
		}

		public Object get(String order) {
			return orders.get(order);
		}

		public Iterator<String> iterator() {
			return orders.keySet().iterator();
		}

		public int count() {
			return orders.size();
		}

		@Override
		public String toString() {
			return orders.toString();
		}
	}

	public static class SqlCondition {
		public String field;
		public Object value;
		public String operator;

		public SqlCondition(String field, String operator, Object value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
		}

		@Override
		public String toString() {
			return ("(" + field + "," + operator + "," + value + ")");
		}
	}

	public String table;
	public CustomFieldList fields = new CustomFieldList();
	public CustomWhereList where = new CustomWhereList();
	public CustomOrderList order = new CustomOrderList();
	public int limit = -1;
	public boolean autoDeletedFiltering = true;

	public SqlQuery(String table) {
		this.table = table;
	}

	@Override
	public String toString() {
		return table + "\r\n" + "fields:" + fields + "\r\n" + "where:" + where + "\r\n" + "order:" + order + "\r\n";
	}

	public static SqlCondition equals(String field, Object value) {
		return new SqlCondition(field, "=", value);
	}
}
