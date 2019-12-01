package com.nut.teamradar.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST API Demo : 鍥剧墖璇煶鏂囦欢涓婁紶銆佷笅杞�HttpClient4.3瀹炵幇
 * 
 * Doc URL: http://www.easemob.com/docs/rest/files/
 * 
 * @author Lynch 2014-09-15
 *
 */
public class EasemobFiles {
	private static final String APPKEY = Constants.APPKEY;
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    // 閫氳繃app鐨刢lient_id鍜宑lient_secret鏉ヨ幏鍙朼pp绠＄悊鍛榯oken
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);

    

	/**
	 * 鍥剧墖/璇煶鏂囦欢涓婁紶
	 * 
	 * @param uploadFile
     *
	 */
	public static ObjectNode mediaUpload(File uploadFile) {

		ObjectNode objectNode = factory.objectNode();

		if (!uploadFile.exists()) {

			objectNode.put("message", "File or directory not found");

			return objectNode;
		}

		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		try {

			List<NameValuePair> headers = new ArrayList<NameValuePair>();
			headers.add(new BasicNameValuePair("restrict-access", "true"));

			objectNode = HTTPClientUtils.uploadFile(EndPoints.CHATFILES_URL, uploadFile, credential, headers);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍥剧墖璇煶鏂囦欢涓嬭浇
	 * 
	 * 
	 * @param fileUUID
	 *            鏂囦欢鍦―B鐨刄UID
	 * @param shareSecret
	 *            鏂囦欢鍦―B涓繚瀛樼殑shareSecret
	 * @param localPath
	 *            涓嬭浇鍚庢枃浠跺瓨鏀惧湴鍧�	 * @param isThumbnail
	 *            鏄惁涓嬭浇缂╃暐鍥�true:缂╃暐鍥�false:闈炵缉鐣ュ浘
	 * @return
	 */
	public static ObjectNode mediaDownload(String fileUUID, String shareSecret, File localPath, Boolean isThumbnail) {

		ObjectNode objectNode = factory.objectNode();

		File downLoadedFile = null;

		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		try {

			List<NameValuePair> headers = new ArrayList<NameValuePair>();
			if (!StringUtils.isEmpty(shareSecret)) {
				headers.add(new BasicNameValuePair("share-secret", shareSecret));
			}
			headers.add(new BasicNameValuePair("Accept", "application/octet-stream"));
			if (isThumbnail != null && isThumbnail) {
				headers.add(new BasicNameValuePair("thumbnail", String.valueOf(isThumbnail)));
			}

			URL mediaDownloadUrl = HTTPClientUtils
					.getURL(Constants.APPKEY.replace("#", "/") + "/chatfiles/" + fileUUID);
			downLoadedFile = HTTPClientUtils.downLoadFile(mediaDownloadUrl, credential, headers, localPath);

		} catch (Exception e) {
			e.printStackTrace();
		}

		objectNode.put("message", "File download successfully .");

		return objectNode;
	}

}
