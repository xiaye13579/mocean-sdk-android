package com.moceanmobile.flickrviewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils
{
	public static InputStream fetchUrl(String fromUrl)
	{		
		try
		{
			System.out.println("Fetcing URL: " + fromUrl);
			
			HttpClient httpclient = new DefaultHttpClient();  
    		HttpGet request = new HttpGet(fromUrl);
    		HttpResponse response = httpclient.execute(request);     
    		StatusLine statusLine = response.getStatusLine();    
    		if (statusLine.getStatusCode() == 200)
    		{
				return response.getEntity().getContent();
    		}
		}
		catch(Exception e)
		{
			System.out.println("Fetcher: generic exception: " + e.getMessage());
		}
		
		return null;
	}
		
	public static Bitmap fetchImage(String url)
	{
		//System.out.println("Start fetch URL for: " + url);
		InputStream is = fetchUrl(url);
		//System.out.println("Done fetch URL");
		
		if (is != null)
		{
			Bitmap image = BitmapFactory.decodeStream(is);
			try
			{
				is.close();	
			}
			catch(Exception ex)
			{
				// error closing stream... bleh!
			}
			
			//System.out.println("Image decoded...");
			return image;
		}
		
		return null;
	}
}
