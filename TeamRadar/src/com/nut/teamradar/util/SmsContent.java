package com.nut.teamradar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

public class SmsContent extends ContentObserver {
	 
	 public static final String SMS_URI_INBOX = "content://sms/inbox";
	 private Activity activity = null;
	 private String smsContent = "";
	 private EditText verifyText = null;
	 public SmsContent(Activity activity, Handler handler, EditText verifyText) {
	  super(handler);
	  this.activity = activity;
	  this.verifyText = verifyText;
	 }
	 @Override
	 public void onChange(boolean selfChange) {
	    super.onChange(selfChange);
	    Cursor cursorCMCC = null;// 光标
	    Cursor cursorCUNION = null;
	    
	    // 读取收件箱中指定号码的短信
	    cursorCMCC = activity.getContentResolver().query(Uri.parse(SMS_URI_INBOX), 
			  new String[] { "_id", "address", "body", "read" }, 
			  "address=? and read=?",
			  new String[] { "10690032980066", "0" }, 
			  "date desc");

	    if (cursorCMCC != null) {// 如果短信为未读模式
	    	cursorCMCC.moveToFirst();
		   if (cursorCMCC.moveToFirst()) {
		    String smsbody = cursorCMCC.getString(cursorCMCC.getColumnIndex("body"));
			System.out.println("smsbody=======================" + smsbody);
			String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(smsbody.toString());
			smsContent = m.replaceAll("").trim().toString();
		    verifyText.setText(smsContent);
		   }
	    }
	    cursorCUNION = activity.getContentResolver().query(Uri.parse(SMS_URI_INBOX), 
			  new String[] { "_id", "address", "body", "read" }, 
			  "address=? and read=?",
			  new String[] { "10657120610111","0" }, 
			  "date desc");
	    if (cursorCUNION != null) {// 如果短信为未读模式
	    	cursorCUNION.moveToFirst();
		   if (cursorCUNION.moveToFirst()) {
		    String smsbody = cursorCUNION.getString(cursorCUNION.getColumnIndex("body"));
			System.out.println("smsbody=======================" + smsbody);
			String regEx = "[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(smsbody.toString());
			smsContent = m.replaceAll("").trim().toString();
		    verifyText.setText(smsContent);
		   }
	    }
	}
}