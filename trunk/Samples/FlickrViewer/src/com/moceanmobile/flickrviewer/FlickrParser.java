package com.moceanmobile.flickrviewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

public class FlickrParser 
{

	//protected Gson gsonParser = new GsonBuilder().serializeNulls().create();

	private URLConnection connection;
	private String connectionUrl;
	private String connectionMethod;
	private DataConsumer dataConsumer;
	
	
	public FlickrParser(String url, String method, DataConsumer consumer)
	{
		connectionUrl = url;
		connectionMethod = method;
		dataConsumer = consumer;
	}
	
	
	public void start() throws IOException
	{
		boolean error = false;
		
		URL url = new URL(connectionUrl);

		connection = url.openConnection();
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);

		HttpURLConnection httpConnection = null;
		if (connection instanceof HttpURLConnection)
			httpConnection = (HttpURLConnection) connection;
		
		if (httpConnection != null)
			httpConnection.setRequestMethod(connectionMethod);

		
		//processOutput(connection.getOutputStream());

		
		int responseCode = -1;
		try
		{
			if (httpConnection != null)
				responseCode = httpConnection.getResponseCode();

			boolean continueProcessing = true;
			continueProcessing = responseCode(responseCode);

			if (!continueProcessing)
				return;

			processInput(responseCode, connection.getInputStream());
		}
		catch (MalformedJsonException e)
		{
			System.out.println("Malformed json: " + e.getMessage());
			error = true;
		}
		catch (IOException e)
		{
			error = true;
			
			boolean processResponseCode = responseCode == -1;
			responseCode = httpConnection.getResponseCode();
			 
			if (processResponseCode)
			{
				boolean continueProcessing = true;
				continueProcessing = responseCode(responseCode);
				 
				if (continueProcessing == false)
					return;
			}
			 
			if (responseCode != -1)
			{
				processError(responseCode, httpConnection.getErrorStream());
			}
			else
			{
				throw e;
			}
		}
		catch(Exception e)
		{
			System.out.println("Parser: generic exception: " + e.getMessage());
			error = true;
		}
		finally
		{
			if (httpConnection != null)
				httpConnection.disconnect();
			
			dataConsumer.completedNotification(error);
		}
	}

	private void processOutput(OutputStream outputStream) throws IOException
	{
		outputStream.close();
	}
	
	/*
	  The data processed will look similar to the following:
	  jsonFlickrFeed({
		"title": "Uploads from everyone",
		"link": "http://www.flickr.com/photos/",
		"description": "",
		"modified": "2012-05-25T17:57:34Z",
		"generator": "http://www.flickr.com/",
		"items": [
	   		{
				"title": "20120525(12)",
				"link": "http://www.flickr.com/photos/59772484@N07/7268798332/",
				"media": {"m":"http://farm8.staticflickr.com/7238/7268798332_8a4db3e801_m.jpg"},
				"date_taken": "2012-05-25T19:25:39-08:00",
				"description": " <p><a href=\"http://www.flickr.com/people/59772484@N07/\">JoaquinSierra<\/a> posted a photo:<\/p> <p><a href=\"http://www.flickr.com/photos/59772484@N07/7268798332/\" title=\"20120525(12)\"><img src=\"http://farm8.staticflickr.com/7238/7268798332_8a4db3e801_m.jpg\" width=\"161\" height=\"240\" alt=\"20120525(12)\" /><\/a><\/p> ",
				"published": "2012-05-25T17:57:34Z",
				"author": "nobody@flickr.com (JoaquinSierra)",
				"author_id": "59772484@N07",
				"tags": ""
		   	},
		   	... more items in array ...
	   	]})
	 */
	private void processInput(int code, InputStream inputStream) throws IOException
	{
		// skip leading "jsonFlickrFeed(" wrapper that is intended for javascript users
		inputStream.skip(15);
		
		JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
		//reader.setLenient(true);
		
		// skip to beginning of array containing image data collections
		reader.beginObject();
		JsonToken token;
		while (reader.hasNext())
		{
			 token = reader.peek();
			 if (token == com.google.gson.stream.JsonToken.BEGIN_ARRAY)
			 {
				 reader.beginArray();
				 break;
			 }
			 else
			 {
				 reader.skipValue();
			 }
		}
		
		// consume this collection
		HashMap<String,String> objectData = new HashMap<String,String>();
		String name = null;
		String value = null;
		boolean done = false;
		boolean isMedia = false;
		while (!done)
		{
			token = reader.peek();
			//System.out.println("parser: token=" + token.name());

			switch (token)
			{
			case BEGIN_OBJECT:
				reader.beginObject();
				if ((name != null) && (name.compareTo("media") == 0))
				{
					isMedia = true;
				}
				break;
				
			case END_OBJECT:
				reader.endObject();
				if (!isMedia)
				{
					dataConsumer.saveDataItem(objectData);
					objectData.clear();
				}
				isMedia = false;
				break;
				
			case NAME:
				name = reader.nextName();
				//System.out.println("parsed data name=" + name );
				break;
				
			case STRING:
				value = reader.nextString();
				//System.out.println("parsed data value=" + value);
				break;
				
			case END_DOCUMENT:
				//System.out.println("End of document.");
				//reader.close();
				done = true;
				break;
				
			case END_ARRAY:
				//System.out.println("End of array.");
				reader.endArray();
				done = true;
				break;
				
			default:
				reader.skipValue();
			}
			
			if ((name != null) && ( value != null))
			{
				objectData.put(name, value);
				name = null;
				value = null;
			}
			
			if (reader.hasNext() == false)
			{
				if (isMedia)
				{
					isMedia = false;
				}
				else
				{
					dataConsumer.saveDataItem(objectData);
					objectData.clear();
				}
				
				reader.endObject();
			}
		}
		reader.close();	
	}
	
	private void processError(int code, InputStream errorStream) throws IOException
	{
		if (errorStream != null)
		{
			processInput(code, errorStream);
			return;
		}

	}

	private boolean responseCode(int code)
	{
		if ((code >= 200) && (code <= 299))
			return true;
		
		return false;
	}
	
	// Consumer interface implemented by caller to save parsed data items
	public interface DataConsumer
	{
		public void saveDataItem(HashMap<String,String>objects);
		public void completedNotification(boolean error);
	}
}
