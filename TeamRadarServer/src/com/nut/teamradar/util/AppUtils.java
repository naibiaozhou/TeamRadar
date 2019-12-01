package com.nut.teamradar.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.nut.teamradar.UserInformation;
import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;

public class AppUtils {
	
	public static void writeResponse(String SessionID,String ClassName,JsonMessage msg,HttpServletResponse response)
	{
    	response.reset(); 
    	response.setContentType("text/json;charset=utf-8");  
        response.setCharacterEncoding("UTF-8");
        byte[] jsonBytes;
		try {
			jsonBytes = msg.toJsonStream();
			//printHex(jsonBytes);
			int length = jsonBytes.length;
	        response.setContentLength(length);  
	        response.setHeader("Content-Encoding", "gzip"); 
	        if(SessionID!=null)
	        {
	        	response.setHeader("TeamRadarSessionId", SessionID);
	        }
        	ServletOutputStream out = response.getOutputStream();
        	out.write(jsonBytes);
        	out.flush();  
        	out.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//System.out.println(SessionID+":"+ClassName +" : Write Response!");
	}
    public static  void printHex(byte[] bytes)
    {
    	String out="String to Hex :";
    	for(int i=0;i<bytes.length;i++)
    	{
    		out = out + String.format("%x ", bytes[i]);
    	}
    	System.out.println(out);
    }
}
