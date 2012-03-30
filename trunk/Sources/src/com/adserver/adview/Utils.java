package com.MASTAdView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;

public class Utils {
	
	public static StateListDrawable GetSelector(Context context, String normal,String pressed, String disable)
	{
		StateListDrawable result = new StateListDrawable();
		
		result.addState((new int[] {-android.R.attr.state_pressed,
				android.R.attr.state_enabled}),GetDrawable(context,normal)); 
		
		if(pressed!=null) result.addState((new int[] {android.R.attr.state_pressed,
				android.R.attr.state_enabled}),GetDrawable(context,pressed)); 
		if(disable!=null) result.addState((new int[] {-android.R.attr.state_enabled}),GetDrawable(context,disable)); 
			else result.addState((new int[] {-android.R.attr.state_enabled}),GetDrawable(context,normal));
		
		return result;
	}
	
	public static Drawable GetDrawable(Context context, String fileName)
	{
		Drawable result = null;
		try {
			result = Drawable.createFromStream(context.getAssets().open(fileName), null);
		} catch (IOException e) {
		}
		try {
			int density = context.getResources().getDisplayMetrics().densityDpi;
			int screenLayout = context.getResources().getConfiguration().screenLayout;
			if ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
				if (density == DisplayMetrics.DENSITY_HIGH) {
					return Drawable.createFromStream(context.getAssets().open("xlarge/"+fileName), null);
				} else if (density == DisplayMetrics.DENSITY_MEDIUM) {
					return Drawable.createFromStream(context.getAssets().open("large/"+fileName), null);
				} else if (density == DisplayMetrics.DENSITY_LOW) {
					return Drawable.createFromStream(context.getAssets().open("large/"+fileName), null);
				} else {
					return Drawable.createFromStream(context.getAssets().open("xlarge/"+fileName), null);
				}
			} else if ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
				if (density == DisplayMetrics.DENSITY_HIGH) {
					return Drawable.createFromStream(context.getAssets().open("large/"+fileName), null);
				} else if (density == DisplayMetrics.DENSITY_MEDIUM) {
					return Drawable.createFromStream(context.getAssets().open("normal/"+fileName), null);
				} else if (density == DisplayMetrics.DENSITY_LOW) {
					return Drawable.createFromStream(context.getAssets().open("normal/"+fileName), null);
				} else {
					return Drawable.createFromStream(context.getAssets().open("large/"+fileName), null);
				}
			} else if ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				if (density == DisplayMetrics.DENSITY_HIGH) {
					return Drawable.createFromStream(context.getAssets().open("normal/"+fileName), null);
				} else if (density == DisplayMetrics.DENSITY_MEDIUM) {
					return Drawable.createFromStream(context.getAssets().open("small/"+fileName), null);
				} else if (density == DisplayMetrics.DENSITY_LOW) {
					return Drawable.createFromStream(context.getAssets().open("small/"+fileName), null);
				} else {
					return Drawable.createFromStream(context.getAssets().open("normal/"+fileName), null);
				}
			} else if ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
				return Drawable.createFromStream(context.getAssets().open("small/"+fileName), null);
			} else {
				return Drawable.createFromStream(context.getAssets().open("normal/"+fileName), null);
			}			
		} catch (IOException e) {
		}
		return result;
	}

	public static String scrape(String resp, String start, String stop) {
		int offset, len;
		if((offset = resp.indexOf(start)) < 0)
			return "";
		if((len = resp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String scrapeIgnoreCase(String resp, String start, String stop) {
		int offset, len;
		String temp = resp.toLowerCase();
		start = start.toLowerCase();
		stop = stop.toLowerCase();
		
		if((offset = temp.indexOf(start)) < 0)
			return "";
		if((len = temp.indexOf(stop, offset + start.length())) < 0)
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
