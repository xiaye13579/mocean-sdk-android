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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;

public class AdDescriptor
{
	/***
	 * Parses an ad descriptor from a pull parser that is parked on the
	 * "ad" start element.  Returns the parser on the "ad" end element.
	 * 
	 * If the result is null or an exception is thrown the parser may be
	 * parked nested in the ad tag.
	 * 
	 * @param parser
	 * @return Parsed AdDescriptor or null if an error was encountered.
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	public static AdDescriptor parseDescriptor(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		Map<String, String> adInfo = new HashMap<String, String>();
		
        String adType = parser.getAttributeValue(null, "type");
        adInfo.put("type", adType);
        
        // read past start tag
        parser.next();
        
        // read and populate ad info
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
        	String name = parser.getName();
        	String value = null;
        	
        	if ((eventType == XmlPullParser.END_TAG) && ("ad".equals(name)))
        	{
        		// done with the ad descriptor
        		break;
        	}
        	else if (eventType == XmlPullParser.START_TAG)
        	{
        		String subType = parser.getAttributeValue(null, "type");
        		if (TextUtils.isEmpty(subType) == false)
        		{
        			adInfo.put(name + "Type", subType);
        		}
        		
                parser.next();
                if (parser.getEventType() == XmlPullParser.TEXT)
                {
                	value = parser.getText();
                }
                
                if (TextUtils.isEmpty(value) == false)
                {
                	adInfo.put(name, value);
                }
        	}
        	
            parser.next();
            eventType = parser.getEventType();
        }
		
        AdDescriptor adDescriptor = new AdDescriptor(adInfo);
		return adDescriptor;
	}
	
	private final Map<String, String> adInfo;
	
	public AdDescriptor(Map<String, String> adInfo)
	{
		this.adInfo = adInfo;
	}
	
	public String getType()
	{
		String value = adInfo.get("type");
		return value;	
	}
	
	public String getURL()
	{
		String value = adInfo.get("url");
		return value;
	}
	
	public String getTrack()
	{
		String value = adInfo.get("track");
		return value;
	}
	
	public String getImage()
	{
		String value = adInfo.get("img");
		return value;
	}
	
	public String getImageType()
	{
		String value = adInfo.get("imgType");
		return value;
	}
	
	public String getText()
	{
		String value = adInfo.get("text");
		return value;
	}
	
	public String getContent()
	{
		String value = adInfo.get("content");
		return value;
	}
}
