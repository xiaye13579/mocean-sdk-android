/*
 * PubMatic Inc. (“PubMatic”) CONFIDENTIAL
 * Unpublished Copyright (c) 2006-2014 PubMatic, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of PubMatic. The intellectual and technical concepts contained
 * herein are proprietary to PubMatic and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from PubMatic.  Access to the source code contained herein is hereby forbidden to anyone except current PubMatic employees, managers or contractors who have executed 
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  
 * information that is confidential and/or proprietary, and is a trade secret, of  PubMatic.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, 
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF PubMatic IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE 
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS  
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.                
 */

package com.moceanmobile.mast;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class AdRequest
{
	public static AdRequest create(int timeout, String adServerUrl, 
			String userAgent, Map<String, String> parameters, Handler handler) throws UnsupportedEncodingException
	{
		AdRequest adRequest = new AdRequest(timeout, adServerUrl, userAgent, parameters, handler);

		adRequest.start();
		
		return adRequest;
	}
	
	private final int timeout;
	private final String requestUrl;
	private final String userAgent;
	private Handler handler = null;
	
	private AdRequest(int timeout, String adServerUrl, String userAgent, 
			Map<String, String> parameters, Handler handler) throws UnsupportedEncodingException
	{
		this.timeout = timeout;
		this.userAgent = userAgent;
		this.handler = handler;

        StringBuilder sb = new StringBuilder(128);
        sb.append(adServerUrl);
        sb.append('?');
        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
        	sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        	sb.append('=');
        	sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        	sb.append('&');
        }
        sb.setLength(sb.length() - 1);
        
        requestUrl = sb.toString();
	}
	
	public String getRequestUrl()
	{
		return requestUrl;
	}
	
	public void cancel()
	{
		this.handler = null;
	}
	
	private void start()
	{
		RequestProcessor processor = new RequestProcessor();
		
		Background.getExecutor().execute(processor);
	}
	
	private class RequestProcessor implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000);
	
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				
				HttpGet httpGet = new HttpGet(requestUrl);
				httpGet.setHeader("User-Agent", userAgent);
				httpGet.setHeader("Connection", "close");
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				if (httpResponse.getStatusLine().getStatusCode() != 200)
				{
					if (handler != null)
					{
						handler.adRequestFailed(AdRequest.this, null);
					}
					
					return;
				}
				
				InputStream inputStream = httpResponse.getEntity().getContent();
				
				// TODO: If debugging, convert stream to raw text (and back to stream to XML parser)

				XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
				parserFactory.setNamespaceAware(false);
				parserFactory.setValidating(false);
				
				XmlPullParser parser = parserFactory.newPullParser();
				parser.setInput(inputStream, "UTF-8");
				
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					if (eventType == XmlPullParser.START_TAG) 
					{
						String name = parser.getName();
							
						if ("error".equals(name))
						{
	                        String errorCode = parser.getAttributeValue(null, "code");
	                        String errorMessage = null;
	
	                        // read past the name
	                        parser.next();
	
	                        // read the contents
	                        if (parser.getEventType() == XmlPullParser.TEXT)
	                        {
	                        	errorMessage = parser.getText();
	                        }
	
	                        if (handler != null)
	                        {
	                        	handler.adRequestError(AdRequest.this, errorCode, errorMessage);
	                        }
	                        
	                        cancel();
	                        
	                        // stop parsing
	                        break;
						}
						else if ("ad".equals(name))
						{
							AdDescriptor adDescriptor = AdDescriptor.parseDescriptor(parser);
							
							if (handler != null)
							{
								handler.adRequestCompleted(AdRequest.this, adDescriptor);
							}
							
	                        // the stream may contain more descriptors but only the
	                        // first one matters (and really, should be the only one)
	                        break;
						}
					}
					
					parser.next();
					eventType = parser.getEventType();
				}
				
				inputStream.close();
			}
			catch (Exception ex)
			{
				if (handler != null)
				{
					handler.adRequestFailed(AdRequest.this, ex);
				}
			}
		}
	}
	
	public interface Handler
	{
		public void adRequestFailed(AdRequest request, Exception exception);
		public void adRequestError(AdRequest request, String errorCode, String errorMessage);
		public void adRequestCompleted(AdRequest request, AdDescriptor adDescriptor);
	}
}
