package com.nut.teamradar.webclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.Service.ApplicationData;
import com.nut.teamradar.base.BaseMessage;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.SecureSocketFactory;
import com.nut.teamradarlib.TeamRadarAPI;

public class HttpConnection implements HttpInterface{
	static final String TAG = "HttpConnection";
    
    private int httpsport = 8443;
    private int clientid = R.raw.client; 
    private Context mCtx;
    
    public HttpConnection(Context ctx)
    {
    	mCtx = ctx;
    }
    
    private AsyncHttpClient  asyncHttpClient = new AsyncHttpClient(8080,httpsport) {

        @Override
        protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
            AsyncHttpRequest httpRequest = getHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context);
            return httpRequest == null
                    ? super.newAsyncHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context)
                    : httpRequest;
        }
    };
    
    public AsyncHttpRequest getHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
        return null;
    }

    
    public void ReconnectHttpClient()
    {
    	Log.d(TAG, "ReconnectHttpClient");
    	asyncHttpClient = null;
    	asyncHttpClient = new AsyncHttpClient(8080,httpsport) {

            @Override
            protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
                AsyncHttpRequest httpRequest = getHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context);
                return httpRequest == null
                        ? super.newAsyncHttpRequest(client, httpContext, uriRequest, contentType, responseHandler, context)
                        : httpRequest;
            }
        };
        try {
        	//Log.d(TAG, "load keystore"+String.format("%d %x", httpsport,clientid));
        	InputStream is = null;
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            is = mCtx.getResources().openRawResource(clientid);
            store.load(is, TeamRadarAPI.getInstance().getStorepass().toCharArray());
            asyncHttpClient.setSSLSocketFactory(new SecureSocketFactory(store, TeamRadarAPI.getInstance().getAlias()));
            Log.d(TAG, "setSSLSocketFactory");
		} catch (Exception e) {
			e.printStackTrace();
		}
        asyncHttpClient.setTimeout(50000);
    }
    public void sethttpsport(int port)
    {
    	httpsport = port;
    }
    public void setcertfileid(int id)
    {
    	clientid = id;
    }
    public String unGZIP(String str)
    {
    	byte[] bytes;
    	ByteArrayOutputStream out = new ByteArrayOutputStream();   
	    GZIPInputStream gunzip;
	    bytes = Encrypt.decode(str);
	    //printHex(bytes);
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes); 
			
			gunzip = new GZIPInputStream(in);
		    byte[] buffer = new byte[1024];   
		    int n;   
		    while ((n = gunzip.read(buffer))>= 0) {   
		    	out.write(buffer, 0, n);   
		    }    
		} catch (IOException e) {
			e.printStackTrace();
		}   
	    return out.toString();    
    }
    public  void printHex(byte[] bytes)
    {
    	String out="String to Hex :";
    	for(int i=0;i<bytes.length;i++)
    	{
    		out = out + String.format("%x ", bytes[i]);
    	}
    	System.out.println(out);


    }
    public BaseMessage getMessage(String jsonStr) throws Exception {
        BaseMessage message = new BaseMessage();
        String uStr = URLDecoder.decode(unGZIP(jsonStr), "utf-8");
        //Log.d(TAG, uStr);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(uStr);//uStr
            if (jsonObject != null) {
                message.setCode(jsonObject.getString("code"));
                message.setMessage(jsonObject.getString("message"));
                message.setResult(jsonObject.getString("result"));
            }
        } catch (JSONException e) {
        	System.out.println(jsonStr);
        	e.printStackTrace();
            throw new Exception("Json format error");
        } catch (Exception e) {
        	System.out.println(jsonStr);
            e.printStackTrace();
        }
        return message;
    }
	@Override
	public AsyncHttpClient getAsyncHttpClient() {
		return this.asyncHttpClient;
	}

	@Override
	public void setAsyncHttpClient(AsyncHttpClient client) {
		this.asyncHttpClient = client;
	}

	@Override
    public synchronized RequestHandle doPost(String URL, HashMap urlParams,
            ResponseHandlerInterface responseHandler) {
		RequestHandle Handle = null;
        List<Header> headers = new ArrayList<Header>();
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        headers.add(new BasicHeader("Accept", "text/json"));
        headers.add(new BasicHeader("Charset", "UTF-8"));
        if (ApplicationData.getInstance().getSessionId() != null) {
            headers.add(new BasicHeader("TeamRadarSessionId",
            		ApplicationData.getInstance().getSessionId()));
        }
        Iterator it = urlParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            postParams.add(new BasicNameValuePair(entry.getKey().toString(),
                    entry.getValue().toString()));
        }
        HttpEntity httpEntity = null;
        try {
            httpEntity = new UrlEncodedFormEntity(postParams, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Log.d(TAG, "doPost:" + URL);
        try{
        	Handle = asyncHttpClient.post(mCtx, URL,
                headers.toArray(new Header[headers.size()]), httpEntity, null,
                responseHandler);
        } catch (Exception e) {
        	ReconnectHttpClient();
        }
        return Handle;
    }

    @Override
    public synchronized RequestHandle doGet(String URL,
            ResponseHandlerInterface responseHandler) {
    	RequestHandle Handle = null;
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "text/json"));
        headers.add(new BasicHeader("Charset", "UTF-8"));
        if (ApplicationData.getInstance().getSessionId() != null) {
            headers.add(new BasicHeader("TeamRadarSessionId",
            		ApplicationData.getInstance().getSessionId()));
        }
        //Log.d(TAG, "doGet:" + URL);
        try{
        	Handle = asyncHttpClient.get(mCtx, URL,
                headers.toArray(new Header[headers.size()]), null,
                responseHandler);
        } catch (Exception e) {
        	ReconnectHttpClient();
        }
        return Handle;
    }

}
