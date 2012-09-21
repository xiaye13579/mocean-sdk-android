//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;


import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;


// The javascript interface class exposes java methods so that they can be invoked from the javascript layer/ad creative
public class JavascriptInterface
{
	// Name used to attach java methods to javacsript; invocation of a java method from js
	// looks like: AdWebView.method(parameters)
	final private String JAVASCRIPT_METHOD_PREFIX = "AdWebView";
		
		
	private AdViewContainer adView;
	private AdWebView webView;
	private Context context;

	private List<NameValuePair> orientationProperties = null;
	private Object orientationSyncObject = new Object();

	private List<NameValuePair> expandProperties = null;
	private Object expandSyncObject = new Object();
	
	private List<NameValuePair> resizeProperties = null;
	private Object resizeSyncObject = new Object();
	
	
	// Construct interface
	public JavascriptInterface(AdViewContainer container, AdWebView webView)
	{
		adView = container;
		this.webView = webView; 
		context = adView.getContext();
		
		// Setup javascript -> java interface
		webView.addJavascriptInterface(this, JAVASCRIPT_METHOD_PREFIX);
	}
	
	
	//
	// Methods exposed to javascript from this class; these are for use by the javascript code
	// to make calls back to the java app.
	//

	
	public void log(String message)
	{
		System.out.println("JavascriptInterface.log: " + message);
	}
	

