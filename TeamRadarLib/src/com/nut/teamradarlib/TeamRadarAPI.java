package com.nut.teamradarlib;

public class TeamRadarAPI {
	
	protected native int setMagic(char[] magic,int len);
	protected native String getString1();
	protected native String getString2();
	protected native String getString3();
	protected native String getString4();
	protected native String getString5();
	protected native String getString6();
	protected native String getString7();
    private static TeamRadarAPI _instance=null;
    static
    {
    	System.loadLibrary("TeamRadarLib");
    }
	private TeamRadarAPI()
	{
		
	}
	public static TeamRadarAPI getInstance()
	{
		if(_instance == null)
		{
			_instance=new TeamRadarAPI();
		}
		return _instance;
	}
	public int Init(char[] magic,int len)
	{
		return setMagic(magic,len);
	}
	public String getAppKey()
	{
		return getString1();
	}
	public String getAppSecret()
	{
		return getString2();
	}
	public String getSecurityCode()
	{
		return getString3();
	}
	public String getAlias()
	{
		return getString4();
	}
	public String getStorepass()
	{
		return getString5();
	}
	public String getAddr()
	{
		return getString6();
	}
	public String getPort()
	{
		return getString7();
	}
}
