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

	static public ContentManager getInstance(Context context) {
		if (instance == null)
			instance = new ContentManager(context);
		
		return instance;
	}

	private ContentManager(final Context context) {
		userAgent = (new WebView(context)).getSettings().getUserAgentString();
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				initDefaultParameters(context);
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

	public void sendImpression(final String uri,final AdLog adLog) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				if(!sendImpr(uri))
					adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_WARNING, Constants.STR_IMPRESSION_NOT_SEND, uri);
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

	private void initDefaultParameters(Context context) {
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

		autoDetectParameters = "&cfmt=text,image,html5richmedia";
		autoDetectParameters += "&sft=jpeg,png,gif";
		autoDetectParameters += "&fmt=json";
		autoDetectParameters += "&cltp=app";
		autoDetectParameters += "&dim=le";
		autoDetectParameters += "&rad=7";
		autoDetectParameters += "&loptin=1";
		autoDetectParameters += "&lc=" + Locale.getDefault().toString().replace("_", "-");
		autoDetectParameters += "&idtp=muid";
		autoDetectParameters += "&deviceOSID=204";

		if ((deviceIdMd5 != null) && (deviceIdMd5.length() > 0)) {
			autoDetectParameters += "&uid=" + deviceIdMd5;
		}

		autoDetectParameters += "&devicemake=" + URLEncoder.encode(android.os.Build.MANUFACTURER);
		// autoDetectParameters += "&devicemodel="+android.os.Build.MODEL;
		autoDetectParameters += "&devicemodel=" + URLEncoder.encode(android.os.Build.MODEL);
		autoDetectParameters += "&deviceos=Android";
		autoDetectParameters += "&deviceosversion=" + URLEncoder.encode(android.os.Build.VERSION.RELEASE);
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
