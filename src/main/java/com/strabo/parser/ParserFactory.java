/*
* Copyright (c) 2015 Yodlee, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of Yodlee, Inc.
* Use is subject to license terms.
*/
package com.strabo.parser;


public class ParserFactory 
{
	private static final String fqcn=ParserFactory.class.getName();
	
	public static Parser getParser(Class<?> T)
	{
		
		Parser parser = null;
		if(T.getCanonicalName().equals("com.strabo.beans.CobrandContext"))
		{
			parser = (CobrandContextParser) new CobrandContextParser();
		}
		if(T.getCanonicalName().equals("com.strabo.beans.UserContext"))
		{
			parser = (UserContextParser) new UserContextParser();
		}
		
		if(T.getCanonicalName().equals("com.strabo.beans.AccessToken"))
		{
			parser = (AccessTokenParser) new AccessTokenParser();
		}
		if(T.getCanonicalName().equals("com.strabo.beans.ProviderAccountRefreshStatus"))
		{
			parser = (ProviderAccRefreshStatusParser) new ProviderAccRefreshStatusParser();
		}
		return parser;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
