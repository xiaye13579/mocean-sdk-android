package com.MASTAdView;

import java.io.File;
import java.io.IOException;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

public class MASTAdLog {
	public static final int LOG_LEVEL_NONE =0;
	public static final int LOG_LEVEL_1 =1;
	public static final int LOG_LEVEL_2 =2;
	public static final int LOG_LEVEL_3 =3;
	
	public static final int LOG_TYPE_ERROR =1;
	public static final int LOG_TYPE_WARNING =2;
	public static final int LOG_TYPE_INFO =3;
	
	int CurrentLogLevel = 0;
	String AppName="";
	private static String defaultLogFileName =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/mOcean-sample-log.txt";
	private static boolean loggingToFile = false;
	MASTAdViewCore adView;
	
	private static int DefaultLevel = LOG_LEVEL_NONE;
	
	public static void setDefaultLogLevel(int logLevel)
	{
		DefaultLevel = logLevel;
		if ((logLevel > 0) && (loggingToFile == false))
		{
			setFileLog(defaultLogFileName); // log to default file
		}
	}
	
	public static void setFileLog(String fileName)
	{
		if ((fileName == null) || (fileName.length() < 1))
		{
			return;
		}
		
		try {     
			File filename = new File(fileName);
			if (filename.exists()) filename.delete();
			filename.createNewFile();
			String cmd = "logcat -v time -f "+filename.getAbsolutePath(); // + " com.MASTAdView.";
			Runtime.getRuntime().exec(cmd);
			loggingToFile = true;
			//log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetFileLog","Logging to file: " + fileName);
			System.out.println("Logging to file: " + fileName);
		} catch (IOException e) { 
				e.printStackTrace(); 
		}
	}
	
	public MASTAdLog(MASTAdViewCore adView)
	{
		this.adView =  adView;
		setLogLevel(DefaultLevel);
	}
	
	public void log(int Level, int Type, String tag, String msg)
	{		
		String resultTag = "["+Integer.toHexString(adView.hashCode())+"]"+ tag;
		
		if(Level<=CurrentLogLevel)
		{
			switch(Type)
			{
			case LOG_TYPE_ERROR: Log.e(resultTag, msg+' '); break;
			case LOG_TYPE_WARNING: Log.w(resultTag, msg+' ');break;
			default:
				Log.i(resultTag, msg+' ');
			}
		}
	}

	public void setLogLevel(int logLevel)
	{
		CurrentLogLevel = logLevel;
		switch(logLevel)
		{
		case LOG_LEVEL_1:log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_1");break;
		case LOG_LEVEL_2:log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_2");break;
		case LOG_LEVEL_3:log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_3");break;
		default:
			log(LOG_LEVEL_1,LOG_TYPE_INFO,"SetLogLevel","LOG_LEVEL_NONE");
		}
		
		if ((logLevel > 0) && (loggingToFile == false))
		{
			setFileLog(defaultLogFileName); // log to default file
		}
	}
}
