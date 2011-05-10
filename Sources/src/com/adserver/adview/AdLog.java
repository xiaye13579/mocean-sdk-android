package com.adserver.adview;

import android.util.Log;

public class AdLog {
	public static final int LOG_LEVEL_NONE =0;
	public static final int LOG_LEVEL_1 =1;
	public static final int LOG_LEVEL_2 =2;
	public static final int LOG_LEVEL_3 =3;
	
	public static final int LOG_TYPE_ERROR =1;
	public static final int LOG_TYPE_WARNING =2;
	public static final int LOG_TYPE_INFO =3;
	
	static int CurrentLogLevel = 0;
	
	public static void log(int Level, int Type, String tag, String msg)
	{
		if(Level<=CurrentLogLevel)
		{
			switch(Type)
			{
			case LOG_TYPE_ERROR: Log.e(tag, msg); break;
			case LOG_TYPE_WARNING: Log.w(tag, msg);break;
			default:
				Log.i(tag, msg);
			}
		}
	}

	public static void SetLogLevel(int logLevel)
	{
		CurrentLogLevel = logLevel;
	}
}
