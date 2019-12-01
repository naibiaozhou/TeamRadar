package com.nut.teamradar.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.util.Log;

import com.nut.teamradar.model.Location;

public class LocationRecordReader {
    private static final String TAG = "LocationRecordReader";
    private XmlProcessor mXmlProcessor = new XmlProcessor();
    
    public boolean beginRead(String fName) {
        try {
            FileInputStream fis = new FileInputStream(fName);
            return mXmlProcessor.startParseLocation(fis);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }
    
    public boolean hasNext() {
        return !mXmlProcessor.isParseDone();
    }
    public String getLocation(Location loc) {
        return mXmlProcessor.getLocation(loc);
    }
    
    public String getCurrentUserName() {
        return mXmlProcessor.getCurrentUserName();
    }
}
