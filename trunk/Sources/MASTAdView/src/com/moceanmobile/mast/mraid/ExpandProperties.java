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

package com.moceanmobile.mast.mraid;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class ExpandProperties
{
	public static ExpandProperties propertiesFromArgs(Map<String, String> args)
	{
		ExpandProperties properties = new ExpandProperties();
		
		try
		{
			properties.setWidth(Integer.parseInt(args.get(Consts.PropertiesWidth)));
			properties.setHeight(Integer.parseInt(args.get(Consts.PropertiesHeight)));
			properties.setUseCustomClose(Consts.True.equals(args.get(Consts.ExpandPropertiesUseCustomClose)));
		}
		catch (Exception ex)
		{
			// TODO: Error handling?
		}
		
		return properties;
	}
	
	private int width = 0;
	private int height = 0;
	private boolean useCustomClose = false;
	
	public ExpandProperties()
	{
		
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public boolean useCustomClose()
	{
		return useCustomClose;
	}

	public void setUseCustomClose(boolean useCustomClose)
	{
		this.useCustomClose = useCustomClose;
	}
	
	@Override
	public String toString()
	{
		String useCustomCloseString = Consts.False;
		if (useCustomClose)
			useCustomCloseString = Consts.True;
		
		String format = "{width:%d,height:%d,useCustomClose:%s}";
		Formatter formatter = new Formatter(Locale.US);
		formatter.format(format, width, height, useCustomCloseString);
		String toString = formatter.toString();
		formatter.close();
		
		return toString;
	}
}
