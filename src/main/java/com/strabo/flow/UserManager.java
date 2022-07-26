/**
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. Use is subject to license terms.
 */
package com.strabo.flow;

import com.yodlee.sdk.context.ClientCredentialUserContext;

import java.util.HashMap;
import java.util.Map;

;

public class UserManager {

	private static final UserManager INSTANCE = new UserManager();

	private final Map<String, ClientCredentialUserContext> userContextMap = new HashMap<>();

	private UserManager() {}

	public static UserManager getInstance() {
		return INSTANCE;
	}

	public ClientCredentialUserContext getContext(String userIdentity) {
		return userContextMap.get(userIdentity);
	}

	public void addUser(String userIdentity, ClientCredentialUserContext clientCredentialUserContext) {
		userContextMap.put(userIdentity, clientCredentialUserContext);
	}

	public void removeUser(String userIdentity) {
		userContextMap.remove(userIdentity);
	}

	public void clear() {
		userContextMap.clear();
	}
}
