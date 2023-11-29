package com.xcite.core.devtools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xcite.core.utils.Configuration;
import com.xcite.core.utils.RequestTracker;

public class JettyUtils {
	private static boolean connected = false;
	private static boolean enabled = false;
	private static Method getInstanceMethod = null;
	private static Method storeInstanceMethod = null;
	private static Method logLineMethod = null;
	private static Method logContextLineMethod = null;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static void connectMethods() {
		connected = true;
		try {
			Class<?> jettyServerClass = Class.forName("com.xcite.jettyconnector.JettyServer");
			getInstanceMethod = jettyServerClass.getMethod("getInstance", String.class);
			storeInstanceMethod = jettyServerClass.getMethod("storeInstance", String.class, Object.class);
			logLineMethod = jettyServerClass.getMethod("logLine", int.class, String.class);
			logContextLineMethod = jettyServerClass.getMethod("logLine", String.class, String.class);
			enabled = true;
		} catch (Exception exception) {
		}
	}

	public static boolean isEnabled() {
		if (!connected) {
			connectMethods();
		}
		return enabled;
	}

	public static Object getInstance(String name, Object defaultValue) {
		if (!connected) {
			connectMethods();
		}
		if (!enabled) {
			return defaultValue;
		}
		Object result = null;
		try {
			result = getInstanceMethod.invoke(null, name);
		} catch (Exception exception) {
		}
		if (result == null) {
			storeMethod(name, defaultValue);
			return defaultValue;
		}
		return result;
	}

	public static void storeMethod(String name, Object object) {
		if (!connected) {
			connectMethods();
		}
		if (!enabled) {
			return;
		}
		try {
			storeInstanceMethod.invoke(null, name, object);
		} catch (Exception exception) {
		}
	}

	public static void logThrowable(Throwable throwable) {
		if (RequestTracker.getRequestTrackerRequestId() == -1) {
			throwable.printStackTrace();
			return;
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);

		String[] lines = stringWriter.toString().split("\r\n");
		boolean first = true;
		for (String line : lines) {
			if (line.trim().startsWith("at ")) {
				int fileNameStart = line.indexOf("(") + 1;
				String fileName = line.substring(fileNameStart, line.length() - 1);
				String fileClass = line.substring(0, fileNameStart).substring(4, line.substring(0, fileNameStart).lastIndexOf('.'));
				String lineNumber = fileName.substring(fileName.indexOf(":") + 1);
				String logLine = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + line.substring(0, fileNameStart) + "<a href='javascript:window.postMessage({\"open\":\"" + fileClass + ":" + lineNumber + "\"}, \"*\");'>" + fileName + "</a>)";
				if (!line.trim().startsWith("at com.xcite") && !line.trim().startsWith("at com.ingame")) {
					logLine = "<font color=\"grey\">" + logLine + "</font>";
				}
				logLine(logLine, first);
			} else {
				logLine(line, first);
			}
			first = false;
			if (line.trim().startsWith("at org.eclipse.jetty")) {
				break;
			}
		}
	}

	public static void logLine(String line) {
		logLine(line, true);
	}

	public static void logLine(String line, boolean timestamp) {
		if (timestamp) {
			line = "[" + sdf.format(new Date()) + "] " + line;
		}
		if (RequestTracker.getRequestTrackerRequestId() == -1) {
			System.out.println(line);
			return;
		}
		if (!connected) {
			connectMethods();
		}
		if (!enabled) {
			return;
		}
		try {
			if (RequestTracker.getRequestTrackerRequestId() == 0) {
				logContextLineMethod.invoke(null, RequestTracker.contextName.toLowerCase(), line);
			} else {
				logLineMethod.invoke(null, RequestTracker.getRequestTrackerRequestId(), line);
			}
		} catch (Exception exception) {
		}
	}

	public static void logSQL(String query) {
		if ("true".equals(Configuration.get("xjfw.mysql.loging.enabled"))) {
			logLine("[SQL]" + query);
		}
	}
}
