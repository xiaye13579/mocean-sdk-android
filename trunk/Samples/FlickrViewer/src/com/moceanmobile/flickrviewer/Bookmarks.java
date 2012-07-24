package com.moceanmobile.flickrviewer;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.moceanmobile.flickrviewer.FlickrDataFeed.flickrItem;


public class Bookmarks
{
	private HashSet<String> bookmarks;
	
	// Setting storage (non-DB private storage)
	private SharedPreferences appSettings;
	
	// The names of the sharedPreferences we are going to manage within this class
	public static final String APP_SETTINGS = "app.bookmarks";

	// Prefix used for storing bookmark items
	private static final String bookmarkSettingPrefix = "bookmark-";
	
	public Bookmarks(Context context)
	{
		bookmarks = new HashSet<String>();
		appSettings = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
	}
	
	
	public void add(String url)
	{
		bookmarks.add(url);
	}
	
	
	public boolean remove(String url)
	{
		return bookmarks.remove(url);
	}
	
	public void clear()
	{
		bookmarks.clear();
	}
	
	public int getCount()
	{
		return bookmarks.size();
	}
	
	public String[] getAll()
	{
		if (bookmarks.size() < 1)
		{
			return null;
		}
	
		String[] list = new String[bookmarks.size()];
		Iterator i = bookmarks.iterator();
		String item;
		int index = 0;
		while (i.hasNext())
		{
			item = (String)i.next();
			list[index++] = item;
		}
		
		return list;
	}

	
	// Get bookmark data in flickrItem format, same as returned by feed,
	// for browsing from main activity.
	public FlickrDataFeed getFeed()
	{
		if (bookmarks.size() < 1)
		{
			return null;
		}
		
		// Variation of flickr feed to contain bookmark data
		FlickrDataFeed flickrItems = new FlickrDataFeed()
		{
			public int getDataSource()
			{
				return DATA_SOURCE_BOOKMARKS;
			}
		};
		
		Iterator i = bookmarks.iterator();
		flickrItem datum;
		while (i.hasNext())
		{
			datum = new flickrItem();
			datum.media = (String)i.next();
			datum.author = "";
			datum.title = "Bookmark";
			datum.published = "";
			datum.tags = "";
			datum.description = "Saved Bookmark";
			flickrItems.addItem(datum);
		}
		
		return flickrItems;
	}
	

	// Simple/cheap save to settings of basic image URL only
	public int save()
	{
		if (bookmarks.size() < 1)
		{
			return 0;
		}
		
		Editor edit = appSettings.edit();
		
		Iterator i = bookmarks.iterator();
		String item;
		int index = 0;
		while (i.hasNext())
		{
			item = (String)i.next();
			System.out.println("Saved bookmark: " + item);
			edit.putString(bookmarkSettingPrefix + index, item);
			index += 1;
		}
		
		edit.commit();
		return index;
	}
	
	
	// Simple/cheap load of bookmark URLs from settings
	public int load()
	{
		Map<String,?> loaded = appSettings.getAll();
		if ((loaded == null) || (loaded.size() < 1))
		{
			return -1;
		}
		
		bookmarks.clear();
		Collection<?> c = loaded.values();
		Iterator i = c.iterator();
		String item;
		while (i.hasNext())
		{
			item = (String)i.next();
			System.out.println("Loaded bookmark: " + item);
			bookmarks.add(item);
		}
		
		return bookmarks.size();
	}
}
