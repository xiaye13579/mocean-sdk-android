//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import android.os.Environment;
import android.util.Log;
import android.view.View;

/**
 * The SDK is instrumented with log() calls to record errors, warnings and server traffic
 * for diagnostic purposes. A small buffer of log events are kept in a memory buffer, and
 * events are also sent to the system log (accessible with the logcat interface.) When
 * reporting problem it is helpful to capture this diagnostic information to aid with
 * debugging and resolution.
 */
public class MASTAdLog {
	
	/**
	 * Log level value to turn logging off. The default.
	 */
	public static final int LOG_LEVEL_NONE =0;
	
	/**
	 * Log level value to log errors only.
	 */
	public static final int LOG_LEVEL_1 =1;
	
	/**
	 * Log level value to log errors and warnings.
	 */
	public static final int LOG_LEVEL_2 =2;
	
	/**
	 * Log level value to log errors, warnings and server traffic.
	 */
	public static final int LOG_LEVEL_3 =3;
	
	/**
	 * Log value associated with errors.
	 */
	public static final int LOG_TYPE_ERROR =1;
	
	/**
	 * Log value associated with warnings. 
	 */
	public static final int LOG_TYPE_WARNING =2;
	
	/**
	 * Log value associated with misc. diagnostics information (server traffic, etc.)
	 */
	public static final int LOG_TYPE_INFO =3;
	
	private int CurrentLogLevel = 0;
	//private String AppName="";
	private static String defaultLogFileName =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/mOcean-sample-log.txt";
	private static boolean loggingToFile = false;
	private View adView;
	
	private static int DefaultLevel = LOG_LEVEL_NONE;
	
	// Maximum number of messages to keep in memory for review access through the app;
	// keep this small by default to conserve memory usage. When debugging apps, callers
	// can increase this value.
	private static int maximumInMemoryLogCount = 200;
	
	// Store all diag info in private internal vector of strings
    private static Vector<String> inMemoryLog = null;
    
    
    /**
     * Set default log level to one of the log level values defined in he MASTAdLog class
     * (corresponding to errors, errors + warnings, or everything including server traffic.)
     * 
     * @param logLevel Int log level to set as the default value (initially NONE).
     */
	public static void setDefaultLogLevel(int logLevel)
	{
		DefaultLevel = logLevel;
		if ((logLevel > 0) && (loggingToFile == false))
		{
			setFileLog(defaultLogFileName); // log to default file
		}
	}
	
	/**
	 * Enable debug logging to the named file.
	 * @param fileName String file name, must be a full pathname to a writable file.
	 */
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
			//System.out.println("Logging to file: " + fileName);
		} catch (IOException e) { 
				e.printStackTrace(); 
		}
	}

	/**
	 * Construct logging object.
	 * @param adView Base view object associated with logging
	 */
	public MASTAdLog(View adView)
	{
		this.adView =  adView;
		setLogLevel(DefaultLevel);
	}
	
	/**
	 * Log a message
	 * @param Level Int logging level, from 0 (none) to 3 (errors, warnings and server traffic)
	 * @param Type Log Int message type, 1 (error, 2 (warning) or 3 (info) 
	 * @param tag String log message tag
	 * @param msg String log message detail
	 */
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
			
			logInternal(resultTag + msg);
		}
	}

	/**
	 * Set log message level to be recorded; log events at a higher level are ignored.
	 * @param logLevel Int log level to be recorded
	 */
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
	

	private void logInternal(String message)
	{
		if (inMemoryLog == null)
		{
			inMemoryLog = new Vector<String>();
		}
		
		if (message != null)
        {
			inMemoryLog.addElement(message);
        }
		
		int currentSize = inMemoryLog.size();
        while (currentSize >= maximumInMemoryLogCount)
        {
            // Need to clean up some old messages first.
        	inMemoryLog.removeElementAt(0);
        	currentSize--;
        }
	}

	/**
	 * Get maximum number of messages to keep in memory
	 * @return Current count setting
	 */
	public static int getMaximumlogCount()
	{
		return maximumInMemoryLogCount;
	}
	
	/**
	 * Set maximum number of messages to keep in memory
	 * @param value Maximum message count
	 */
	public static void setMaximumLogCount(int value)
	{
		maximumInMemoryLogCount = value;
	}
	
	/**
	 * Get reference to in-memory log informatoin
	 * @return Vector of log message strings
	 */
	public static Vector<String> getInternalLogs()
	{
		return inMemoryLog;
	}

	/**
	 * Clear internal log messages
	 */
	public static void clearInternalLogs()
	{
		if (inMemoryLog != null)
		{
			inMemoryLog.clear();
		}
	}
}
