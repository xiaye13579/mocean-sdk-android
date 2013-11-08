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
