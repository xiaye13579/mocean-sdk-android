/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

	public static String scrape(String resp, String start, String stop) {
		int offset, len;
		if((offset = resp.indexOf(start)) < 0)
			return "";
		if((len = resp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String md5(String data) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			digester.update(data.getBytes());
			byte[] messageDigest = digester.digest();
			return Utils.byteArrayToHexString(messageDigest);
		} catch(NoSuchAlgorithmException e) {			
		}
		return null;
	}
	
	public static String byteArrayToHexString(byte[] array) {
		StringBuffer hexString = new StringBuffer();
		for (byte b : array) {
			int intVal = b & 0xff;
			if (intVal < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(intVal));
		}
		return hexString.toString();		
	}
	
}
