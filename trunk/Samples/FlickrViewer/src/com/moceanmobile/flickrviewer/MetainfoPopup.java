package com.moceanmobile.flickrviewer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdView;


public class MetainfoPopup
{
	private Activity parentActivity = null;
	private DisplayMetrics metrics = null;
	private Bookmarks bookmarks = null; 
	private LayoutInflater inflater = null;
	
	
	public MetainfoPopup(Activity parent, DisplayMetrics dimensions, Bookmarks bm) 
	{
		parentActivity = parent;
		metrics = dimensions;
		bookmarks = bm;

		inflater = LayoutInflater.from(parent);
	}
	
	
	// Create dialog showing meta-information about the current feed item
	// (really only useful for an actual flickr feed; bookmarks drop most of this data)
	public AlertDialog createDialog(final FlickrDataFeed.flickrItem item, int feedSource)
	{
    	if (item == null)
    	{
    		return null;
    	}
   
    	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(parentActivity);
		alertBuilder.setTitle("Picture Information"); 
		
		View v = inflater.inflate(R.layout.metainfo_dialog, null);
		
		// Show full author name
		EditText e = (EditText) v.findViewById(R.id.metaAuthor);
		if (item.author != null)
		{
			e.setText(item.author);
		}
		
		// Show full title
		e = (EditText) v.findViewById(R.id.metaTitle);
		if (item.title != null)
		{
			e.setText(item.title);
		}
		
		// Show full description; flickr descriptions are HTML fragments,
		// so wrap this with prefix and display in a web view.
		WebView wv = (WebView) v.findViewById(R.id.metaDescription);
		if (item.description != null)
		{
			String description = "<HTML><BODY>" + item.description;
			wv.loadData(description, "text/html", null); 
		}
		else
		{
			wv.setVisibility(View.GONE);
		}
		
		// Show full publication date
		e = (EditText) v.findViewById(R.id.metaDate);
		if (item.published != null)
		{
			e.setText(item.published);
		}		
		
		// Show full tag list
		e = (EditText) v.findViewById(R.id.metaTags);
		if (item.tags != null)
		{
			e.setText(item.tags);
		}
		
		// Make sure ad view is updated
		MASTAdView ad = (MASTAdView)v.findViewById(R.id.metaAdView);
		ad.setLogLevel(Constants.defaultAdLogLevel);
		ad.setBackgroundColor(Color.BLACK);
		// limit ad width to 70% of screen width because fill_parent isn't reliable here
		ad.setMaxSizeX((int)(metrics.widthPixels * Constants.metainfoPopupWidthFactor));
		// Center ad
		//ad.setInjectionBodyCode(FlickrViewerActivity.injectionString);
		ad.update();
		
		alertBuilder.setView(v);  
		alertBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() 
		{ 
			public void onClick(DialogInterface dialog, int whichButton) 
			{   
				dialog.dismiss();
			} 
		});
		
		// From Flickr feed, can add bookmarks, from bookmark feed, can remove them
		if (feedSource == FlickrDataFeed.DATA_SOURCE_FLICKR)
		{
			alertBuilder.setNegativeButton("Bookmark", new DialogInterface.OnClickListener() 
			{ 
				public void onClick(DialogInterface dialog, int whichButton) 
				{   
					bookmarks.add(item.media);
					bookmarks.save();
				} 
			});
		}
		else if (feedSource == FlickrDataFeed.DATA_SOURCE_BOOKMARKS)
		{
			alertBuilder.setNegativeButton("Forget", new DialogInterface.OnClickListener() 
			{ 
				public void onClick(DialogInterface dialog, int whichButton) 
				{   
					bookmarks.remove(item.media);
					bookmarks.save();
				} 
			});
		}
		
		return alertBuilder.create();
	}
}
