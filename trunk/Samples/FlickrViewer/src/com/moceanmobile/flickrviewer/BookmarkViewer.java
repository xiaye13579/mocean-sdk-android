package com.moceanmobile.flickrviewer;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.MASTAdView.MASTAdLog;
import com.MASTAdView.MASTAdView;


public class BookmarkViewer extends Activity implements OnItemClickListener
{
	// Class-wide reference to ad view object for banner and interstitial
	private MASTAdView adView = null;

	// Bookmarks
	private Bookmarks bookmarks = null;
	
	// Listview for bookmark data
	private ListView bookmarkList = null;

	// Parent activity reference
	private static FlickrViewerActivity mainActivity = null;
	
	// Original parent feed
	private FlickrDataFeed mainFeed = null;


	// Setup thread pool queue and executor used for getting thumbnails
	private ThreadPoolExecutor executor;
	private PriorityBlockingQueue<Runnable> workQueue;
	

	// Handler so that UI can be notified to update when images are updated
	private Handler handler = new Handler();
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	// Get device size information, save
    	WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	//metrics = new DisplayMetrics();
		//windowManager.getDefaultDisplay().getMetrics(metrics);
	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarks);
        
        if (mainActivity != null)
        {
        	mainFeed = mainActivity.getDataFeed();
        }
        
        // Get reference to ad view for banner ad; we will randomly use either the top or bottom
        // ad, and mark it visible; the other will be removed because just leaving it gone doesn't
        // seem to do the trick (BUG!!!)
    	adView = (MASTAdView)findViewById(R.id.bookmarkBanner);
        adView.setUpdateTime(Constants.banner_ad_update_time);
        adView.setBackgroundColor(Color.BLACK);
        adView.setLogLevel(Constants.defaultAdLogLevel);
        //adView.setInjectionBodyCode("<body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%; display:-webkit-box;-webkit-box-orient:horizontal;-webkit-box-pack:center;-webkit-box-align:center;\">");
        
        // Get reference to layout listview so we can configure it, and do so
        bookmarkList = (ListView)findViewById(R.id.bookmarkListview);
        bookmarkList.setOnItemClickListener(this);
        
        // Thread pool queue for getting images
        workQueue = new PriorityBlockingQueue<Runnable>();
    	executor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, workQueue);
    }
    
    /** Called when the activity returns to the front of the stack **/
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	// Update the banner ad view each time the screen is resumed, just to keep it
    	// refreshed. This will be triggered each time the interstitial view closes.
    	// The ad view also has an auto-refresh if more than X seconds (45 for now)
    	// goes by without an update.
    	if (adView != null)
    	{
    		adView.update();
    	}
    	
    	// Get bookmark data
        if (mainActivity != null)
        {
        	bookmarks = mainActivity.getBookmarks();
        	
        	if (mainFeed == null)
            {
            	mainFeed = mainActivity.getDataFeed();
            }
        }
        
    	if ((bookmarks != null) && (bookmarks.getCount() > 0))
    	{
	    	String[] data = bookmarks.getAll();
	        BookmarkListAdapter adapter = new BookmarkListAdapter(this.getApplicationContext(), R.layout.bookmark_item, data);
			bookmarkList.setAdapter(adapter);
    	}
    	else
    	{
    		bookmarkList.setVisibility(View.INVISIBLE);
    	}
    }
    
    
    public static void setMainActivity(FlickrViewerActivity activity)
    {
    	mainActivity = activity;
    }
    
    
    /**
     * Click listener which handles clicks on a row in our ListView.
     * Set the data feed in the main activity to a feed created from the current
     * bookmarks, re-start at position 0, and display that image.
     */
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
    {	
    	if ((bookmarks != null) && (mainActivity != null))
    	{
    		mainActivity.setDataFeed(bookmarks.getFeed());
    		mainActivity.setFeedIndex(position);
    		mainActivity.showFlickrImage();
    		finish();
    	}
    }
    
    
    // Get image, show it as a thumbnail, but ONLY if the view we are targeting
    // still references the same URL we started with. This is important because
    // of the way Android will reuse views in a list (so by the time the image
    // download completes, the cell we started off could have scrolled off-screen
    // and been re-used for a different bookmark list item.)
    private class DownloadWorker implements Runnable, Comparable<Object>
    {
    	protected View layoutView;
    	protected ImageView imageView;
    	protected String imageUrl;
    	
    	public DownloadWorker(View lv, ImageView iv,String url)
    	{
    		layoutView = lv;
    		imageView = iv;
    		imageUrl = url;
    	}
    	
    	public void run()
    	{
    		if ((layoutView != null) && (imageUrl != null))
	    	{
	    		final Bitmap image = ImageUtils.fetchImage(imageUrl);
	    		final String checkUrl = (String)layoutView.getTag();
	    		if (imageUrl.compareTo(checkUrl) == 0)
				{
	    			handler.post(new Runnable()
					{
						public void run()
						{
							if (imageUrl.compareTo(checkUrl) == 0)
							{
								imageView.setImageBitmap(image);
								imageView.setVisibility(View.VISIBLE);
							}
							else
							{
								image.recycle();			
							}
						}
					});
				}
	    		else
	    		{
	    			image.recycle();
	    		}
	    	}		
    	}
    	
    	public int compareTo(Object another)
    	{
    		DownloadWorker otherObj = (DownloadWorker)another;
    		return imageUrl.compareTo(otherObj.imageUrl);
    	}
    }
    		
    
    // Execute download worker on thread pool queue, so w don't overload the system
    // by spawning a thread for every single bookmark. Could optimize this by creating
    // an on-device cache...
    public void getThumbnail(View lv, ImageView iv, String url)
    {
    	DownloadWorker worker = new DownloadWorker(lv, iv, url);
    	executor.execute(worker);
    }
    
    
    private class BookmarkListAdapter extends ArrayAdapter<Object>
    {
    	private String[] items;
        private Context myContext;
        private int viewId;
        private LayoutInflater mInflater;
        
        
        public BookmarkListAdapter(Context context, int viewResourceId, String[] data) 
        {
            super(context, viewResourceId, data);
            
            myContext = context;
            viewId = viewResourceId;
            items = data;
            
            mInflater = LayoutInflater.from(context);
        }
        
        
        /**
         * Called to get a view for a particular row.  The row is specified by
         * the position variable.  The convertView variable is a reusable already
         * made row.  If it is null then we have to create the view.
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
        	// NOTE: re-using pre-existing views, but not using the further optimization
        	// of using a view holder to avoid redundant findview calls. Don't think our
        	// list is going to get big enough to matter.
        	
        	View v;
        	if (convertView == null)
        	{
        		v = mInflater.inflate(viewId, parent, false);
        	}
        	else
        	{
        		v = convertView;
        	}
        	
        	// Set tag so we can identify this view when image is loaded
        	v.setTag(items[position]);
        	
        	// Show thumbnail preview of image (fetch runs on another thread)
        	ImageView thumb = (ImageView)v.findViewById(R.id.thumbnailImage);
        	getThumbnail(v, thumb, items[position]);
        	thumb.setVisibility(View.INVISIBLE);
        	
        	// Show URL
        	TextView url = (TextView)v.findViewById(R.id.bookmarkUrl);
        	url.setText(items[position]);
        	
        	// Return view for use in list
        	return v;
        }
    }
}
