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
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ThirdPartyDescriptor
{
	public static ThirdPartyDescriptor parseDescriptor(String content) throws XmlPullParserException, IOException
	{
        final String start = "<external_campaign";
        final String end = "</external_campaign>";
        
        int startIndex = content.indexOf(start);
        int endIndex = content.indexOf(end, startIndex);
        
        if ((startIndex == -1) || (endIndex == -1))
        	return null;

        content = content.substring(startIndex, endIndex + end.length());
        
        ThirdPartyDescriptor thirdPartyDescriptor = new ThirdPartyDescriptor();
        
        Stack<String> elementStack = new Stack<String>();
        Map<String, String> elementAttributes = new HashMap<String, String>();
        String elementContent = null;
        
        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
		parserFactory.setNamespaceAware(false);
		parserFactory.setValidating(false);
		
		XmlPullParser parser = parserFactory.newPullParser();
		parser.setInput(new StringReader(content));
		
		while ("external_campaign".equals(parser.getName()) == false)
			parser.next();
		
		parser.next();
		int eventType = parser.getEventType();
		
		parsing:
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			switch (eventType)
			{
			case XmlPullParser.START_TAG:
			{
				elementStack.push(parser.getName());
				elementAttributes.clear();
				elementContent = null;
				
				int c = parser.getAttributeCount();
				for (int i = 0; i < c; ++i)
				{
					String attributeName = parser.getAttributeName(i);
					String attributeValue = parser.getAttributeValue(i);
					
					elementAttributes.put(attributeName, attributeValue);
				}
			}
			break;
			
			case XmlPullParser.END_TAG:
			{
				if (elementStack.size() == 0)
					break parsing;
				
				String key = elementStack.pop();
				
				if (elementContent != null)
				{
					if ("param".equals(key))
					{
						if (elementAttributes.containsKey("name"))
						{
							key = elementAttributes.get("name");
							
							thirdPartyDescriptor.getParams().put(key,
									elementContent);
						}
					}
					else
					{
						thirdPartyDescriptor.getProperties().put(key, 
								elementContent);
					}
				}
				
				elementAttributes.clear();
				elementContent = null;
			}
			break;
			
			case XmlPullParser.TEXT:
				elementContent = parser.getText();
				break;
			}
			
			eventType = parser.next();
		}
		
		return thirdPartyDescriptor;
	}
	
	private Map<String, String> properties = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	
	private ThirdPartyDescriptor()
	{
		
	}
	
	public Map<String, String> getProperties()
	{
		return properties;
	}
	
	public Map<String, String> getParams()
	{
		return params;
	}
}
