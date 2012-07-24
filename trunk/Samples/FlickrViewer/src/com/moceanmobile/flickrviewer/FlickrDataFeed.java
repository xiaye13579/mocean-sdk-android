package com.moceanmobile.flickrviewer;


import java.util.Vector;

/**
 * Base class for a "feed" of data objects representing data from flickr; 
 * can be used to parse & return data pulled from the flickr API over the net,
 * or to return bookmarked URLs saved by the user.
 */
public class FlickrDataFeed
{
	// Object encapsulating a flickr item based on above data
	public static class flickrItem
	{
		public String title = null;
		//public String link;
		public String media = null;
		//public String date_taken = null;
		public String description = null;
		public String published = null;
		public String author = null;
		//public String author_id;
		public String tags = null;
	}

	// Source of data this objet contains
	public static final int DATA_SOURCE_UNKNOWN		= -1;
	public static final int DATA_SOURCE_FLICKR 		= 1;
	public static final int DATA_SOURCE_BOOKMARKS	= 2;
	
	// vector of flickr items containing data from above
	protected Vector<flickrItem> flickrItems = null;
	
	public FlickrDataFeed()
	{
		flickrItems = new Vector<flickrItem>();
	}
	
	public boolean refresh()
	{
		// Override this with suitable code
		return false;
	}
	
	public int itemCount()
	{
		return flickrItems.size();
	}
	
	public flickrItem getItem(int index)
	{
		if ((index < 0) || (index > flickrItems.size()))
		{
			index = 0;
		}
		return flickrItems.elementAt(index);
	}
	
	public void addItem(flickrItem datum)
	{
		if ((datum != null) && (datum.media != null))
		{
			System.out.println("flickrfeed: adding item, media=" + datum.media);
			flickrItems.addElement(datum);	
		}
	}	
	
	public void notifyFeedUpdated()
	{
		// Override this with suitable code
	}
	
	public int getDataSource()
	{
		// Orverrie this in dervied classes!!!
		return DATA_SOURCE_UNKNOWN;
	}
}
