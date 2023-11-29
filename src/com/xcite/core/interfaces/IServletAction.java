package com.xcite.core.interfaces;

public abstract class IServletAction {
	public enum Type {
		WEB, JSON
	}

	abstract public Type getType();

	public boolean requireRequestURI() {
		return false;
	}

	public boolean requireResponseObject() {
		return false;
	}

	abstract public String getAuthenticaionClassname();

	public boolean requireRequestIP() {
		return false;
	}

	public boolean requireAuthorizationHeader() {
		return false;
	}

	public boolean requireJettyLog() {
		return true;
	}
}
