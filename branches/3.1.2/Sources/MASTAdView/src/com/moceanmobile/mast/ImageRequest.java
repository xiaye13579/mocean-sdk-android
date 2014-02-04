//
// Copyright (C) 2013 Mocean Mobile. All Rights Reserved. 
//
package com.moceanmobile.mast;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageRequest
{
	public static ImageRequest create(int timeout, String url, String userAgent, boolean useGifDecoder, Handler handler)
	{
		if (handler == null)
			return null;
		
		ImageRequest imageRequest = new ImageRequest(timeout, url, userAgent, useGifDecoder, handler);
		imageRequest.start();
		return imageRequest;
	}
	
	private final int timeout;
	private final String url;
	private final String userAgent;
	private final boolean useGifDecoder;
	private Handler handler = null;
	
	public ImageRequest(int timeout, String url, String userAgent, boolean useGifDecoder, Handler handler)
	{
		this.timeout = timeout;
		this.url = url;
		this.userAgent = userAgent;
		this.useGifDecoder = useGifDecoder;
		this.handler = handler;
	}
	
	public void cancel()
	{
		handler = null;
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
				
				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("User-Agent", userAgent);
				httpGet.setHeader("Connection", "close");
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				if (httpResponse.getStatusLine().getStatusCode() != 200)
				{
					if (handler != null)
					{
						handler.imageFailed(ImageRequest.this, null);
					}

					return;
				}

				final int bufferSize = 8192 * 4;
				InputStream inputStream = httpResponse.getEntity().getContent();
				inputStream = new BufferedInputStream(inputStream, bufferSize);

				inputStream.mark(bufferSize);

				boolean isGif = false;
				if (useGifDecoder)
				{
					byte[] gifBuffer = new byte[3];
					inputStream.read(gifBuffer);
					if ((gifBuffer[0] == 'G') && (gifBuffer[1] == 'I') && (gifBuffer[2] == 'F'))
					{
						isGif = true;
					}
					inputStream.reset();
				}
				
				Object imageObject = null;

				if (isGif)
				{
					GifDecoder gifDecoder = new GifDecoder();
					int status = gifDecoder.read(inputStream);
					
					if (status == GifDecoder.STATUS_OK)
					{
						imageObject = gifDecoder;
					}
				}
				else
				{
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					
					if (bitmap != null)
					{
						imageObject = bitmap;
					}
				}
				
				if (imageObject != null)
				{
					handler.imageReceived(ImageRequest.this, imageObject);
				}
				else
				{
					handler.imageFailed(ImageRequest.this, null);
				}
				
				inputStream.close();
			}
			catch (Exception ex)
			{
				handler.imageFailed(ImageRequest.this, ex);
			}
		}
	}
	
	public interface Handler
	{
		public void imageFailed(ImageRequest request, Exception exception);
		public void imageReceived(ImageRequest request, Object imageObject);
	}
}
