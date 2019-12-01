package com.nut.teamradar.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST API Demo: 鍙戦�娑堟伅 REST API HttpClient4.3瀹炵幇
 * 
 * Doc URL: http://www.easemob.com/docs/rest/sendmessage/
 * 
 * @author Lynch 2014-09-15
 *
 */
public class EasemobMessages {

    private static final String APPKEY = Constants.APPKEY;
    private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    // 閫氳繃app鐨刢lient_id鍜宑lient_secret鏉ヨ幏鍙朼pp绠＄悊鍛榯oken
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);

    
	/**
	 * 妫�祴鐢ㄦ埛鏄惁鍦ㄧ嚎
	 * 
	 * @param username
     *
	 * @return
	 */
	public static ObjectNode getUserStatus(String username) {
		ObjectNode objectNode = factory.objectNode();

		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		// check properties that must be provided
		if (StringUtils.isEmpty(username)) {
			objectNode.put("message", "You must provided a targetUserName .");
			return objectNode;
		}

		try {
			URL userStatusUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users/"
					+ username + "/status");
			objectNode = HTTPClientUtils.sendHTTPRequest(userStatusUrl, credential, null, HTTPMethod.METHOD_GET);
			String userStatus = objectNode.get("data").path(username).asText();
			if ("online".equals(userStatus)) {
			} else if ("offline".equals(userStatus)) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍙戦�娑堟伅
	 * 
	 * @param targetType
	 *            娑堟伅鎶曢�鑰呯被鍨嬶細users 鐢ㄦ埛, chatgroups 缇ょ粍
	 * @param target
	 *            鎺ユ敹鑰匢D 蹇呴』鏄暟缁�鏁扮粍鍏冪礌涓虹敤鎴稩D鎴栬�缇ょ粍ID
	 * @param msg
	 *            娑堟伅鍐呭
	 * @param from
	 *            鍙戦�鑰�	 * @param ext
	 *            鎵╁睍瀛楁
	 * 
	 * @return 璇锋眰鍝嶅簲
	 */
	public static ObjectNode sendMessages(String targetType, ArrayNode target, ObjectNode msg, String from,
			ObjectNode ext) {

		ObjectNode objectNode = factory.objectNode();

		ObjectNode dataNode = factory.objectNode();

		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		// check properties that must be provided
		if (!("users".equals(targetType) || "chatgroups".equals(targetType))) {
			
			objectNode.put("message", "TargetType must be users or chatgroups .");

			return objectNode;
		}

		try {
			// 鏋勯�娑堟伅浣�			dataNode.put("target_type", targetType);
			dataNode.put("target", target);
			dataNode.put("msg", msg);
			dataNode.put("from", from);
			dataNode.put("ext", ext);

			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.MESSAGES_URL, credential, dataNode,
					HTTPMethod.METHOD_POST);

			objectNode = (ObjectNode) objectNode.get("data");
			for (int i = 0; i < target.size(); i++) {
				String resultStr = objectNode.path(target.path(i).asText()).asText();
				if ("success".equals(resultStr)) {
				} else if (!"success".equals(resultStr)) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

}
