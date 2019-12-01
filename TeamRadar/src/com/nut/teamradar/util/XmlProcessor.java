package com.nut.teamradar.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.nut.teamradar.model.Location;

public class XmlProcessor {
    private static final String TAG = "XmlParser";
    private static final String ENCODING = "UTF-8";
    private static final String LOCATIONS_TAG = "Locations";
    private static final String LOCATION_TAG = "Location";
    private static final String INDENT = "http://xmlpull.org/v1/doc/features.html#indent-output";
    
    private XmlPullParser mParser = null;
    private String mCurrentTag = null;
    private String mCurrentUserName = null;
    private boolean mEndLocations = false;
    
    public boolean isParseDone() {
        try {
            if (mEndLocations || mParser == null || mParser.getEventType() == XmlPullParser.END_DOCUMENT)
                return true;
            return false;
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.toString());
            return true;
        }
    }

    private void setupLocation(Location loc, String txt) {
        if (mCurrentTag == null || loc == null || txt == null)
            return;

        if (mCurrentTag.compareTo(Location.COL_LATITUDE) == 0)
            loc.setLatitude(Double.parseDouble(txt));
        else if (mCurrentTag.compareTo(Location.COL_LONGITUDE) == 0)
            loc.setLongitude(Double.parseDouble(txt));
        else if (mCurrentTag.compareTo(Location.COL_ACCURACY) == 0)
            loc.setAccuracy(Float.parseFloat(txt));
        else if (mCurrentTag.compareTo(Location.COL_ACTIVITYID) == 0)
            loc.setGroupid(Integer.parseInt(txt));
        else if (mCurrentTag.compareTo(Location.COL_ALTITUDE) == 0)
            loc.setAltitude(Integer.parseInt(txt));
        else if (mCurrentTag.compareTo(Location.COL_AVGCN0) == 0)
            loc.setAveragecn0(Integer.parseInt(txt));
        else if (mCurrentTag.compareTo(Location.COL_HEADING) == 0)
            loc.setHeading(Float.parseFloat(txt));
        else if (mCurrentTag.compareTo(Location.COL_ID) == 0)
            loc.setId(Long.parseLong(txt));
        else if (mCurrentTag.compareTo(Location.COL_PROVIDER) == 0)
            loc.setProvider(txt);
        else if (mCurrentTag.compareTo(Location.COL_SPEED) == 0)
            loc.setSpeed(Float.parseFloat(txt));
        else if (mCurrentTag.compareTo(Location.COL_USERID) == 0)
            loc.setUserid(Integer.parseInt(txt));
        else if (mCurrentTag.compareTo(Location.NAME) == 0)
            mCurrentUserName = txt;
        return;
    }
    
    public String getCurrentUserName() {
        return mCurrentUserName;
    }
    
    private boolean isLocation(String tag) {
        return tag.compareTo(LOCATION_TAG) == 0;
    }
    
    private boolean isLocations(String tag) {
        return tag.compareTo(LOCATIONS_TAG) == 0;
    }
    public String getLocation(Location loc) {
        mCurrentUserName = null;
        while (!isParseDone()) {
            try {
                int eventType = mParser.next();
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    //Log.e(TAG, "START_TAG = " + mParser.getName());
                    mCurrentTag = mParser.getName();
                    if (isLocation(mCurrentTag)) {
                        //Log.e(TAG, "Time = " + Long.parseLong(mParser.getAttributeValue(0)));
                        loc.setTime(Long.parseLong(mParser.getAttributeValue(0)));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    //Log.e(TAG, "END_TAG = " + mParser.getName());
                    mCurrentTag = null;
                    if (isLocation(mParser.getName())) {
                        mParser.next();mParser.nextToken(); // TO parse end of document correctly.
                        if (mParser.getName() != null && isLocations(mParser.getName())) {
                            mEndLocations = true;
                            //Log.e(TAG, "getLocations: END? " + Boolean.toString(mEndLocations));
                        }
                        return mCurrentUserName;
                    }
                    break;
                case XmlPullParser.TEXT:
                    setupLocation(loc, mParser.getText());
                    //Log.e(TAG, "TEXT = " + mParser.getText());
                    break;
                default:
                    break;
                }
            } catch (XmlPullParserException e) {
                Log.e(TAG, e.toString());
                return null;
            } catch(IOException e)
            {
                Log.e(TAG, e.toString());
                return null;
            }
        }
        return null;
    }

    public boolean startParseLocation(InputStream is) {
        mEndLocations = false;
        try {
            XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            mParser = factory.newPullParser();
            mParser.setInput(is, ENCODING);
            if (mParser.getEventType() != XmlPullParser.START_DOCUMENT)
                return false;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Failed to create XmlPullParserFactory");
            Log.e(TAG, e.toString());
            return false;
        }
        return true;
    }

    private XmlSerializer mSerializer = Xml.newSerializer();
    
    private ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
    public byte[] startLocationDocument() {
        mBaos.reset();
        try {
            mSerializer.setOutput(mBaos, ENCODING);
            mSerializer.startDocument("UTF-8", true);
            mSerializer.setFeature(INDENT, true); 
            mSerializer.startTag(null, LOCATIONS_TAG);
            mSerializer.flush();
        } catch (IllegalArgumentException  e) {
            Log.e(TAG, e.toString());
        }catch(IllegalStateException  e)
        {
        	Log.e(TAG, e.toString());
        }catch(IOException e){
        	Log.e(TAG, e.toString());
        }
        return mBaos.size() > 0 ? mBaos.toByteArray() : null;
    }
    public byte[] endLocationDocument() {
        try {
        mBaos.reset();
        mSerializer.endTag(null, LOCATIONS_TAG);
        mSerializer.endDocument();
        mSerializer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBaos.size() > 0 ? mBaos.toByteArray() : null;
    }
    public byte[] locationToXmlString(Location loc, String name) {
        mBaos.reset();
        try {
            mSerializer.startTag(null, LOCATION_TAG);
            mSerializer.attribute(null, Location.COL_TIME, Long.toString(loc.getTime()));
            mSerializer.startTag(null, Location.COL_ID);
            mSerializer.text(Long.toString(loc.getId()));
            mSerializer.endTag(null, Location.COL_ID);
            mSerializer.startTag(null, Location.COL_ACTIVITYID);
            mSerializer.text(Long.toString(loc.getGroupid()));
            mSerializer.endTag(null, Location.COL_ACTIVITYID);
            mSerializer.startTag(null, Location.COL_USERID);
            mSerializer.text(Long.toString(loc.getUserid()));
            mSerializer.endTag(null, Location.COL_USERID);
            mSerializer.startTag(null, Location.SUB);
            mSerializer.endTag(null, Location.SUB);
            mSerializer.startTag(null, Location.NAME);
            if (name == null)
                mSerializer.text("");
            else
            mSerializer.text(name);
            mSerializer.endTag(null, Location.NAME);
            mSerializer.startTag(null, Location.COL_LATITUDE);
            mSerializer.text(Double.toString(loc.getLatitude()));
            mSerializer.endTag(null, Location.COL_LATITUDE);
            mSerializer.startTag(null, Location.COL_LONGITUDE);
            mSerializer.text(Double.toString(loc.getLongitude()));
            mSerializer.endTag(null, Location.COL_LONGITUDE);
            mSerializer.startTag(null, Location.COL_ALTITUDE);
            mSerializer.text(Integer.toString(loc.getAltitude()));
            mSerializer.endTag(null, Location.COL_ALTITUDE);
            mSerializer.startTag(null, Location.COL_SPEED);
            mSerializer.text(Float.toString(loc.getSpeed()));
            mSerializer.endTag(null, Location.COL_SPEED);
            mSerializer.startTag(null, Location.COL_HEADING);
            mSerializer.text(Float.toString(loc.getHeading()));
            mSerializer.endTag(null, Location.COL_HEADING);
            mSerializer.startTag(null, Location.COL_ACCURACY);
            mSerializer.text(Float.toString(loc.getAccuracy()));
            mSerializer.endTag(null, Location.COL_ACCURACY);
            mSerializer.startTag(null, Location.COL_AVGCN0);
            mSerializer.text(Integer.toString(loc.getAveragecn0()));
            mSerializer.endTag(null, Location.COL_AVGCN0);
            mSerializer.endTag(null, LOCATION_TAG);
            mSerializer.flush();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return mBaos.size() > 0 ? mBaos.toByteArray() : null;
    }
}
