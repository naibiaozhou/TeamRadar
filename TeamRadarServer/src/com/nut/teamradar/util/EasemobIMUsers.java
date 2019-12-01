package com.nut.teamradar.util;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST API Demo :鐢ㄦ埛浣撶郴闆嗘垚 REST API HttpClient4.3瀹炵幇
 * 
 * Doc URL: http://www.easemob.com/docs/rest/userapi
 * 
 * @author Lynch 2014-09-15
 * 
 */
public class EasemobIMUsers {

	private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    // 閫氳繃app鐨刢lient_id鍜宑lient_secret鏉ヨ幏鍙朼pp绠＄悊鍛榯oken
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);

    

    /**
	 * 娉ㄥ唽IM鐢ㄦ埛[鍗曚釜]
	 * 
	 * 缁欐寚瀹欳onstants.APPKEY鍒涘缓涓�釜鏂扮殑鐢ㄦ埛
	 * 
	 * @param dataNode
	 * @return
	 */
	public static ObjectNode createNewIMUserSingle(ObjectNode dataNode) {

		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		objectNode.removeAll();

		// check properties that must be provided
		if (null != dataNode && !dataNode.has("username")) {

			objectNode.put("message", "Property that named username must be provided .");

			return objectNode;
		}
		if (null != dataNode && !dataNode.has("password")) {
			objectNode.put("message", "Property that named password must be provided .");

			return objectNode;
		}

		try {

		    objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.USERS_URL, credential, dataNode,
					HTTPMethod.METHOD_POST);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 娉ㄥ唽IM鐢ㄦ埛[鎵归噺]
	 * 
	 * 缁欐寚瀹欳onstants.APPKEY鍒涘缓涓�壒鐢ㄦ埛
	 * 
	 * @param dataArrayNode
	 * @return
	 */
	public static ObjectNode createNewIMUserBatch(ArrayNode dataArrayNode) {

		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		// check properties that must be provided
		if (dataArrayNode.isArray()) {
			for (JsonNode jsonNode : dataArrayNode) {
				if (null != jsonNode && !jsonNode.has("username")) {

					objectNode.put("message", "Property that named username must be provided .");

					return objectNode;
				}
				if (null != jsonNode && !jsonNode.has("password")) {

					objectNode.put("message", "Property that named password must be provided .");

					return objectNode;
				}
			}
		}

		try {

			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.USERS_URL, credential, dataArrayNode,
					HTTPMethod.METHOD_POST);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 娉ㄥ唽IM鐢ㄦ埛[鎵归噺鐢熸垚鐢ㄦ埛鐒跺悗娉ㄥ唽]
	 * 
	 * 缁欐寚瀹欳onstants.APPKEY鍒涘缓涓�壒鐢ㄦ埛
	 * 
	 * @param usernamePrefix
	 *            鐢熸垚鐢ㄦ埛鍚嶇殑鍓嶇紑
	 * @param perNumber
	 *            鎵归噺娉ㄥ唽鏃朵竴娆℃敞鍐岀殑鏁伴噺
	 * @param totalNumber
	 *            鐢熸垚鐢ㄦ埛娉ㄥ唽鐨勭敤鎴锋�鏁�	 * @return
	 */
	public static ObjectNode createNewIMUserBatchGen(String usernamePrefix, Long perNumber, Long totalNumber) {
		ObjectNode objectNode = factory.objectNode();

		if (totalNumber == 0 || perNumber == 0) {
			return objectNode;
		}
		//System.out.println("浣犲嵆灏嗘敞鍐� + totalNumber + "涓敤鎴凤紝濡傛灉澶т簬" + perNumber + "浜�浼氬垎鎵规敞鍐�姣忔娉ㄥ唽" + perNumber + "涓�);
		ArrayNode genericArrayNode = EasemobIMUsers.genericArrayNode(usernamePrefix, totalNumber);
		if (totalNumber <= perNumber) {
			objectNode = EasemobIMUsers.createNewIMUserBatch(genericArrayNode);
		} else {

			ArrayNode tmpArrayNode = factory.arrayNode();
			
			for (int i = 0; i < genericArrayNode.size(); i++) {
				tmpArrayNode.add(genericArrayNode.get(i));
				// 300 records on one migration
				if ((i + 1) % perNumber == 0) {
					objectNode = EasemobIMUsers.createNewIMUserBatch(tmpArrayNode);
					if(objectNode!=null){
					}
					tmpArrayNode.removeAll();
					continue;
				}

				// the rest records that less than the times of 300
				if (i > (genericArrayNode.size() / perNumber * perNumber - 1)) {
					objectNode = EasemobIMUsers.createNewIMUserBatch(tmpArrayNode);
					if(objectNode!=null){
					}
					tmpArrayNode.removeAll();
				}
			}
		}

		return objectNode;
	}

	/**
	 * 鑾峰彇IM鐢ㄦ埛
	 * 
	 * @param userName
	 *            鐢ㄦ埛涓婚敭锛歶sername鎴栬�uuid
	 * @return
	 */
	public static ObjectNode getIMUsersByUserName(String userName) {
		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		// check properties that must be provided
		if (StringUtils.isEmpty(userName)) {

			objectNode.put("message", "The userName that will be used to query must be provided .");

			return objectNode;
		}

		try {

			URL userPrimaryUrl = HTTPClientUtils
					.getURL(Constants.APPKEY.replace("#", "/") + "/users/" + userName);
			objectNode = HTTPClientUtils.sendHTTPRequest(userPrimaryUrl, credential, null, HTTPMethod.METHOD_GET);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍒犻櫎IM鐢ㄦ埛[鍗曚釜]
	 * 
	 * 鍒犻櫎鎸囧畾Constants.APPKEY涓婭M鍗曚釜鐢ㄦ埛
	 *
	 * 
	 * @param userName
	 * @return
	 */
	public static ObjectNode deleteIMUserByuserName(String userName) {
		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		try {

			URL deleteUserPrimaryUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users/"
					+ userName);
			objectNode = HTTPClientUtils.sendHTTPRequest(deleteUserPrimaryUrl, credential, null,
					HTTPMethod.METHOD_DELETE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 鍒犻櫎IM鐢ㄦ埛[鎵归噺]
	 * 
	 * 鎵归噺鎸囧畾Constants.APPKEY涓嬪垹闄M鐢ㄦ埛
	 * 
	 * @param limit
	 * @param queryStr
	 * @return
	 */
	public static ObjectNode deleteIMUserByUsernameBatch(Long limit) {

		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		try {

			URL deleteIMUserByUsernameBatchUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users"
					+ "?limit=" + limit);
			objectNode = HTTPClientUtils.sendHTTPRequest(deleteIMUserByUsernameBatchUrl, credential, null,
					HTTPMethod.METHOD_DELETE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 閲嶇疆IM鐢ㄦ埛瀵嗙爜 鎻愪緵绠＄悊鍛榯oken
	 * 
	 * @param userName
	 * @param dataObjectNode
	 * @return
	 */
	public static ObjectNode modifyIMUserPasswordWithAdminToken(String userName, ObjectNode dataObjectNode) {
		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		if (StringUtils.isEmpty(userName)) {
			objectNode.put("message",
					"Property that named userName must be provided锛宼he value is username or imuser.");

			return objectNode;
		}

		if (null != dataObjectNode && !dataObjectNode.has("newpassword")) {
			
			objectNode.put("message", "Property that named newpassword must be provided .");

			return objectNode;
		}

		try {
			URL modifyIMUserPasswordWithAdminTokenUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/")
					+ "/users/" + userName + "/password");
			objectNode = HTTPClientUtils.sendHTTPRequest(modifyIMUserPasswordWithAdminTokenUrl, credential,
					dataObjectNode, HTTPMethod.METHOD_PUT);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 娣诲姞濂藉弸[鍗曚釜]
	 * 
	 * @param ownerUserName
	 * @param friendUserName
	 * 
	 * @return
	 */
	public static ObjectNode addFriendSingle(String ownerUserName, String friendUserName) {
		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {
			
			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		if (StringUtils.isEmpty(ownerUserName)) {
			
			objectNode.put("message", "Your userName must be provided锛宼he value is username of imuser.");

			return objectNode;
		}

		if (StringUtils.isEmpty(friendUserName)) {
			
			objectNode.put("message",
					"The userName of friend must be provided锛宼he value is username of imuser.");

			return objectNode;
		}

		try {

			URL addFriendSingleUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users/"
					+ ownerUserName + "/contacts/users/" + friendUserName);

			ObjectNode body = factory.objectNode();
			objectNode = HTTPClientUtils.sendHTTPRequest(addFriendSingleUrl, credential, body, HTTPMethod.METHOD_POST);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 鍒犻櫎濂藉弸
	 * 
	 * @param ownerUserName
	 * @param friendUserName
	 * 
	 * @return
	 */
	public static ObjectNode deleteFriendSingle(String ownerUserName, String friendUserName) {
		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {
			
			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		if (StringUtils.isEmpty(ownerUserName)) {
			
			objectNode.put("message", "Your userName must be provided锛宼he value is username of imuser.");

			return objectNode;
		}

		if (StringUtils.isEmpty(friendUserName)) {
			
			objectNode.put("message",
					"The userName of friend must be provided锛宼he value is username of imuser.");

			return objectNode;
		}

		try {
			URL addFriendSingleUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users/"
					+ ownerUserName + "/contacts/users/" + friendUserName);

			ObjectNode body = factory.objectNode();
			objectNode = HTTPClientUtils.sendHTTPRequest(addFriendSingleUrl, credential, body, HTTPMethod.METHOD_DELETE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛鎵�湁濂藉弸
	 * 
	 * @param ownerUserName
	 * 
	 * @return
	 */
	public static ObjectNode getFriends(String ownerUserName) {
		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		if (StringUtils.isEmpty(ownerUserName)) {

			objectNode.put("message", "Your userName must be provided锛宼he value is username of imuser.");

			return objectNode;
		}

		try {
			
			URL addFriendSingleUrl = HTTPClientUtils.getURL(Constants.APPKEY.replace("#", "/") + "/users/"
					+ ownerUserName + "/contacts/users");

			ObjectNode body = factory.objectNode();
			objectNode = HTTPClientUtils.sendHTTPRequest(addFriendSingleUrl, credential, body, HTTPMethod.METHOD_GET);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * IM鐢ㄦ埛鐧诲綍
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static ObjectNode imUserLogin(String username, String password) {

		ObjectNode objectNode = factory.objectNode();

		// check appKey format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", Constants.APPKEY)) {

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}
		if (StringUtils.isEmpty(username)) {

			objectNode.put("message", "Your username must be provided锛宼he value is username of imuser.");

			return objectNode;
		}
		if (StringUtils.isEmpty(password)) {

			objectNode.put("message", "Your password must be provided锛宼he value is username of imuser.");

			return objectNode;
		}

		try {
			ObjectNode dataNode = factory.objectNode();
			dataNode.put("grant_type", "password");
			dataNode.put("username", username);
			dataNode.put("password", password);

			objectNode = HTTPClientUtils.sendHTTPRequest(EndPoints.TOKEN_APP_URL, null, dataNode,
					HTTPMethod.METHOD_POST);

		} catch (Exception e) {
			throw new RuntimeException("Some errors occurred while fetching a token by username and password .");
		}

		return objectNode;
	}

	/*************************************************************************************************************************/
	/**
	 * 鎸囧畾鍓嶇紑鍜屾暟閲忕敓鎴愮敤鎴峰熀鏈暟鎹�	 * 
	 * @param usernamePrefix
	 * @param number
	 * @return
	 */
	public static ArrayNode genericArrayNode(String usernamePrefix, Long number) {
		ArrayNode arrayNode = factory.arrayNode();
		for (int i = 0; i < number; i++) {
			ObjectNode userNode = factory.objectNode();
			userNode.put("username", usernamePrefix + "_" + i);
			userNode.put("password", Constants.DEFAULT_PASSWORD);

			arrayNode.add(userNode);
		}

		return arrayNode;
	}

}
