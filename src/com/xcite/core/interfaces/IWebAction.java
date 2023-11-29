package com.xcite.core.interfaces;

import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;

public abstract class IWebAction extends IServletAction {
	public Type getType() {
		return Type.WEB;
	}

	public abstract ProcessResult processRequest(ParameterMap parameterMap) throws Throwable;
}