	// Open (new) URL in full-screen internal (or external, by setting) browser window;
	// the target URL does NOT expect to operate in an MRAID environment.
	public void open(String url)
	{
		System.out.println("JavascriptInterface: open");
		adView.mraidEvent("open", url);
	
		synchronized(this)
		{
			if (url != null)
			{
				// Notify ad view to perform open on UI thread
				Message msg = adView.getHandler().obtainMessage(AdMessageHandler.MESSAGE_OPEN);
				Bundle data = new Bundle();
				data.putString(AdMessageHandler.OPEN_URL, url);
				msg.setData(data);
				adView.getHandler().sendMessage(msg);
			}
		}
	}

	
	// Allow an ad to downgrade its state, and fire a state change event
	public void close()
	{
		System.out.println("JavascriptInterface: close");
		adView.mraidEvent("close", null);
		
		synchronized(this)
		{
			adView.getHandler().sendEmptyMessage(AdMessageHandler.MESSAGE_CLOSE);
		}
	}

	
	// Used by javascript code to pass orientation properties into the java app
	public void setOrientationProperties(String encodedProperties)
	{
		adView.mraidEvent("setOrientationProperties", encodedProperties);
		
		synchronized (orientationSyncObject)
		{
			System.out.println("JavascriptInterface: setOrientationProperties: " + encodedProperties);
	
			// The encoded property string is made up of key=value fragments joined with an '&',
			// with each key and value portion being uri encoded (on the javascript side.) We
			// can use a standard android library routine to parse this by making it look like
			// a real URI.
			try
			{
				URI propertiesUri = new URI("http://orientation.properties?" + encodedProperties);
				//List<NameValuePair> properties = URLEncodedUtils.parse(propertiesUri, "UTF-8"); 
				//expandProperties = createMapFromList(properties);
				orientationProperties = URLEncodedUtils.parse(propertiesUri, "UTF-8");
				
				// If orientation properties are set, and ad is expanded, apply those now
				// because the spec allows creatives to change orientation settings on-the-fly
				MraidInterface.STATES adState = webView.getMraidInterface().getState();
				
				
				
				// XXX need to do this for interstital as well
				
				
				
				if (adState == MraidInterface.STATES.EXPANDED)
				{
					boolean allowReorientation = true; // default
					String forceOrientation = MraidInterface.get_FORCE_ORIENTATION_PROPERTIES_name(MraidInterface.FORCE_ORIENTATION_PROPERTIES.NONE);
					
					String value = getListValueByName(orientationProperties, MraidInterface.get_ORIENTATION_PROPERTIES_name(MraidInterface.ORIENTATION_PROPERTIES.ALLOW_ORIENTATION_CHANGE));
					if ((value != null) && (value.equalsIgnoreCase("false")))
					{
						allowReorientation = false;
					}

					value = getListValueByName(orientationProperties, MraidInterface.get_ORIENTATION_PROPERTIES_name(MraidInterface.ORIENTATION_PROPERTIES.FORCE_ORIENTATION));
					if (value != null)
					{
						forceOrientation = value;
					}

					// Apply orientation options
					AdSizeUtilities.handleOrientation(allowReorientation, forceOrientation, context);
				}
			}
			catch(Exception ex)
			{
				System.out.println("Exception setting orientation properties from javascript: " + ex.getMessage() + " using: " + encodedProperties);
			}
		}
	}
	
	
	// Used by javascript code to pass expand properties into the java app
	public void setExpandProperties(String encodedProperties)
	{
		adView.mraidEvent("setExpandProperties", encodedProperties);
		
		synchronized (expandSyncObject)
		{
			System.out.println("JavascriptInterface: setExpandProperties: " + encodedProperties);
	
			// The encoded property string is made up of key=value fragments joined with an '&',
			// with each key and value portion being uri encoded (on the javascript side.) We
			// can use a standard android library routine to parse this by making it look like
			// a real URI.
			try
			{
				URI propertiesUri = new URI("http://expand.properties?" + encodedProperties);
				//List<NameValuePair> properties = URLEncodedUtils.parse(propertiesUri, "UTF-8"); 
				//expandProperties = createMapFromList(properties);
				expandProperties = URLEncodedUtils.parse(propertiesUri, "UTF-8");				
			}
			catch(Exception ex)
			{
				System.out.println("Exception setting expand properties from javascript: " + ex.getMessage() + " using: " + encodedProperties);
			}
		}
	}
	
	
	// Full-screen, modal view of ad in-app with support for 1 or 2 part creatives;
	// SDK-enforced tap-to-close area in fixed (top-right) location; relative alignment.
	// Ad state will change to expanded upon success (firing the state change event.)
	public void expand(String url)
	{
		adView.mraidEvent("expand", url);
		
		synchronized (expandSyncObject)
		{
			System.out.println("JavascriptInterface: expand");
			
		
			// Notify ad view to perform resize on UI thread
			Message msg = adView.getHandler().obtainMessage(AdMessageHandler.MESSAGE_EXPAND);
			Bundle data = convertExpandDimensions(expandProperties); // pass expand properties, convert pixel dimensions
			if (url != null)
			{
				data.putString(AdMessageHandler.EXPAND_URL, url);
			}
			msg.setData(data);
			adView.getHandler().sendMessage(msg);
		}
	}
	
	
	// Used by javascript code to pass resize properties into the java app
	public void setResizeProperties(String encodedProperties, String sizeIsPixels)
	{
		adView.mraidEvent("setResizeProperties", encodedProperties);
		
		synchronized (resizeSyncObject)
		{
			System.out.println("JavascriptInterface: setResizeProperties: " + encodedProperties);
	
			// The encoded property string is made up of key=value fragments joined with an '&',
			// with each key and value portion being uri encoded (on the javascript side.) We
			// can use a standard android library routine to parse this by making it look like
			// a real URI.
			try
			{
				URI propertiesUri = new URI("http://resize.properties?" + encodedProperties);
				//List<NameValuePair> properties = URLEncodedUtils.parse(propertiesUri, "UTF-8"); 
				//resizeProperties = createMapFromList(properties);
				resizeProperties = URLEncodedUtils.parse(propertiesUri, "UTF-8");
				if ((sizeIsPixels != null) && (sizeIsPixels.equalsIgnoreCase("true")))
				{
					// convert to points, because that's what we assume later
					// XXX This is a hack for ormma resize(withd, height) compatibility, should not be needed in production!!!
					convertResizeDimensionsToPoints(resizeProperties);
				}
			}
			catch(Exception ex)
			{
				System.out.println("Exception setting resize properties from javascript: " + ex.getMessage() + " using: " + encodedProperties);
			}
		}
	}
	
	
	// Non-modal dynamic size view for ad content; SDK-enforced tap-to-close area with
	// adjustable position; absolute positioning possible; resize direction selectable.
	// Ad state will change to resized upon success (firing the state change event,
	// as well as the size change event.)
	public void resize()
	{
		adView.mraidEvent("resize", null);
		
		synchronized (resizeSyncObject)
		{
			System.out.println("JavascriptInterface: resize");
			
			if ((resizeProperties == null) || (resizeProperties.isEmpty()))
			{
				// It is an error not not set the resize properties before calling this method
				webView.getMraidInterface().fireErrorEvent("Resize parameters not set", "resize");
				return;
			}
			
			// Notify ad view to perform resize on UI thread
			Message msg = adView.getHandler().obtainMessage(AdMessageHandler.MESSAGE_RESIZE);
			Bundle data = convertResizeDimensionsToPixels(resizeProperties); // pass resize properties, convert pixel dimensions
			msg.setData(data);
			adView.getHandler().sendMessage(msg);
		}
	}

	
	// get current postion for ad view
	public String getCurrentPosition()
	{
		adView.mraidEvent("getCurrentPosition", null);
		
		synchronized(this)
		{
			System.out.println("javascriptinterface: getCurrentPositon()");
			
			int x = AdSizeUtilities.devicePixelToMraidPoint(adView.getLeft(), context);
			int y = AdSizeUtilities.devicePixelToMraidPoint(adView.getTop(), context);
			int w = AdSizeUtilities.devicePixelToMraidPoint(adView.getWidth(), context);
			int h = AdSizeUtilities.devicePixelToMraidPoint(adView.getHeight(), context);
			
			try
			{
				JSONObject position = new JSONObject();
				position.put(MraidInterface.get_CURRENT_POSITION_name(MraidInterface.CURRENT_POSITION.X), "" + x);
				position.put(MraidInterface.get_CURRENT_POSITION_name(MraidInterface.CURRENT_POSITION.Y), "" + y);
				position.put(MraidInterface.get_CURRENT_POSITION_name(MraidInterface.CURRENT_POSITION.WIDTH), "" + w);
				position.put(MraidInterface.get_CURRENT_POSITION_name(MraidInterface.CURRENT_POSITION.HEIGHT), "" + h);
			
				System.out.println("returning: " + position.toString());
				return position.toString();
			}
			catch (Exception ex)
			{
				System.out.println("Exception returning current position: " + ex.getMessage());
			}
		}
		
		return "undefined";
	}

	
	public void createCalendarEntry(String encodedProperties)
	{
		adView.mraidEvent("createCalendarEntry", encodedProperties);
		
		synchronized(this)
		{
			boolean approved = false;
			if (adView.adDelegate.getUserApprovalRequestHandler() != null)
			{
				// XXX move this to UI thread???
				approved = adView.adDelegate.getUserApprovalRequestHandler().onAddCalendarEntryEvent(adView);
			}
			
			if (approved)
			{
				System.out.println("JavascriptInterface: createCalendarEntry: " + encodedProperties);
			
				// The encoded property string is made up of key=value fragments joined with an '&',
				// with each key and value portion being uri encoded (on the javascript side.) We
				// can use a standard android library routine to parse this by making it look like
				// a real URI.
				try
				{
					URI propertiesUri = new URI("http://calendar.event?" + encodedProperties);
					List<NameValuePair> properties = URLEncodedUtils.parse(propertiesUri, "UTF-8"); 
					Bundle dataBundle = listToDataBundle(properties);
					
					// Notify ad view to perform action on UI thread
					Message msg = adView.getHandler().obtainMessage(AdMessageHandler.MESSAGE_CREATE_EVENT);
					msg.setData(dataBundle);
					adView.getHandler().sendMessage(msg);
				}
				catch(Exception ex)
				{
					String error = "Exception creating calendar event javascript: " + ex.getMessage() + " using: " + encodedProperties;
					System.out.println(error);
					webView.getMraidInterface().fireErrorEvent(error, "createCalendarEvent");
				}
			}
		}
	}
	
	
	public void playVideo(String uri)
	{
		adView.mraidEvent("playVideo", uri);
		
		synchronized(this)
		{
			// Notify ad view to perform action on UI thread
			Message msg = adView.getHandler().obtainMessage(AdMessageHandler.MESSAGE_PLAY_VIDEO);
			Bundle data = new Bundle();
			data.putString(AdMessageHandler.PLAYBACK_URL, uri);
			msg.setData(data);
			adView.getHandler().sendMessage(msg);
		}
	}
	
	
	private File makePictureFile(String extension)
	{
		long now = System.currentTimeMillis();
		
		File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	
		// Make sure the Pictures directory exists.
        folder.mkdirs();
		
		File path = new File( folder, "picture-" + now + extension);
		return path;
	}
	
	
	public boolean storePicture(final String uri)
	{
		adView.mraidEvent("storePicture", uri);
		
		synchronized(this)
		{
			boolean approved = false;
			if (adView.adDelegate.getUserApprovalRequestHandler() != null)
			{
				// XXX move this to UI thread???
				approved = adView.adDelegate.getUserApprovalRequestHandler().onStorePictureEvent(adView, uri);
			}
			
			if (approved)
			{
				Thread worker = new Thread()
				{
					public void run()
					{
						try
						{
							InputStream is = AdData.fetchUrl(uri);
							File outputFile = makePictureFile(".jpg"); // XXX parse extension from URI???
							if (outputFile != null)
							{
								FileUtils.writeToDisk(is, outputFile);
								
								// Tell the media scanner about the new file so that it is
						        // immediately available to the user.
						        MediaScannerConnection.scanFile(context,
						                new String[] { outputFile.toString() }, null,
						                new MediaScannerConnection.OnScanCompletedListener()
						        		{
						            		public void onScanCompleted(String path, Uri uri)
						            		{
						            			// XXX log scan of file
						            		}
						        		});
						        
						        return;
							}
						}
						catch(Exception ex)
						{
							// log
							webView.getMraidInterface().fireErrorEvent("Error storing picture: " + ex.getMessage(), "storePicture");
						}
						
						// Should not get here
						webView.getMraidInterface().fireErrorEvent("Storing picture failed for: " + uri, "storePicture");
					}
				};
				worker.setName("[JavascriptInterface] storePicture");
				worker.start();
				
				return true;
			}
			else
			{
				// XXX log...
				webView.getMraidInterface().fireErrorEvent("Storing picture not allowed for: " + uri, "storePicture");
			}
		}
		
		return false;
	}
	
	
	//
	// Support functions
	//
	
	
	private HashMap<Object, Object> createMapFromList(List<NameValuePair> list)
	{
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		if (list != null)
		{
			Iterator<NameValuePair> i = list.iterator();
			NameValuePair nvp;
			while (i.hasNext())
			{
				nvp = i.next();
				if ((nvp != null) && (nvp.getName() != null))
				{
					System.out.println("createMapFromList: name=" + nvp.getName() + ", value=" + nvp.getValue());
					map.put(nvp.getName(), nvp.getValue());
				}
			}
		}
		
		return map;
	}
	
	
	// Convert a name value pair list to a name/value data bundle for passing through the handler interface
	private Bundle listToDataBundle(List<NameValuePair> list)
	{
		Bundle data = new Bundle();
		
		if (list != null)
		{
			Iterator<NameValuePair> i = list.iterator();
			NameValuePair nvp;
			while (i.hasNext())
			{
				nvp = i.next();
				if ((nvp != null) && (nvp.getName() != null))
				{
					data.putString(nvp.getName(), nvp.getValue());
				}
			}
		}
		
		return data;
	}
	

