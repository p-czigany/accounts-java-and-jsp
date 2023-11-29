package com.xcite.core.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.xcite.core.devtools.Debug;
import com.xcite.core.devtools.JettyUtils;
import com.xcite.core.interfaces.IServletAction;

public class ParameterMap {
	private HashMap<String, Object> parameters = new HashMap<String, Object>();

	public ParameterMap(HttpServletRequest request) {
		this(request, null, null);
	}

	public ParameterMap(HttpServletRequest request, HttpServletResponse response, IServletAction action) {
		Debug.check(parameters.isEmpty());
		if (!ServletFileUpload.isMultipartContent(request)) {
			Enumeration<String> getParameterNames = request.getParameterNames();

			String uri = request.getRequestURI();
			String queryString = request.getQueryString();
			if (uri != null && uri.contains("jsonservice") && queryString != null && queryString.startsWith("uri=")) {
				queryString = queryString.replace("uri=", "");
				parameters.put("uri", queryString);
			} else {
				while (getParameterNames.hasMoreElements()) {
					String key = (String) getParameterNames.nextElement();
					String value = request.getParameter(key);
					parameters.put(key, value);
				}
			}
		} else {
			FileOutputStream fileOutputStream = null;
			InputStream input = null;
			try {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					String key = item.getFieldName();
					input = item.openStream();
					if (item.isFormField()) {
						String value = Streams.asString(input, "UTF-8");
						parameters.put(key, value);
					} else {

						File tempFile = File.createTempFile("xjfw_parametermap_temp", item.getName() + "_" + System.currentTimeMillis());
						fileOutputStream = new FileOutputStream(tempFile);
						int maxBufferSize = 1 * 1024 * 1024;

						byte[] buffer = new byte[maxBufferSize];

						int bytesRead = input.read(buffer, 0, maxBufferSize);
						while (bytesRead > 0) {
							fileOutputStream.write(buffer, 0, bytesRead);
							bytesRead = input.read(buffer, 0, maxBufferSize);
						}
						parameters.put(key, new FileInputStream(tempFile.getPath()));
						parameters.put(key + "_name", item.getName());
						parameters.put(key + "_type", item.getContentType());
					}
				}
			} catch (Throwable throwable) {
				JettyUtils.logThrowable(throwable);

			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						JettyUtils.logThrowable(e);
					}
				}
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						JettyUtils.logThrowable(e);
					}
				}
			}
		}
		if (request != null) {
			parameters.put("__xjfw_serverName", request.getServerName());
		}
		if (action == null) {
			return;
		}
		if (request != null && action.requireRequestURI()) {
			parameters.put("__xjfw_requestURI", request.getRequestURI());
		}
		if (action.requireResponseObject()) {
			parameters.put("__xjfw_responseObject", response);
		}
		if (request != null && action.requireRequestIP()) {
			parameters.put("__xjfw_requestIP", request.getRemoteAddr());
		}
		if (request != null && action.requireAuthorizationHeader()) {
			parameters.put("__xjfw_authorizationHeader", request.getHeader("Authorization"));
		}

	}

	public String get(String name) {
		return (String) parameters.get(name);
	}

	public void put(String name, Object ob) {
		parameters.put(name, ob);
	}

	public Object getObject(String name) {
		return parameters.get(name);
	}

}
