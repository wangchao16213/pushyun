package com.common.tools;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;


public class MD5Encrypt {

    public final static String MD5(String s) {
	char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f' };
	try {
	    byte[] strTemp = s.getBytes();
	    MessageDigest mdTemp = MessageDigest.getInstance("MD5");
	    mdTemp.update(strTemp);
	    byte[] md = mdTemp.digest();
	    int j = md.length;
	    char str[] = new char[j * 2];
	    int k = 0;
	    for (int i = 0; i < j; i++) {
		byte byte0 = md[i];
		str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		str[k++] = hexDigits[byte0 & 0xf];
	    }
	    return new String(str);
	} catch (Exception e) {
	    return null;
	}
    }

    public static void main(String[] args) {
	// MD5_Test aa = new MD5_Test();
//    	System.out.println(new String(Base64.decodeBase64("123456")));  
	System.out.println(MD5Encrypt.MD5(MD5Encrypt.MD5("1234")));
//	System.out.println(MD5Encrypt.MD5("1244555"));
	
//	System.out.println(MD5Encrypt.MD5(new String(Base64.decodeBase64("chenyuxia2"))));
    }
}
