package com.xcite.core.utils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xcite.core.devtools.JettyUtils;

public class RequestTracker {
	private static class Request {
		public int requestId = -1;
		public HttpServletRequest httpRequest;
		HttpServletResponse httpResponse;
		public Map<String, Object> sessionMap = null;
		public Connection connection = null;
		public StringBuilder logbuilder = null;
		public Boolean useOfflineData = false;
		public Map<String, Object> authenticatedUserDetails;

	}

	private static boolean useKafka;

	private static ServletContext context;

	public static ServletContext getContext() {
		return context;
	}

	public static void setContext(ServletContext context) {
		RequestTracker.context = context;
	}

	public static boolean getUseKafka() {
		return useKafka;
	}

	public static void setUseKafka(boolean useKafka) {
		RequestTracker.useKafka = useKafka;
	}

	public static boolean getUseOfflineData() {

		Request request = getRequest();
		return request.useOfflineData;
	}

	public static void setUseOfflineData(boolean useOfflineDataBoolean) {

		Request request = getRequest();
		request.useOfflineData = useOfflineDataBoolean;

	}

	public static void appendToLog(String s) {
		Request request = getRequest();
		if (request.logbuilder == null) {
			request.logbuilder = new StringBuilder();
		}
		request.logbuilder.append(s + "\r\n");
	}

	public static String getLog() {
		Request request = getRequest();
		if (request != null && request.logbuilder != null) {
			return request.logbuilder.toString();
		}
		return null;
	}

	public static String contextName;

	private static Map<Long, Request> table = new HashMap<Long, Request>();

	private static Request getRequest() {
		Request request = table.get(Thread.currentThread().getId());
		if (request == null) {
			request = new Request();
			table.put(Thread.currentThread().getId(), request);
		}
		return request;
	}

	public static void setRequestTrackerRequestId(int requestId) {
		setRequestTrackerRequestId(requestId, null, null);
	}

	public static void setRequestTrackerRequestId(int requestId, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		Request request = getRequest();
		request.requestId = requestId;
		request.httpRequest = httpRequest;
		request.httpResponse = httpResponse;
	}

	public static int getRequestTrackerRequestId() {
		Request request = getRequest();
		return request.requestId;
	}

	public static void removeRequestTracker() {
		try {
			Request request = table.get(Thread.currentThread().getId());
			if (request.sessionMap != null) {
				SessionManager.setSessionMap(request.httpRequest, request.sessionMap);
			}
			table.remove(Thread.currentThread().getId());
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		}
	}



	public static int getSessionInt(String key) {
		Request request = table.get(Thread.currentThread().getId());
		ensureSession(request, false);
		if (request.sessionMap == null) {
			return 0;
		}
		Integer value = (Integer) request.sessionMap.get(key);
		if (value == null) {
			return 0;
		}
		return value;
	}

	public static String getSessionString(String key) {
		Request request = table.get(Thread.currentThread().getId());
		ensureSession(request, false);
		if (request.sessionMap == null) {
			return null;
		}
		return (String) request.sessionMap.get(key);
	}

	public static void setSessionInt(String key, int value) {
		Request request = table.get(Thread.currentThread().getId());
		ensureSession(request, true);
		request.sessionMap.put(key, new Integer(value));
	}

	public static String getSessionKey() {
		Request request = table.get(Thread.currentThread().getId());
		return (String) request.httpRequest.getAttribute("sessionKey");
	}

	public static HttpServletRequest getHTTPRequest() {
		Request request = table.get(Thread.currentThread().getId());
		return request.httpRequest;
	}

	public static void setSessionString(String key, String value) {
		Request request = table.get(Thread.currentThread().getId());
		ensureSession(request, true);
		request.sessionMap.put(key, value);
	}

	public static void clearSession() {
		Request request = table.get(Thread.currentThread().getId());
		ensureSession(request, false);
		if (request.sessionMap != null) {
			SessionManager.clearSession(request.httpRequest);
			request.sessionMap = null;
		}
	}

	private static void ensureSession(Request request, boolean create) {
		if (request.sessionMap == null) {
			request.sessionMap = SessionManager.getSessionMap(request.httpRequest, request.httpResponse, create);
		}
	}

	public static void writeHeaders() {
		Request request = table.get(Thread.currentThread().getId());
		request.httpResponse.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate"); // HTTP 1.1
		request.httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
		request.httpResponse.setDateHeader("Expires", 0); // Proxies.
		request.httpResponse.setCharacterEncoding("UTF-8");
	}

	public static void setAuthenticatedUserDetails(String tokenType, int loginType, String userId, String authToken) {
		Request request = getRequest();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tokenType", tokenType);
		map.put("loginType", loginType);
		map.put("userId", userId);
		map.put("authToken", authToken);
		request.authenticatedUserDetails = map;
	}

	public static Map<String, Object> getAuthenticatedUserDetails() {
		Request request = getRequest();
		return request.authenticatedUserDetails;

	}
}
