package com.xcite.core.servlet;

import java.util.HashMap;

import com.xcite.core.devtools.Debug;

public class ProcessResult {
	public enum ProcessResultType {
		FORWARD, DISPATCH, DIRECT, JSON
	}

	public ProcessResultType type;
	public String data;

	private HashMap<String, Object> parameters = new HashMap<String, Object>();

	private ProcessResult(ProcessResultType type) {
		this(type, null);
	}

	private ProcessResult(ProcessResultType type, String data) {
		this.type = type;
		this.data = data;
	}

	public static ProcessResult createProcessForwardResult(String url) {
		return new ProcessResult(ProcessResultType.FORWARD, url);
	}

	public static ProcessResult createProcessDispatchResult(String url) {
		return new ProcessResult(ProcessResultType.DISPATCH, url);
	}

	public static ProcessResult createProcessJSONResult(String response) {
		return new ProcessResult(ProcessResultType.JSON, response);
	}

	public boolean hasStringParameter(String name) {
		Object value = parameters.get(name);
		if (value == null || (value instanceof String && ((String) value).isEmpty())) {
			return false;
		}
		return true;
	}

	public String getStringParameter(String name) {
		Object value = parameters.get(name);
		if (value == null) {
			return "";
		}
		if (!(value instanceof String)) {
			Debug.error("getStringParameter value is not String");
		}
		return (String) value;
	}

	public void addData(String name, Object value) {
		parameters.put(name, value);
	}

	public Object getObject(String name) {
		return parameters.get(name);
	}

	public static ProcessResult createProcessDirectResult() {
		return new ProcessResult(ProcessResultType.DIRECT);
	}
}
