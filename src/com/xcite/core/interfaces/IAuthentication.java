package com.xcite.core.interfaces;

import javax.servlet.http.HttpServletRequest;

public interface IAuthentication {
	public boolean authenticate(HttpServletRequest request);

	public boolean useUnauthorizedResponseIfRequired();
}
