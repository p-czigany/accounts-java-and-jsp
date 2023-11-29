package com.xcite.core.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.servlet.ServletContext;

public class Configuration {
	private static Configuration instance = null;
	private Properties properties;

	public static boolean isDebug() {
		return true;
	}

	public Configuration(ServletContext context) throws Exception {
		// Debug.check(instance == null);
		instance = this;

		properties = new Properties();
		properties.load(new FileReader(new File(context.getInitParameter("configpath"))));
	}

	public Configuration(String configpath) throws Exception {
		instance = this;
		properties = new Properties();
		properties.load(new FileReader(new File(configpath)));
	}

	public static boolean isEnabled(String name) {
		return true;
	}

	public static String get(String name) {
		return (String) instance.properties.get(name);
	}

	public static String getEnvironmentVariable(String name) {
		String r = null;
		r = System.getenv(name);

		return r;
	}

}
