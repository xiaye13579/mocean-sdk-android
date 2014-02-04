package com.moceanmobile.mast.interstitialswipe;

import java.util.ArrayList;
import java.util.List;

public class SwipeData
{
	static private SwipeData instance = null;
	static public SwipeData getInstance()
	{
		if (instance == null)
			instance = new SwipeData();
		
		return instance;
	}
	
	private int index = -1;
	private List<Object> data = new ArrayList<Object>();
	
	public boolean hasPrevious()
	{
		if (index > 0)
			return true;
		
		return false;
	}
	
	public boolean hasNext()
	{
		if (index < data.size() - 1)
			return true;
		
		return false;
	}
	
	public Object getCurrent()
	{
		return data.get(index);
	}
	
	public Object next()
	{
		if (hasNext() == false)
			throw new IndexOutOfBoundsException();
	
		++index;
		return getCurrent();
	}
	
	public Object previous()
	{
		if (hasPrevious() == false)
			throw new IndexOutOfBoundsException();
		
		--index;
		return getCurrent();
	}
	
	public void addItem(Object item)
	{
		data.add(item);
	}
}
