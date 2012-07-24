package com.moceanmobile.flickrviewer;

import java.util.HashMap;
import java.util.Vector;

import android.os.Handler;

public class FlickrFeed extends FlickrDataFeed
{
	/**
	 * URL to get feed of public photos from flickr in json data format
  	 */
	private static final String feedUrl = "http://api.flickr.com/services/feeds/photos_public.gne?format=json";

	/**
	 * URL to get the favorites for a particular user (id xxx) in json data format
	 */
	//private static final String favoriteFeedUrl = "http://api.flickr.com/services/feeds/photos_faves.gne?format=json&id=xxx";
	
	// handler object used to send message back to UI activity after an update
	private Handler parentHandler;

	
	public FlickrFeed(Handler activityHandler)
	{
		super();
		parentHandler = activityHandler;
	}

	public int getDataSource()
	{
		return DATA_SOURCE_FLICKR;
	}
	
	public boolean refresh()
	{
		flickrItems.clear();

		Thread worker = new Thread()
		{
			public void run()
			{
				FlickrParser parser = new FlickrParser(feedUrl, "GET", new FlickrParser.DataConsumer()
				{
					@Override
					public void saveDataItem(HashMap<String,String>data)
					{
						flickrItem datum = new flickrItem();
						datum.author = data.get("author");
						datum.title = data.get("title");
						datum.published = data.get("published");
						datum.media = data.get("m");
						datum.description = data.get("description");
						datum.tags = data.get("tags");
						addItem(datum);
					}
					
					@Override
					public void completedNotification(boolean withError)
					{
						if (withError)
						{
							System.out.println("parsing completed with error, not updating app");
							return;
						}
						
						System.out.println("parsing completed, updating app");
						parentHandler.post(new Runnable()
						{
							public void run()
							{
								notifyFeedUpdated();
							}
						});	
					}
				});
				
				try
				{
					parser.start();
				}
				catch(Exception ex)
				{
					System.out.println("Exception from parser: " + ex.getMessage());
				}
			}
		};
		
		worker.start();
		
		return true;
	}
}
