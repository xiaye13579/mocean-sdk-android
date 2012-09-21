//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.MASTAdView.MASTAdConstants;
import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdRequest;

public class ContentManager
{
	private static final String INSTALLATION = "INSTALLATION";
	private String autoDetectParameters = "";
	private String userAgent = "";
	private static ContentManager instance;
	private static boolean isSimAvailable;
	private HashMap<ContentConsumer, ContentParameters> senderParameters = new HashMap<ContentConsumer, ContentParameters>();
	private String id = null;
	private boolean useSystemDeviceId = false;
	private Context context;
	private static boolean useBuiltinTestAds = false;
	private AdParser parser = new AdParser();

	
	static public ContentManager getInstance(ContentConsumer consumer)
	{
		if (instance == null)
			instance = new ContentManager(consumer);
		
		return instance;
	}


	// Calers must implement this interface to provide the needed data
	public static interface ContentConsumer
	{
		public String getUserAgent();
		public Context getContext();
		public boolean setResult(AdData ad);
	}
	

	/*
	public static boolean setUseBuiltinTestAds(Boolean flag)
	{
		if (flag == null)
		{
			return useBuiltinTestAds;
		}
		else
		{
			useBuiltinTestAds = flag;
			return flag;
		}
	}
	*/
	
	
	//private ContentManager(WebView webView) {
	private ContentManager(ContentConsumer consumer)
	{
		userAgent = consumer.getUserAgent();
		this.context = consumer.getContext().getApplicationContext();
		runInitDefaultParameters();
	}

	
	private void runInitDefaultParameters()
	{
		Thread thread = new Thread() {
			@Override
			public void run() {
				initDefaultParameters();
			}
		};
		thread.setName("[ContentManager] InitDefaultParameters");
		thread.start();
	}
	
	
	public String getAutoDetectParameters()
	{
		return autoDetectParameters;
	}
	
	
	public String getUserAgent() {
		return userAgent;
	}

	
	public static boolean isSimAvailable()
	{
		return isSimAvailable;
	}

	
	public void startLoadContent(ContentConsumer consumer, String url)
	{
		if (senderParameters.containsKey(consumer))
			stopLoadContent(consumer);

		ContentParameters parameters = new ContentParameters();
		parameters.sender = consumer;
		parameters.url = url;
		//parameters.w = w;
		//parameters.h = h;

		senderParameters.put(consumer, parameters);

		ContentThread cTh = new ContentThread(parameters);
		parameters.cTh = cTh;
		cTh.setName("[ContentManager] LoadContent");
		cTh.start();
	}

	
	public void stopLoadContent(ContentConsumer consumer)
	{
		if (senderParameters.containsKey(consumer))
		{
			senderParameters.get(consumer).sender = null;
			senderParameters.get(consumer).cTh.cancel();
			senderParameters.remove(consumer);
		}
	}
	
	
	private class ContentParameters
	{
		public String url;
		public ContentConsumer sender;
		ContentThread cTh;
	};

	
	private class ContentThread extends Thread
	{
		ContentParameters parameters;
		boolean isCanceled = false;
		
		public ContentThread(ContentParameters parameters)
		{
			this.parameters = parameters;
		}

		@Override
		public void run() {
			try {
				
				// Optionally use built-in ads for testing
				/*
				if (useBuiltinTestAds)
				{
					String adText = null;
					
					int random = (int)(System.currentTimeMillis() % 3); // 0 or 1 or 2
					if (random == 0)
					{
						// Text
						adText = Utils.readRawAsset(context, R.raw.test_text_xml);
					}
					else if (random == 1)
					{
						// Image
						adText = Utils.readRawAsset(context, R.raw.test_image_xml);
					}
					else
					{
						// Richmedia
						adText = Utils.readRawAsset(context, R.raw.test_richmedia_xml);
					}
					
					if (adText != null)
					{
						AdParser parser = new AdParser();
						AdData ad = parser.parse(adText);
						if (parameters.sender != null)
						{
							parameters.sender.setResult(ad);
							return;
						}
					}
				}
				*/
				
				System.setProperty("http.keepAlive", "false");
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(parameters.url);
				get.addHeader("User-Agent", userAgent);	
				get.addHeader("Connection","close");

				HttpConnectionParams.setConnectionTimeout(get.getParams(), MASTAdConstants.AD_RELOAD_PERIOD);
				HttpConnectionParams.setSoTimeout(get.getParams(), MASTAdConstants.DEFAULT_REQUEST_TIMEOUT);				
				HttpResponse response = client.execute(get);
				
				if(response.getStatusLine().getStatusCode()!=200)
				{
					setErrorResult("Response code = "+String.valueOf(response.getStatusLine().getStatusCode()));
					stopLoadContent(parameters.sender);
					return;
				}
				
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);				
				String responseValue="";
				
				if(!isCanceled)
				{
					responseValue = readInputStream(bufferedInputStream);
				}
				
				bufferedInputStream.close();
				inputStream.close();
				
				//AdParser parser = new AdParser();
				AdData ad = parser.parseAdData(responseValue);
				if (isCanceled)
				{
					ad.error = "Canceled";
				}
				if (parameters.sender != null)
				{
					parameters.sender.setResult(ad);
				}
			}
			catch (ClientProtocolException e)
			{
				setErrorResult(e.toString() + ": " + e.getMessage());
			}
			catch (IOException e)
			{
				setErrorResult(e.toString() + ": " + e.getMessage());
			}

