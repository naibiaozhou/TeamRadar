package com.nut.teamradar.base;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.HashMap;
import java.util.List;

public interface UIInterface {
	
	AsyncHttpClient getAsyncHttpClient();
	
	void setAsyncHttpClient(AsyncHttpClient client);

	RequestHandle doPost(String URL, HashMap urlParams, ResponseHandlerInterface responseHandler);
	RequestHandle doGet(String URL, ResponseHandlerInterface responseHandler);
}
