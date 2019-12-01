package com.nut.teamradar.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import com.nut.teamradar.Constant;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradarlib.TeamRadarAPI;

public class Encrypt {
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    
    public static String MD5Encode(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 
        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', 
        '4', '5', '6', '7', '8', '9', '+', '/' }; 

    private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 
        60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, 
        -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 
        38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, 
        -1, -1 }; 
    private static String encode(byte[] data) { 
        StringBuffer sb = new StringBuffer(); 
        int len = data.length; 
        int i = 0; 
        int b1, b2, b3; 
        while (i < len) { 
            b1 = data[i++] & 0xff; 
            if (i == len) { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]); 
                sb.append("=="); 
                break; 
            } 
            b2 = data[i++] & 0xff; 
            if (i == len) { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) 
                        | ((b2 & 0xf0) >>> 4)]); 
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]); 
                sb.append("="); 
                break; 
            } 
            b3 = data[i++] & 0xff; 
            sb.append(base64EncodeChars[b1 >>> 2]); 
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) 
                    | ((b2 & 0xf0) >>> 4)]); 
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) 
                    | ((b3 & 0xc0) >>> 6)]); 
            sb.append(base64EncodeChars[b3 & 0x3f]); 
        } 
        return sb.toString(); 
    } 
    public static byte[] decode(String str) { 
        byte[] data = str.getBytes(); 
        int len = data.length; 
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len); 
        int i = 0; 
        int b1, b2, b3, b4; 
  
        while (i < len) { 
  
            do { 
                b1 = base64DecodeChars[data[i++]]; 
            } while (i < len && b1 == -1); 
            if (b1 == -1) { 
                break; 
            } 
  
            do { 
                b2 = base64DecodeChars[data[i++]]; 
            } while (i < len && b2 == -1); 
            if (b2 == -1) { 
                break; 
            } 
            buf.write((int) ((b1 << 2) | ((b2 & 0x30) >>> 4))); 
  
            do { 
                b3 = data[i++]; 
                if (b3 == 61) { 
                    return buf.toByteArray(); 
                } 
                b3 = base64DecodeChars[b3]; 
            } while (i < len && b3 == -1); 
            if (b3 == -1) { 
                break; 
            } 
            buf.write((int) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2))); 
  
            do { 
                b4 = data[i++]; 
                if (b4 == 61) { 
                    return buf.toByteArray(); 
                } 
                b4 = base64DecodeChars[b4]; 
            } while (i < len && b4 == -1); 
            if (b4 == -1) { 
                break; 
            } 
            buf.write((int) (((b3 & 0x03) << 6) | b4)); 
        } 
        return buf.toByteArray(); 
    }     
    public static String Base64Encode(String strObj)
    {
    	String result = null;
    	try {
    		result = encode(strObj.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }
    
    public static String Base64Decode(String strObj)
    {
    	String result = new String(decode(strObj));
    	return result;
    }
    
    public static String GetEncryptString(String str)
    {
    	String OutStr;
    	/*OutStr = Base64Encode(MD5Encode(str));
    	OutStr = OutStr.substring(0, OutStr.length()-1).toLowerCase();*/
    	OutStr = SecurityEncode.encoderByDES(str, TeamRadarAPI.getInstance().getSecurityCode());
    	return OutStr;
    }
    public static String GetDecryptString(String str)
    {
    	String OutStr;
    	/*OutStr = Base64Encode(MD5Encode(str));
    	OutStr = OutStr.substring(0, OutStr.length()-1).toLowerCase();*/
    	OutStr = SecurityEncode.decoderByDES(str, TeamRadarAPI.getInstance().getSecurityCode());
    	return OutStr;
    }
    /*private Key key;
    private byte[] byteMi = null;
    private byte[] byteMing = null;
    private String strMi= "";
    private String strM= ""; 
    public void setKey(String strKey){ 
	   try{  
	        KeyGenerator _generator = KeyGenerator.getInstance("DES");  
	        _generator.init(new SecureRandom(strKey.getBytes()));  
	        this.key = _generator.generateKey();  
	        _generator=null;
	        }
	    catch(Exception e){
	     e.printStackTrace();
	     }
	}  
    public void setEncString(String strMing){
		BASE64Encoder base64en = new BASE64Encoder();  
	    try {
	    	this.byteMing = strMing.getBytes("UTF8");  
	    	this.byteMi = this.getEncCode(this.byteMing);  
	    	this.strMi = base64en.encode(this.byteMi);
	    }  
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	     	this.byteMing = null;  
	    	this.byteMi = null;
	    }
	}    
    private byte[] getEncCode(byte[] byteS){
	   byte[] byteFina = null;  
	    Cipher cipher;  
	    try
	     {
	      cipher = Cipher.getInstance("DES");  
	      cipher.init(Cipher.ENCRYPT_MODE,key);  
	      byteFina = cipher.doFinal(byteS);
	      }  
	    catch(Exception e)
	     {
	      e.printStackTrace();
	      }  
	    finally
	    {
	     cipher = null;
	     }
	       
	   return byteFina;
	  } 
    public void setDesString(String strMi){  
	   BASE64Decoder base64De = new BASE64Decoder();   
	    try
	    {
	     this.byteMi = base64De.decodeBuffer(strMi);  
	      this.byteMing = this.getDesCode(byteMi);  
	      this.strM = new String(byteMing,"UTF8");  
	      }  
	    catch(Exception e)
	     {
	      e.printStackTrace();
	      }  
	    finally
	     {
	      base64De = null;  
	      byteMing = null;  
	      byteMi = null;
	      }  
	  
	  }
    private byte[] getDesCode(byte[] byteD){
    	Cipher cipher;  
	    byte[] byteFina=null;  
	    try{
	    	cipher = Cipher.getInstance("DES");  
	    	cipher.init(Cipher.DECRYPT_MODE,key);  
	    	byteFina = cipher.doFinal(byteD);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	    	cipher=null;
	    }  
	    return byteFina;
	  } 
    public String getStrMi()
    {
     return strMi;
    }
    public String getStrM()
    {
     return strM;
    }*/
}
