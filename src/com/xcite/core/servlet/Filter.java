package com.xcite.core.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xcite.core.devtools.JettyUtils;
import com.xcite.core.interfaces.IJSONAction;
import com.xcite.core.interfaces.IServletAction;
import com.xcite.core.interfaces.IServletAction.Type;
import com.xcite.core.interfaces.IWebAction;
import com.xcite.core.servlet.ProcessResult.ProcessResultType;
import com.xcite.core.utils.Authentication;
import com.xcite.core.utils.Authentication.AuthenticationResult;
import com.xcite.core.utils.Configuration;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.RequestTracker;

public class Filter implements javax.servlet.Filter {
	private Map<String, String> urlmapping;
	private List<String> passthroughExtensions;
	private List<String> passthroughFolders;

	private void processActionRequest(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String requestUri = request.getRequestURI();
		IServletAction action = null;
		String className = null;
		if (urlmapping.containsKey(requestUri)) {
			className = urlmapping.get(requestUri).replace('/', '.');
		} else if (requestUri.startsWith("/admin")) {
			String unauthenticatedUri = (String) DataBase.selectFieldByField("xjfw.configuration", "key", "xjfw.filter.loginredirect.uri", "value");
			if (unauthenticatedUri != null) {
				response.setStatus(HttpServletResponse.SC_SEE_OTHER);
				response.setHeader("Location", unauthenticatedUri);
			}
			return;
		}
		if ("true".equals(Configuration.get("xjfw.core.filter.debug.request.urlmapping"))) {
			JettyUtils.logLine("[DEBUG_REQUEST] URL:" + request.getRequestURL() + " className: " + className + " urlmapping: " + urlmapping);
		}
		try {


			Class<?> cls = Class.forName(className);

			action = (IServletAction) cls.newInstance();
			if (action == null) {
				JettyUtils.logThrowable(new Throwable("No action for URI:" + requestUri));
				// TODO log invalid request
				return;
			}
		} catch (Exception e) {
			JettyUtils.logThrowable(new Throwable("No action for URI:" + requestUri));
		}
		if (action != null && action.getAuthenticaionClassname() != null) {
			AuthenticationResult authenticationResult = Authentication.authenticateAction(action.getAuthenticaionClassname());
			if (authenticationResult == AuthenticationResult.NOLOGIN) {
				String unauthenticatedUri = (String) DataBase.selectFieldByField("xjfw.configuration", "key", "xjfw.filter.loginredirect.uri", "value");
				if (unauthenticatedUri != null) {
					response.setStatus(HttpServletResponse.SC_SEE_OTHER);
					response.setHeader("Location", unauthenticatedUri);
				}
				return;
			} else if (authenticationResult == AuthenticationResult.FAILED) {
				String unauthenticatedUri = (String) DataBase.selectFieldByField("xjfw.configuration", "key", "xjfw.filter.unauthenticated.uri", "value");
				if (unauthenticatedUri != null) {
					response.setStatus(HttpServletResponse.SC_SEE_OTHER);
					response.setHeader("Location", unauthenticatedUri);
				}
				return;
			} else if (authenticationResult == AuthenticationResult.UNAUTHORIZED) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		if (action.getType() == Type.JSON) {

			if (request.getParameter("offlinedata") != null || "true".equals(RequestTracker.getSessionString("offlinedata"))) {
				RequestTracker.setUseOfflineData(true);
			} else {
				RequestTracker.setUseOfflineData(false);
			}

			if ("off".equals((String) DataBase.selectFieldByField("xjfw.configuration", "key", "xjfw.site.config.mode", "value"))) {
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return;
			}
			String jsonData = null;
			
				if (Configuration.isDebug() && request.getParameter("data") != null) {
					jsonData = request.getParameter("data");
				} else {
					try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"))) {
						StringBuilder jsonDataTemp = new StringBuilder();
						String line = inputStream.readLine();
						while (line != null) {
							jsonDataTemp.append(line);
							line = inputStream.readLine();
						}
						try {
							jsonData = URLDecoder.decode(jsonDataTemp.toString(), "UTF-8");
						} catch (IllegalArgumentException exception) {
							jsonData = jsonDataTemp.toString();
							//Ignore if decoder fails
							//TODO is decoding necessary here?
						}
					}
				}
			
			String logString = ("[" + request.getServerName() + "]" + "[API REQUEST] " + Thread.currentThread().getId() + " " + requestUri + " " + jsonData + "\r\n");
			JSONObject requestJson = null;
			if (jsonData != null && !jsonData.isEmpty()) {
				if (jsonData.startsWith("{") && jsonData.endsWith("}")) {
					requestJson = new JSONObject(jsonData);
				} else if (jsonData.startsWith("[") && jsonData.endsWith("]")) {
					requestJson = new JSONObject();
					requestJson.put("requestJsonArray", new JSONArray(jsonData));
				}
			}
			JSONObject responseJson = ((IJSONAction) action).processRequest(new ParameterMap(request, null, action), requestJson);
			if (RequestTracker.getLog() != null) {
				logString += (RequestTracker.getLog());
			}
			logString += ("[" + request.getServerName() + "]" + "[API RESPONSE] " + Thread.currentThread().getId() + " " + requestUri + " " + responseJson.toString());
			if (action.requireJettyLog()) {
				JettyUtils.logLine(logString);
			}
			int statusCode = ((IJSONAction) action).getStatusCode();
			if (statusCode != -1) {
				response.setStatus(statusCode);
			}
			if (responseJson != null) {
				RequestTracker.writeHeaders();
				response.setContentType("application/json");
				if (request.getParameter("sendlog") != null) {
					response.setContentType("application/json");
					responseJson.put("log", RequestTracker.getLog());

				}
				if (responseJson.has("responseJsonArray") && responseJson.optBoolean("returnAsJONArray", false)) {
					response.getWriter().write(responseJson.getJSONArray("responseJsonArray").toString());
				} else {
					response.getWriter().write(responseJson.toString());
				}
			}
		} else if (action.getType() == Type.WEB) {

			if (request.getParameter("offlinedata") != null || "true".equals(RequestTracker.getSessionString("offlinedata"))) {
				RequestTracker.setUseOfflineData(true);
			} else {
				RequestTracker.setUseOfflineData(false);
			}

			ProcessResult result = ((IWebAction) action).processRequest(new ParameterMap(request, response, action));
			if (result.type == ProcessResultType.DIRECT) {
				return;
			} else if (result.type == ProcessResultType.JSON) {
				RequestTracker.writeHeaders();
				response.setContentType("application/json");
				response.getWriter().write(result.data);
			} else if (result.type == ProcessResultType.FORWARD) {
				response.setStatus(HttpServletResponse.SC_SEE_OTHER);
				response.setHeader("Location", result.data);
			} else {
				RequestTracker.writeHeaders();
				request.setAttribute("processResult", result);
				request.getServletContext().getRequestDispatcher("/" + result.data + ".jsp").forward(request, response);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;


		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
	
			RequestTracker.setRequestTrackerRequestId(-1, httpRequest, httpResponse);
	
			String requestUri = httpRequest.getRequestURI();
			boolean passthrough = false;
			for (String extension : passthroughExtensions) {
				if (requestUri.endsWith(extension)) {
					passthrough = true;
					break;
				}
			}
			for (String folder : passthroughFolders) {
				if (requestUri.startsWith(folder)) {
					passthrough = true;
					break;
				}
			}
			if (passthrough) {
				if (!requestUri.endsWith(".jsp") || request.getAttribute("processResult") != null) {
					RequestTracker.writeHeaders();
					chain.doFilter(request, response);
				}
			} else {
					processActionRequest(httpRequest, httpResponse);
			}
			if (httpRequest.getSession(false) != null) {
				throw new RuntimeException("Somewhere request.getSession() was called");
			}
		} catch (Throwable throwable) {
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			RequestTracker.writeHeaders();
			if (request.getParameter("sendlog") != null) {
				response.setContentType("application/json");
				JSONObject j = new JSONObject();
				j.put("log", RequestTracker.getLog());
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				throwable.printStackTrace(printWriter);
				j.put("exception", stringWriter.toString());
				response.getWriter().write(j.toString());
			} else {
				request.getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
			}
			JettyUtils.logThrowable(throwable);
		} finally {
			RequestTracker.removeRequestTracker();
		}
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			RequestTracker.setRequestTrackerRequestId(0);
			urlmapping = DataBase.selectMapIfTableExists("xjfw.core.urlmapping", "url", "action");			
			urlmapping.put("/admin/db/gettables", "com.xcite.core.db.GetTables");
			urlmapping.put("/admin/db/getcolumns", "com.xcite.core.db.GetColumns");
			urlmapping.put("/admin/db/getrows", "com.xcite.core.db.GetRows");
			urlmapping.put("/admin/db/modifytable", "com.xcite.core.db.ModifyTable");
			passthroughExtensions = DataBase.selectListIfTableExists("xjfw.core.filter.passthrough.extensions", "extension");			
			passthroughFolders = DataBase.selectListIfTableExists("xjfw.core.filter.passthrough.folders", "folder");
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		} finally {
			RequestTracker.removeRequestTracker();
		}
	}


	@Override
	public void destroy() {
	}

}
