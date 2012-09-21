//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;


public class OrientationChangeListener extends OrientationEventListener
{
	private Context context;
	//private AdWebView adWebView;
	private Display activeDisplay;
	private static OrientationChangeListener instance = null;
	private int lastOrientation = ORIENTATION_UNKNOWN;
	private ArrayList<AdViewContainer> observedViews;
	
	
	public OrientationChangeListener(Context context, Display display)
	{
		super(context);
		super.enable();
		
		this.context = context;
		activeDisplay = display;
		//adWebView = ad;
		observedViews = new ArrayList<AdViewContainer>();
	}
	
	
	synchronized public static OrientationChangeListener getInstance(Context c, Display d)
	{
		if (instance == null)
		{
			instance = new OrientationChangeListener(c, d);
		}
		
		return instance;
	}
	
	
	public void addView(AdViewContainer ad)
	{
		observedViews.add(ad);
	}
	
	
	/*
	 * Called when the orientation of the device has changed. orientation parameter is in degrees,
	 * ranging from 0 to 359. orientation is 0 degrees when the device is oriented in its natural
	 * position, 90 degrees when its left side is at the top, 180 degrees when it is upside down,
	 * and 270 degrees when its right side is to the top. ORIENTATION_UNKNOWN is returned when the
	 * device is close to flat and the orientation cannot be determined.
	 * 
	 * 
	 * 
	 * XXX we don't are about the angle, just want to latch on to the device orientation itself
	 * 
	 * 
	 * 
	 */
	public void onOrientationChanged(int orientation)
	{
		// Get device rotation value as well, so we can do the right thing for tablets and other
		// devices that are naturally "landscape" instead of "portrait" like a phone.
		
		//int baseRotation = activeDisplay.getRotation();
		int screenOrientation = context.getResources().getConfiguration().orientation;
		
		if (screenOrientation != lastOrientation)
		{
			// Orientation has changed, update each observed ad view
			lastOrientation = screenOrientation;
		
			Iterator<AdViewContainer> i = observedViews.iterator();
			AdViewContainer ad;
			while (i.hasNext())
			{
				ad = i.next();
				ad.onOrientationChange(orientation, screenOrientation);
			}
		}
	}
}
