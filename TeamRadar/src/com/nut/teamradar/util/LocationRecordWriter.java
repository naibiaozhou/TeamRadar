package com.nut.teamradar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import com.nut.teamradar.model.Location;

public class LocationRecordWriter {
    private static final String TAG = "LocationRecordWriter";
    private static final File sdCardDir = Environment.getExternalStorageDirectory();
    private static final String DIR = "com.nut.teamradar";
    private FileOutputStream mFos = null;
    private XmlProcessor mXmlProcessor = new XmlProcessor();

    public synchronized boolean beginWrite(ContextWrapper ctx) {
        if (mFos != null)
            return true;
        try {
            /*String sdStatus = Environment.getExternalStorageState();
            if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                 Log.d(TAG, "SD card is not avaiable/writeable right now.");
                 return false;
            }*/

            mFos = ctx.openFileOutput(LocationRecordFileNameProcessor.generateLocationRecordFileName(new Date()), Context.MODE_PRIVATE);
            /*
            mFileName = sdCardDir.getCanonicalPath() + "/" + 
                    LocationRecordFileNameGenerator.generateLocationRecordFileName(new Date());*/
            byte[] rec = mXmlProcessor.startLocationDocument();
            if (rec != null)
                mFos.write(rec);
            mFos.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Open() failed");
            e.printStackTrace();
            return false;
        }
    }
    public synchronized void endWrite() {
        if (mFos != null) {
            try {
                byte[] rec = mXmlProcessor.endLocationDocument();
                if (rec != null)
                    mFos.write(rec);
                mFos.flush();
                mFos.close();
                mFos = null;
            } catch (IOException e) {
                Log.e(TAG, "Close() failed");
                Log.e(TAG, e.toString());
            }
        } else {
            Log.e(TAG, "Try to close null file");
        }
    }

    public synchronized void writeStringRecord(String name, Location loc) {
        if (mFos != null) {
            try {
                mFos.write(mXmlProcessor.locationToXmlString(loc, name));
                mFos.flush();
            } catch (IOException e) {
                Log.e(TAG, "writeStringRecord() failed");
                Log.e(TAG, e.toString());
            }
        } else {
            Log.e(TAG, "Try to write null file");
        }
    }
    public synchronized void writeBinRecord(String sub, String name, Location loc) throws Exception {
        Log.e(TAG, "Not Implemented");
        throw new Exception();
    }
}