	private String getListValueByName(List<NameValuePair> list, String name)
	{
		if ((list != null) && (name != null))
		{
			Iterator<NameValuePair> i = list.iterator();
			NameValuePair nvp;
			while (i.hasNext())
			{
				nvp = i.next();
				if ((nvp != null) && (nvp.getName() != null) && (nvp.getName().compareTo(name) == 0))
				{
					return nvp.getValue();
				}
			}
		}
		
		return null;
	}
	
	
	private Bundle convertExpandDimensions(List<NameValuePair> list)
	{
		Bundle data = new Bundle();
		
		if (list != null)
		{
			Iterator<NameValuePair> i = list.iterator();
			NameValuePair nvp;
			Integer pixels;
			while (i.hasNext())
			{
				nvp = i.next();
				if ((nvp != null) && (nvp.getName() != null))
				{
					if (nvp.getName().compareTo(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.HEIGHT)) == 0)
					{
						pixels = AdSizeUtilities.mraidPointToDevicePixel(Integer.parseInt(nvp.getValue()), context);
						data.putString(nvp.getName(), pixels.toString());
					}
					else if (nvp.getName().compareTo(MraidInterface.get_EXPAND_PROPERTIES_name(MraidInterface.EXPAND_PROPERTIES.WIDTH)) == 0)
					{
						pixels = AdSizeUtilities.mraidPointToDevicePixel(Integer.parseInt(nvp.getValue()), context);
						data.putString(nvp.getName(), pixels.toString());
					}
					else
					{
						data.putString(nvp.getName(), nvp.getValue());	
					}
				}
			}
		}
		
