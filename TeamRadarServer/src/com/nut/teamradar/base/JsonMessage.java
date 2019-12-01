package com.nut.teamradar.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import com.nut.teamradar.util.Encrypt;

import net.sf.json.JSONObject;

public class JsonMessage {
	private String message;
	private String code;
	private BaseModel obj=null;
	private Map<String, Object> dataMap;   
	private Map<String, Object> resultMap;
	private List <BaseModel> dataSet=null;
	private JSONObject json;
	String className;
	String [] path;
    public JsonMessage()
    {
    	dataMap = new HashMap<String, Object>();
    	resultMap= new HashMap<String, Object>();	
    	dataSet = new ArrayList<BaseModel>();
    	json=new JSONObject();
    }
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    public String getMessage()
    {
    	return message;
    }
    public void setMessage(String str)
    {
    	message = str;
    }
    public void setCode(String code)
    {
    	this.code = code;
    }
    public String getCode()
    {
    	return code;
    } 
    public void setObject(BaseModel obj)
    {
    	this.obj = obj;
    }
    public void addObject(BaseModel obj)
    {
    	this.dataSet.add(obj);
    }
    public BaseModel getObject()
    {
    	return obj;
    }
    public byte[] toJsonStream() throws Exception
    {
    	byte[] jsonBytes = new byte[1];
    	String Out="";

    	dataMap.put("code", code);
    	dataMap.put("message", message);
    	if(obj != null)
    	{
        	className = obj.getClass().getName();
        	path = className.split("\\.");
    		resultMap.put(path[path.length-1], obj);
    	}
    	else if(dataSet.size() != 0)
    	{
    		obj = dataSet.get(0);
        	className = obj.getClass().getName();
        	path = className.split("\\.");
    		resultMap.put(path[path.length-1], this.dataSet);
    	}
    	else
    	{
    		throw new NullPointerException();
    	}
    	dataMap.put("result",resultMap);
    	
    	json.putAll(dataMap);
    	
        String str = json.toString();
        String ustr = URLEncoder.encode(str, "utf-8");

    	ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();    
    	GZIPOutputStream gop;
		try {
			gop = new GZIPOutputStream(arrayOutputStream);
	    	gop.write(ustr.getBytes("utf-8"), 0, ustr.length());
	    	gop.finish();
	    	gop.close();
	    	jsonBytes = arrayOutputStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		Out = Encrypt.encode(jsonBytes);
		return Out.getBytes();
    }
}
