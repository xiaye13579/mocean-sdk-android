package com.adserver.adview;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.webkit.WebView;

public class ContentManager {
	private static final String INSTALLATION = "INSTALLATION";
	private String autoDetectParameters = "";
	private String userAgent = "";
	private static ContentManager instance;
	private static boolean isSimAvailable;
	private HashMap<AdServerViewCore, ContentParameters> senderParameters = new HashMap<AdServerViewCore, ContentParameters>();
	private static String id = null;
	private Context context;

	static public ContentManager getInstance(WebView webView) {
		if (instance == null)
			instance = new ContentManager(webView);
		
		return instance;
	}

	private ContentManager(WebView webView) {
		userAgent = webView.getSettings().getUserAgentString();
		this.context = webView.getContext().getApplicationContext();
		Thread thread = new Thread() {
			@Override
			public void run() {
				initDefaultParameters();
			}
		};
		thread.setName("[ContentManager] InitDefaultParameters");
		thread.start();
	}
	
	class AsyncImpression
	{
		public String uri;
		public int counter = 0;		
	}
	
	public boolean sendImpr(String uri)
	{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		try {
			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode()!=200) return false;			
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public String getAutoDetectParameters() {
		return autoDetectParameters;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public static boolean isSimAvailable() {
		return isSimAvailable;
	}

	public void sendImpression(final String uri,final MASTAdLog adLog) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				if(!sendImpr(uri))
					adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_WARNING, Constants.STR_IMPRESSION_NOT_SEND, uri);
			}
		};
		thread.start();
	}

	public void startLoadContent(AdServerViewCore sender, String url/*, String w, String h*/) {
		if (senderParameters.containsKey(sender))
			stopLoadContent(sender);

		ContentParameters parameters = new ContentParameters();
		parameters.sender = sender;
		parameters.url = url;
		//parameters.w = w;
		//parameters.h = h;

		senderParameters.put(sender, parameters);

		ContentThread cTh = new ContentThread(parameters);
		parameters.cTh = cTh;
		cTh.setName("[ContentManager] LoadContent");
		cTh.start();
	}

	public void stopLoadContent(AdServerViewCore sender) {
		if (senderParameters.containsKey(sender)) {
			senderParameters.get(sender).sender = null;
			senderParameters.get(sender).cTh.cancel();
			senderParameters.remove(sender);
		}
	}

	public void installNotification(Integer advertiserId, String groupCode)
	{	
		InstallNotificationThread installNotificationThread = 
			new InstallNotificationThread(context, advertiserId, groupCode);
		installNotificationThread.start();
	}
	
	private class InstallNotificationThread extends Thread {
		private Context context;
		private Integer advertiserId;
		private String groupCode;

		public InstallNotificationThread(Context context, Integer advertiserId, String groupCode) {
			this.context = context;
			this.advertiserId = advertiserId;
			this.groupCode = groupCode;
		}

		@Override
		public synchronized void run() {
			try {
				if(context != null) {
					if((advertiserId != null) && (advertiserId > 0)
							&& (groupCode != null) && (groupCode.length() > 0)) {
						SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
						boolean isFirstAppLaunch = settings.getBoolean(Constants.PREF_IS_FIRST_APP_LAUNCH, true);

						if(isFirstAppLaunch) {
							String deviceId;
							TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
							String temp = tm.getDeviceId();
							if (null !=  temp) deviceId = temp;
							else {
								temp = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID); ;
								if (null != temp) deviceId = temp;
								else deviceId = "";
							}
							
							String deviceIdMD5 = Utils.md5(deviceId);
							
							if((deviceIdMD5 != null) && (deviceIdMD5.length() > 0)) {
								StringBuilder url = new StringBuilder(Constants.FIRST_APP_LAUNCH_URL);
								url.append("?advertiser_id=" + advertiserId.toString());
								url.append("&group_code=" + URLEncoder.encode(groupCode));
								url.append("&"+AdserverRequest.parameter_device_id+"=" + URLEncoder.encode(deviceIdMD5));
								
								sendImpr(url.toString());
								
								SharedPreferences.Editor editor = settings.edit();
								editor.putBoolean(Constants.PREF_IS_FIRST_APP_LAUNCH, false);
								editor.commit();
							}
						}
					}
				}
			} catch (Exception e) {
				//adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "InstallNotificationThread", e.getMessage());
			}
		}
	}
	
	private class ContentParameters {
		public String url;
		//public String w;
		//public String h;
		public AdServerViewCore sender;
		ContentThread cTh;
	};

	private class ContentThread extends Thread {
		ContentParameters parameters;
		boolean isCanceled = false;
		
		public ContentThread(ContentParameters parameters) {
			this.parameters = parameters;
		}

		@Override
		public void run() {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(parameters.url);
				//get.addHeader("Accept-Encoding", "gzip");
				//get.addHeader("Accept", "application/json");
				//get.addHeader("UA-Pixels", parameters.w + "x" + parameters.h);
				get.addHeader("User-Agent", userAgent);				

				HttpConnectionParams.setConnectionTimeout(get.getParams(), Constants.AD_RELOAD_PERIOD);
				HttpConnectionParams.setSoTimeout(get.getParams(), Constants.DEFAULT_REQUEST_TIMEOUT);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						inputStream, 1024);				
				String responseValue="";
				if(!isCanceled)
				{
					responseValue = readInputStream(bufferedInputStream);
				}
				bufferedInputStream.close();
				inputStream.close();
				if (parameters.sender != null)
					parameters.sender.setResult(responseValue, isCanceled ? "canceled" : null);
			} catch (ClientProtocolException e) {
				if (parameters.sender != null)
					parameters.sender.setResult(null, e.toString() + ": " + e.getMessage());
			} catch (IOException e) {
				if (parameters.sender != null)
					parameters.sender.setResult(null, e.toString() + ": " + e.getMessage());
			}

			stopLoadContent(parameters.sender);
		}

		public void cancel()
		{
			isCanceled = true;
		}
		
		private String readInputStream(BufferedInputStream in) throws IOException {
			byte[] buffer = new byte[1024];
			ByteArrayBuffer byteBuffer = new ByteArrayBuffer(1);
			for (int n; (n = in.read(buffer)) != -1;) {
				if(isCanceled) return "";
				byteBuffer.append(buffer, 0, n);
			}
			return new String(byteBuffer.buffer(),0,byteBuffer.length());			
		}
	}

	private void initDefaultParameters() {
		String deviceId;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		isSimAvailable = tm.getSimState() > TelephonyManager.SIM_STATE_ABSENT;
		String tempDeviceId = tm.getDeviceId();

		if (null != tempDeviceId) {
			deviceId = tempDeviceId;
		} else {
			tempDeviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

			if (null != tempDeviceId) {
				deviceId = tempDeviceId;
			} else {
				deviceId = null;
			}
		}

		if (deviceId == null) {
			deviceId = getId(context);
		}
		
		String deviceIdMd5 = Utils.md5(deviceId);
		
		autoDetectParameters = "";
		
		if (tm!=null) 
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

		/*autoDetectParameters = "&cfmt=text,image,html5richmedia";
		autoDetectParameters += "&sft=jpeg,png,gif";
		autoDetectParameters += "&fmt=json";
		autoDetectParameters += "&cltp=app";
		autoDetectParameters += "&dim=le";
		autoDetectParameters += "&rad=7";
		autoDetectParameters += "&loptin=1";
		autoDetectParameters += "&lc=" + Locale.getDefault().toString().replace("_", "-");
		autoDetectParameters += "&idtp=muid";
		autoDetectParameters += "&deviceOSID=204";*/

		if ((deviceIdMd5 != null) && (deviceIdMd5.length() > 0)) {
			autoDetectParameters += "&"+AdserverRequest.parameter_device_id+"=" + deviceIdMd5;
		}
		
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

		/*autoDetectParameters += "&devicemake=" + URLEncoder.encode(android.os.Build.MANUFACTURER);
		// autoDetectParameters += "&devicemodel="+android.os.Build.MODEL;
		autoDetectParameters += "&devicemodel=" + URLEncoder.encode(android.os.Build.MODEL);
		autoDetectParameters += "&deviceos=Android";
		autoDetectParameters += "&deviceosversion=" + URLEncoder.encode(android.os.Build.VERSION.RELEASE);*/
	}

	private synchronized static String getId(Context context) {
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
