package com.xcite.core.devtools;

public class Debug {
	public static void check(boolean condition) {
		if (!condition) {
			System.err.println("Debug check failed");
			try {
				throw new Throwable();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void event(String event) {
		System.out.println("Debug event:" + event);
	}

	public static void warning(String event) {
		System.out.println("Warning:" + event);
	}

	public static void error(String event) {
		System.err.println("Error:" + event);
	}
}
