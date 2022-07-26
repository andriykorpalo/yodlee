/*
* Copyright (c) 2016 Yodlee, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of Yodlee, Inc.
* Use is subject to license terms.
*/

package com.strabo.parser;

import java.io.IOException;

import com.strabo.beans.ProviderAccountRefreshStatus;

import com.google.gson.Gson;

public class ProviderAccRefreshStatusParser implements Parser{

	private String fqcn = this.getClass().getName();
	public ProviderAccountRefreshStatus parseJSON(String json) throws IOException 
	{
		
		Gson gson = new Gson();
		return (ProviderAccountRefreshStatus)gson.fromJson(json, ProviderAccountRefreshStatus.class);
	}

}
