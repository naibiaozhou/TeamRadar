package com.nut.teamradar;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.nut.teamradar.base.Constant;
import com.nut.teamradar.base.JsonMessage;
import com.nut.teamradar.model.Result;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.AppUtils;
import com.nut.teamradar.util.DBConnection;
import com.opensymphony.xwork2.ActionSupport;

public class getSessionIdAction extends ActionSupport implements ServletRequestAware,  
	ServletResponseAware {  
	private String message;
	private String code;
	private boolean flag =false;
	private Result result;
	private String SessionId=null;	
	private HttpServletRequest request;  
	
	private HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {  
		this.request = request;  
        SessionId = request.getSession().getId();
        if(SessionId != null)
        {
        	//System.out.println(SessionId+":"+this.getClass().getName());
        }
	}  
	@Override
	public void setServletResponse(HttpServletResponse response) {  
	    this.response = response;  
	} 
	
	@Override
	public String execute() throws Exception {
		JsonMessage msg = new JsonMessage();
		result = new Result();
		result.setData("success");
		msg.setCode("2");
		msg.setMessage("get_session");
		msg.setObject(result);
		AppUtils.writeResponse(SessionId,this.getClass().getName(),msg, response);
	    return null; 
	}
	
	@SkipValidation
	public String form() throws Exception {
	    return INPUT;
	}
}