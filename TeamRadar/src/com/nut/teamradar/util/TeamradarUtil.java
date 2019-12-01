package com.nut.teamradar.util;

import android.content.Context;
import android.os.Environment;

public class TeamradarUtil {
	public static  String getSdCacheDir(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			java.io.File fExternalStorageDirectory = Environment
					.getExternalStorageDirectory();
			java.io.File trDir = new java.io.File(
					fExternalStorageDirectory, "Teamradar");
			boolean result = false;
			if (!trDir.exists()) {
				result = trDir.mkdir();
			}
			java.io.File updateDir = new java.io.File(trDir,
					"APK");
			if (!updateDir.exists()) {
				result = updateDir.mkdir();
			}
			return updateDir.toString() + "/";
		} else {
			return "";
		}
	}
}
