package com.xcite.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.xcite.core.devtools.JettyUtils;
import com.xcite.core.utils.DBInterface.Consts;

public class SessionManager {
	public static Map<String, Object> getSessionMap(String sessionKey) {
		int sessionId = DataBase.selectUniqueIdByField("session", "key", sessionKey);
		String data = (String) DataBase.selectField("session", sessionId, "data");
		if (data == null) {
			return null;
		}
		JSONObject object = new JSONObject(data);
		return object.toMap();
	}

	public static Map<String, Object> getSessionMap(HttpServletRequest request, HttpServletResponse response, boolean create) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			JettyUtils.logLine("Cookie:" + cookie.getName() + "=" + cookie.getValue());
			if (cookie.getName().equals("XJFW_Session")) {
				int sessionId = DataBase.selectUniqueIdByField("session", "key", cookie.getValue());
				if (sessionId == -1) {
					continue;
				}
				request.setAttribute("sessionId", sessionId);
				request.setAttribute("sessionKey", cookie.getValue());

				SqlQuery updatequery = new SqlQuery("session");
				updatequery.fields.add("lastseen", Consts.NOW);
				DataBase.update(sessionId, updatequery);

				String data = (String) DataBase.selectField("session", sessionId, "data");
				JSONObject object = new JSONObject(data);
				return object.toMap();
			}
		}

		if (!create) {
			return null;
		}

		String sessionKey = UUID.randomUUID().toString().replace("-", "");
		Cookie cookie = new Cookie("XJFW_Session", sessionKey);
		cookie.setHttpOnly(true);

		response.addCookie(cookie);
		JettyUtils.logLine("Setting cookie value:" + cookie.getValue());

		SqlQuery insertquery = new SqlQuery("session");
		insertquery.fields.add("key", sessionKey);
		insertquery.fields.add("data", "{}");
		insertquery.fields.add("lastseen", Consts.NOW);
		int sessionId = DataBase.insert(insertquery);
		request.setAttribute("sessionId", sessionId);
		request.setAttribute("sessionKey", sessionKey);
		return new HashMap<String, Object>();
	}

	public static void setSessionMap(HttpServletRequest request, Map<String, Object> sessionMap) {
		SqlQuery updatequery = new SqlQuery("session");

		JSONObject json = new JSONObject(sessionMap);
		String data = json.toString();
		updatequery.fields.add("data", data);
		updatequery.fields.add("lastseen", Consts.NOW);
		DataBase.update((Integer) request.getAttribute("sessionId"), updatequery);
	}

	public static void clearSession(HttpServletRequest request) {
		DataBase.delete("session", (Integer) request.getAttribute("sessionId"));
	}
}
