package com.xcite.core.utils;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.xcite.core.devtools.JettyUtils;
import com.xcite.core.interfaces.IAuthentication;

public class Authentication {
	public static enum LoginResult {
		SUCCEEDED, PASSWORD_FAILED, NOT_ACTIVATED/*, BLOCKED*/
	}

	public static enum AuthenticationResult {
		NOLOGIN, FAILED, OK, UNAUTHORIZED
	}

	public static int getAccountIdByEmail(String email) {
		return DataBase.selectUniqueIdByField("xjfw.account", "email", email);
	}

	public static int createAccount(String email, String password) {
		SqlQuery query = new SqlQuery("xjfw.account");
		query.fields.add("email", email);
		query.fields.add("password", sha256(password));
		return DataBase.insert(query);
	}

	public static void activateAccount(int accountId) {
		DataBase.update("xjfw.account", accountId, "activated", 1);
		RequestTracker.setSessionInt("accountId", accountId);
	}

	public static LoginResult login(int accountId, String password) {
		if (!((String) DataBase.selectField("xjfw.account", accountId, "password")).equals(sha256(password))) {
			return LoginResult.PASSWORD_FAILED;
		}
		if (!(Boolean) DataBase.selectField("xjfw.account", accountId, "activated")) {
			return LoginResult.NOT_ACTIVATED;
		}
		//TODO block
		RequestTracker.setSessionInt("accountId", accountId);
		return LoginResult.SUCCEEDED;
	}

	public static void logout() {
		RequestTracker.setSessionInt("accountId", 0);
	}

	private static String sha256(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String craeteActivationToken(int accountId) {
		String token = UUID.randomUUID().toString().replace("-", "");
		DataBase.insert("xjfw.account_tokens", "token", token, "accountId", accountId);
		return token;
	}

	public static AuthenticationResult authenticateAction(String authenticaionClassName) {
		try {
			Class<?> cls = Class.forName(authenticaionClassName);
			Object instance = cls.newInstance();
			if (instance instanceof IAuthentication) {
				if (((IAuthentication) instance).authenticate(RequestTracker.getHTTPRequest())) {
					return AuthenticationResult.OK;
				} else if (((IAuthentication) instance).useUnauthorizedResponseIfRequired()) {
					return AuthenticationResult.UNAUTHORIZED;
				}
				return AuthenticationResult.FAILED;
			}
		} catch (Throwable throwable) {
			JettyUtils.logThrowable(throwable);
			return AuthenticationResult.FAILED;
		}
		int accountId = RequestTracker.getSessionInt("accountId");
		if (accountId == 0) {
			return AuthenticationResult.NOLOGIN;
		}
		if (authenticateAction(authenticaionClassName, accountId)) {
			return AuthenticationResult.OK;
		}
		return AuthenticationResult.FAILED;
	}

	public static boolean authenticateAction(String authenticaionClassName, int accountId) {
		SqlQuery query = new SqlQuery("xjfw.account.groups");
		query.fields.add("groupId");
		query.where.add("accountId", accountId);
		List<Map<String, Object>> result = DataBase.select(query);
		for (Map<String, Object> row : result) {
			SqlQuery rightQuery = new SqlQuery("xjfw.authentication.rights");
			rightQuery.fields.add("id");
			rightQuery.where.add("groupId", (Integer) row.get("groupId"));
			rightQuery.where.add("className", authenticaionClassName);
			if (DataBase.select(rightQuery).size() > 0) {
				return true;
			}
		}
		SqlQuery rightQuery = new SqlQuery("xjfw.authentication.rights");
		rightQuery.fields.add("id");
		rightQuery.where.add("groupId", 0);
		rightQuery.where.add("className", authenticaionClassName);
		if (DataBase.select(rightQuery).size() > 0) {
			return true;
		}
		return false;
	}
}
