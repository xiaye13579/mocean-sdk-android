package com.moceanmobile.mast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class AdTracking
{
	public static void invokeTrackingUrl(int timeout, String url, String userAgent)
	{
		try
		{
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", userAgent);
			httpGet.setHeader("Connection", "close");
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			
			httpResponse.getStatusLine();
		}
		catch (Exception ex)
		{
			
		}
	}
}