		return data;
	}
	
	

	private Bundle convertResizeDimensionsToPixels(List<NameValuePair> list)
	{
		System.out.println("Converting resize properites to pixel values");
		
		Bundle data = new Bundle();
		
		if (list != null)
		{
			Iterator<NameValuePair> i = list.iterator();
			NameValuePair nvp;
			Integer pixels;
			while (i.hasNext())
			{
				nvp = i.next();
				if ((nvp != null) && (nvp.getName() != null))
				{
					if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.HEIGHT)) == 0)
					{
						pixels = AdSizeUtilities.mraidPointToDevicePixel(Integer.parseInt(nvp.getValue()), context);
						data.putString(nvp.getName(), pixels.toString());
					}
					else if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.WIDTH)) == 0)
					{
						pixels = AdSizeUtilities.mraidPointToDevicePixel(Integer.parseInt(nvp.getValue()), context);
						data.putString(nvp.getName(), pixels.toString());
					}
					else if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_X)) == 0)
					{
						pixels = AdSizeUtilities.mraidPointToDevicePixel(Integer.parseInt(nvp.getValue()), context);
						data.putString(nvp.getName(), pixels.toString());
					}
					else if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_Y)) == 0)
					{
						pixels = AdSizeUtilities.mraidPointToDevicePixel(Integer.parseInt(nvp.getValue()), context);
						data.putString(nvp.getName(), pixels.toString());
					}
					else
					{
						data.putString(nvp.getName(), nvp.getValue());	
					}
				}
			}
		}
		
		return data;
	}
	
	
	private void convertResizeDimensionsToPoints(List<NameValuePair> list)
	{
		System.out.println("Converting resize properites to point values");
		
		if (list != null)
		{
			Iterator<NameValuePair> i = list.iterator();
			NameValuePair nvp;
			BasicNameValuePair newNvp;
			Integer pixels;
			int index;
			while (i.hasNext())
			{
				nvp = i.next();
				if ((nvp != null) && (nvp.getName() != null))
				{
					if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.HEIGHT)) == 0)
					{
						pixels = AdSizeUtilities.devicePixelToMraidPoint(Integer.parseInt(nvp.getValue()), context);
						newNvp = new BasicNameValuePair(nvp.getName(), "" + pixels);
						index = list.indexOf(nvp);
						list.remove(index);
						list.add(index, newNvp);
					}
					else if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.WIDTH)) == 0)
					{
						pixels = AdSizeUtilities.devicePixelToMraidPoint(Integer.parseInt(nvp.getValue()), context);
						newNvp = new BasicNameValuePair(nvp.getName(), "" + pixels);
						index = list.indexOf(nvp);
						list.remove(index);
						list.add(index, newNvp);
					}
					else if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_X)) == 0)
					{
						pixels = AdSizeUtilities.devicePixelToMraidPoint(Integer.parseInt(nvp.getValue()), context);
						newNvp = new BasicNameValuePair(nvp.getName(), "" + pixels);
						index = list.indexOf(nvp);
						list.remove(index);
						list.add(index, newNvp);
					}
					else if (nvp.getName().compareTo(MraidInterface.get_RESIZE_PROPERTIES_name(MraidInterface.RESIZE_PROPERTIES.OFFSET_Y)) == 0)
					{
						pixels = AdSizeUtilities.devicePixelToMraidPoint(Integer.parseInt(nvp.getValue()), context);
						newNvp = new BasicNameValuePair(nvp.getName(), "" + pixels);
						index = list.indexOf(nvp);
						list.remove(index);
						list.add(index, newNvp);
					}
				}
			}
		}
	}
}
