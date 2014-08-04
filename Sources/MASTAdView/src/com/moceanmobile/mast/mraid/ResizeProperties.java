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



public class ResizeProperties
{
	public static ResizeProperties propertiesFromArgs(Map<String, String> args)
	{
		ResizeProperties properties = new ResizeProperties();
		
		try
		{
			properties.setWidth(Integer.parseInt(args.get(Consts.PropertiesWidth)));
			properties.setHeight(Integer.parseInt(args.get(Consts.PropertiesHeight)));
			properties.setOffsetX(Integer.parseInt(args.get(Consts.ResizePropertiesOffsetX)));
			properties.setOffsetY(Integer.parseInt(args.get(Consts.ResizePropertiesOffsetY)));
			properties.setAllowOffscreen(Consts.True.equals(args.get(Consts.ResizePropertiesAllowOffscreen)));
			
			String customClosePositionString = args.get(Consts.ResizePropertiesCustomClosePosition);
			if (Consts.ResizePropertiesCCPositionTopLeft.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.TopLeft);
			}
			else if (Consts.ResizePropertiesCCPositionTopCenter.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.TopCenter);
			}
			else if (Consts.ResizePropertiesCCPositionTopRight.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.TopRight);
			}
			else if (Consts.ResizePropertiesCCPositionCenter.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.Center);
			}
			else if (Consts.ResizePropertiesCCPositionBottomLeft.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.BottomLeft);
			}
			else if (Consts.ResizePropertiesCCPositionBottomCenter.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.BottomCenter);
			}
			else if (Consts.ResizePropertiesCCPositionBottomRight.equals(customClosePositionString))
			{
				properties.setCustomClosePosition(Consts.CustomClosePosition.BottomRight);
			}
		}
		catch (Exception ex)
		{
			// TODO: Error handler?
		}
		
		return properties;
	}
	
	private int width = 0;
	private int height = 0;
	private Consts.CustomClosePosition customClosePosition = Consts.CustomClosePosition.TopRight;
	private int offsetX = 0;
	private int offsetY = 0;
	private boolean allowOffscreen = false;
	
	public ResizeProperties()
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

	public Consts.CustomClosePosition getCustomClosePosition()
	{
		return customClosePosition;
	}

	public void setCustomClosePosition(Consts.CustomClosePosition customClosePosition)
	{
		this.customClosePosition = customClosePosition;
	}

	public int getOffsetX()
	{
		return offsetX;
	}

	public void setOffsetX(int offsetX)
	{
		this.offsetX = offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}

	public void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}

	public boolean getAllowOffscreen()
	{
		return allowOffscreen;
	}

	public void setAllowOffscreen(boolean allowOffscreen)
	{
		this.allowOffscreen = allowOffscreen;
	}
	
	@Override
	public String toString()
	{
		String customClosePositionString = Consts.ResizePropertiesCCPositionTopRight;
		switch (customClosePosition)
		{
			case TopLeft:
				customClosePositionString = Consts.ResizePropertiesCCPositionTopLeft;
				break;
			case TopCenter:
				customClosePositionString = Consts.ResizePropertiesCCPositionTopCenter;
				break;
			case TopRight:
				customClosePositionString = Consts.ResizePropertiesCCPositionTopRight;
				break;
			case Center:
				customClosePositionString = Consts.ResizePropertiesCCPositionCenter;
				break;
			case BottomLeft:
				customClosePositionString = Consts.ResizePropertiesCCPositionBottomLeft;
				break;
			case BottomCenter:
				customClosePositionString = Consts.ResizePropertiesCCPositionBottomCenter;
				break;
			case BottomRight:
				customClosePositionString = Consts.ResizePropertiesCCPositionBottomRight;
				break;
		}
		
		String allowOffscreenString = Consts.False;
		if (allowOffscreen)
			allowOffscreenString = Consts.True;
		
		String format = "{width:%d,height:%d,customClosePosition:'%s',offsetX:%d,offsetY:%d,allowOffscreen:%s}";
		Formatter formatter = new Formatter(Locale.US);
		formatter.format(format, width, height, customClosePositionString, offsetX, offsetY, allowOffscreenString);
		String toString = formatter.toString();
		formatter.close();
		
		return toString;
	}
}
