package com.nut.teamradar.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomString {

    
	public static String getRandomString()
	{
		String RandStr = RandomStringUtils.randomAlphanumeric(32);
		
		return Encrypt.MD5Encode(RandStr);
	}
	public static String getRandomString16()
	{
		String RandStr = RandomStringUtils.randomAlphanumeric(16);
		
		return Encrypt.MD5Encode(RandStr);
	}

}
