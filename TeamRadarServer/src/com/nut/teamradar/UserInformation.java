package com.nut.teamradar;

import java.util.HashMap;
import java.util.Map;

public class UserInformation {
	private Map<String, Object> parameters = null;
	public UserInformation()
	{
		parameters = new HashMap<String, Object>();
	}
	public void put(String key, Object value)
	{
		parameters.put(key, value);
	}
	public Object get(String key)
	{
		return parameters.get(key);
	}
}
