package com.nut.teamradar.webclient;

import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

public interface HttpInterface {
	AsyncHttpClient getAsyncHttpClient();
	
	void setAsyncHttpClient(AsyncHttpClient client);

	RequestHandle doPost(String URL, HashMap urlParams, ResponseHandlerInterface responseHandler);
	RequestHandle doGet(String URL, ResponseHandlerInterface responseHandler);
}
