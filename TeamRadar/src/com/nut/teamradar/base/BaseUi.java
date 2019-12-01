package com.nut.teamradar.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseUi extends Fragment  {
	static final String TAG = "BaseUi";
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void registerForContextMenu(View view) {
        super.registerForContextMenu(view);
    }

    @Override
    public void unregisterForContextMenu(View view) {
        super.unregisterForContextMenu(view);
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void setInitialSavedState(Fragment.SavedState state) {
        super.setInitialSavedState(state);
    }

    @Override
    public void onResume() {
        super.onResume();
        // debug memory
        debugMemory("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        // debug memory
        debugMemory("onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        // debug memory
        debugMemory("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        // debug memory
        debugMemory("onStop");
    }

    public void onShow() {
        debugMemory("onShow");
    }

    public void onHide() {
        debugMemory("onHide");
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////
    // util method

    public void toast(String msg) {
        Toast t = Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    public Context getContext() {
        return this.getActivity();
    }

    public LayoutInflater getLayout() {
        return (LayoutInflater) this.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getLayout(int layoutId) {
        return getLayout().inflate(layoutId, null);
    }

    public View getLayout(int layoutId, int itemId) {
        return getLayout(layoutId).findViewById(itemId);
    }

    public void debugMemory(String tag) {
        // Log.w(this.getClass().getSimpleName(),
        // tag+":"+AppUtil.getUsedMemory());
    }
}