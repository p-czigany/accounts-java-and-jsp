package com.xcite.core.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.xcite.core.devtools.JettyUtils;
import com.xcite.core.utils.Configuration;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.RequestTracker;

public class ServletListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent context) {
		try {

			RequestTracker.setContext(context.getServletContext());
			RequestTracker.contextName = context.getServletContext().getInitParameter("webappname");
			RequestTracker.setRequestTrackerRequestId(0);
			new Configuration(context.getServletContext());


			DataBase.initRequiredTables(context.getServletContext());
			JettyUtils.logLine("contextInitialized");

			DataBase.insert("xjfw.configuration", "key", "serviceStartTimeInMs", "value", String.valueOf(System.currentTimeMillis()));

		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
		} finally {
			RequestTracker.removeRequestTracker();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}
}
