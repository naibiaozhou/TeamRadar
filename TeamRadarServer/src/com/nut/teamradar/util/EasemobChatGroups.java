package com.nut.teamradar.util;

import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST API Demo : 缇ょ粍绠＄悊 HttpClient4.3瀹炵幇
 * 
 * Doc URL: http://www.easemob.com/docs/rest/groups/
 * 
 * @author Lynch 2014-09-15
 *
 */
public class EasemobChatGroups {


    // 閫氳繃app鐨刢lient_id鍜宑lient_secret鏉ヨ幏鍙朼pp绠＄悊鍛榯oken
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);

    	
	private static JsonNodeFactory factory = new JsonNodeFactory(false);
	private static final String APPKEY = Constants.APPKEY;
	
	/**
	 * 鑾峰彇APP涓墍鏈夌殑缇ょ粍ID
	 * 
	 * @return
	 */
	public static ObjectNode getAllChatgroupids() {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.CHATGROUPS_URL, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鑾峰彇涓�釜鎴栬�澶氫釜缇ょ粍鐨勮鎯�	 * 
	 * @return
	 */
	public static ObjectNode getGroupDetailsByChatgroupid(String[] chatgroupIDs) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL groupDetailsByChatgroupidUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/chatgroups/" + chatgroupIDs.toString());
			objectNode = HTTPClientUtils.sendHTTPRequest(groupDetailsByChatgroupidUrl, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍒涘缓缇ょ粍
	 * 
	 */
	public static ObjectNode creatChatGroups(ObjectNode dataObjectNode) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		// check properties that must be provided
		if (!dataObjectNode.has("groupname")) {
			objectNode.put("message", "Property that named groupname must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("desc")) {
			objectNode.put("message", "Property that named desc must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("public")) {
			objectNode.put("message", "Property that named public must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("approval")) {
			objectNode.put("message", "Property that named approval must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("owner")) {
			objectNode.put("message", "Property that named owner must be provided .");
			return objectNode;
		}
		if (!dataObjectNode.has("members") || !dataObjectNode.path("members").isArray()) {
			objectNode.put("message", "Property that named members must be provided .");
			return objectNode;
		}

		try {
			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.CHATGROUPS_URL, credential, dataObjectNode,
					HTTPMethod.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍒犻櫎缇ょ粍
	 * 
	 */
	public static ObjectNode deleteChatGroups(String chatgroupid) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL deleteChatGroupsUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid);
			objectNode = HTTPClientUtils.sendHTTPRequest(deleteChatGroupsUrl, credential, null,
					HTTPMethod.METHOD_DELETE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鑾峰彇缇ょ粍涓殑鎵�湁鎴愬憳
	 * 
	 */
	public static ObjectNode getAllMemberssByGroupId(String chatgroupid) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL allMemberssByGroupIdUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid + "/users");
			objectNode = HTTPClientUtils.sendHTTPRequest(allMemberssByGroupIdUrl, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍦ㄧ兢缁勪腑娣诲姞涓�釜浜�	 * 
	 */
	public static ObjectNode addUserToGroup(String chatgroupid, String userName) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL allMemberssByGroupIdUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid + "/users/" + userName);
			ObjectNode dataobjectNode = factory.objectNode();
			objectNode = HTTPClientUtils.sendHTTPRequest(allMemberssByGroupIdUrl, credential, dataobjectNode,
					HTTPMethod.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍦ㄧ兢缁勪腑鍑忓皯涓�釜浜�	 * 
	 */
	public static ObjectNode deleteUserFromGroup(String chatgroupid, String userName) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}

		try {
			URL allMemberssByGroupIdUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/chatgroups/"
					+ chatgroupid + "/users/" + userName);
			objectNode = HTTPClientUtils.sendHTTPRequest(allMemberssByGroupIdUrl, credential, null,
					HTTPMethod.METHOD_DELETE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 鑾峰彇涓�釜鐢ㄦ埛鍙備笌鐨勬墍鏈夌兢缁�	 * 
	 * @param username
	 * @return
	 */
	private static ObjectNode getJoinedChatgroupsForIMUser(String username) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}
		if (StringUtils.isBlank(username.trim())) {
			objectNode.put("message", "Property that named username must be provided .");
			return objectNode;
		}

		try {
			URL getJoinedChatgroupsForIMUserUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/users/" + username + "/joined_chatgroups");
			objectNode = HTTPClientUtils.sendHTTPRequest(getJoinedChatgroupsForIMUserUrl, credential, null,
					HTTPMethod.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 缇ょ粍鎵归噺娣诲姞鎴愬憳
	 * 
	 * @param toAddBacthChatgroupid
	 * @param usernames
	 * @return
	 */
	private static ObjectNode addUsersToGroupBatch(String toAddBacthChatgroupid, ObjectNode usernames) {
		ObjectNode objectNode = factory.objectNode();
		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			objectNode.put("message", "Bad format of Appkey");
			return objectNode;
		}
		if (StringUtils.isBlank(toAddBacthChatgroupid.trim())) {
			objectNode.put("message", "Property that named toAddBacthChatgroupid must be provided .");
			return objectNode;
		}
		// check properties that must be provided
		if (null != usernames && !usernames.has("usernames")) {
			objectNode.put("message", "Property that named usernames must be provided .");
			return objectNode;
		}

		try {
			URL getJoinedChatgroupsForIMUserUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/chatgroups/" + toAddBacthChatgroupid + "/users");
			objectNode = HTTPClientUtils.sendHTTPRequest(getJoinedChatgroupsForIMUserUrl, credential, usernames,
					HTTPMethod.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

}