			stopLoadContent(parameters.sender);
		}
		
		
		private void setErrorResult(String message)
		{
			if (parameters.sender != null)
			{
				AdData error = new AdData();
				error.error = message;
				parameters.sender.setResult(error);
			}
		}

		
		public void cancel()
		{
			isCanceled = true;
		}
		
		
		private String readInputStream(BufferedInputStream in) throws IOException 
		{
			byte[] buffer = new byte[1024];
			ByteArrayBuffer byteBuffer = new ByteArrayBuffer(1);
			for (int n; (n = in.read(buffer)) != -1;)
			{
				if(isCanceled) return "";
				byteBuffer.append(buffer, 0, n);
			}
			return new String(byteBuffer.buffer(),0,byteBuffer.length());			
		}
	}

	
	public String getDeviceId()
	{
		return getDeviceId((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
	}
	
	// Return user-specified device ID value if any, otherwise unique device ID from
	// phone if that option has been enabled.
	private String getDeviceId(TelephonyManager tm)
	{
		if (id != null)
		{
			return id;
		}
		
		String deviceId = null;
		if (useSystemDeviceId)
		{
			String tempDeviceId = tm.getDeviceId();
	
			if (null != tempDeviceId) {
				deviceId = tempDeviceId;
			} else {
				tempDeviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	
				if (null != tempDeviceId) {
					deviceId = tempDeviceId;
				} else {
					deviceId = makeDeviceId(context);
				}
			}
		}

		id = deviceId;
		return deviceId;
	}
	
	private void initDefaultParameters()
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		isSimAvailable = tm.getSimState() > TelephonyManager.SIM_STATE_ABSENT;
		String deviceId = getDeviceId(tm);
		String deviceIdMd5 = null;
		if (deviceId != null)
		{
			deviceIdMd5 = FileUtils.md5(deviceId);
		}
		
		autoDetectParameters = "";
		
		if (tm != null) 
		{
			String networkOperator = tm.getNetworkOperator();      
			if ((networkOperator != null) && (networkOperator.length()>3)) 
			{         
				String mcc = networkOperator.substring(0, 3);   
				String mnc = networkOperator.substring(3);  
				autoDetectParameters += "&mcc=" + mcc;
				autoDetectParameters += "&mnc=" + mnc;
				
			} 
			//adserverRequest.setMCC(tm.getNetworkCountryIso());
			//tm.getNetworkOperator()
		}

		/*
		if ((deviceIdMd5 != null) && (deviceIdMd5.length() > 0))
		{
			autoDetectParameters += "&"+MASTAdRequest.parameter_device_id+"=" + deviceIdMd5;
		}
		*/
		
		Integer connectionSpeed = null;
    	ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
		if(networkInfo != null) {
			int type = networkInfo.getType();
			int subtype = networkInfo.getSubtype();
			
			//0 - low (gprs, edge), 1 - fast (3g, wifi)
			if(type == ConnectivityManager.TYPE_WIFI) {
				connectionSpeed = 1;
			} else if(type == ConnectivityManager.TYPE_MOBILE) {
				if(subtype == TelephonyManager.NETWORK_TYPE_EDGE) {
					connectionSpeed = 0;
				} else if(subtype == TelephonyManager.NETWORK_TYPE_GPRS) {
					connectionSpeed = 0;
				} else if(subtype == TelephonyManager.NETWORK_TYPE_UMTS) {
					connectionSpeed = 1;
				}
			}
		}
		
		if(connectionSpeed != null) {
			autoDetectParameters += "&connection_speed="+connectionSpeed.toString();
		}
	}

	private synchronized String makeDeviceId(Context context) {
		if (id == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				id = readInstallationFile(installation);
			} catch (Exception e) {
				id = "1234567890";
			}
		}
		
		return id;
	}

	
	public boolean getUseSystemDeviceId()
	{
		return useSystemDeviceId;
	}
	
	
	public void setUseSystemDeviceId(boolean value)
	{
		boolean changed = false;
		if (useSystemDeviceId != value)
		{
			changed = true;
		}
		useSystemDeviceId = value;
		
		if (changed)
		{
			runInitDefaultParameters();
		}
	}
	
	
	public void setDeviceId(String value)
	{
		boolean changed = false;
		if ((id != null) && (id.compareTo(value) != 0))
		{
			changed = true;
		}
		id = value;
		
		if (changed)
		{
			runInitDefaultParameters();
		}
	}
	
	private static String readInstallationFile(File installationFile) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installationFile, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installationFile) throws IOException {
		FileOutputStream out = new FileOutputStream(installationFile);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}
}
