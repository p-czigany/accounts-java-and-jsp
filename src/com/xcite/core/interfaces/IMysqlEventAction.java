package com.xcite.core.interfaces;

import java.util.List;

public interface IMysqlEventAction {
	public abstract List<String> monitoredTables();

	public abstract void processEvent() throws Throwable;
}
