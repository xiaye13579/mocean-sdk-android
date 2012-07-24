package com.moceanmobile.flickrviewer;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class ApplicationMenu
{
	public static final int BOOKMARKS_ID 			= Menu.FIRST;
	

	// Reference to activity object of parent using this menu
	private Activity callerActivity;
	
	
	public ApplicationMenu(Activity caller)
	{
		callerActivity = caller;
	}

	private boolean requestedMenuItem(int id, int[] list)
	{
		boolean found = false;
		
		for (int n = 0; n < list.length; n++)
		{
			if (list[n] == id)
			{
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	/** This is called when the menu is being created and give you a chance to add items. */
    public boolean onCreateOptionsMenu(Menu menu, int[] menuItems)
    {    
        // Add custom menu items
    	MenuItem menuItem;
    	
    	if (requestedMenuItem(BOOKMARKS_ID, menuItems))
    	{
    		menuItem = menu.add(0, BOOKMARKS_ID, 0, "Bookmarks");
    		//menuItem.setIcon(R.drawable.menureports);
    	}
    	
    	return true;
    }
    
    /** This is called when a menu item is selected. */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	Intent i;
    	
        switch(item.getItemId())
        {
        case BOOKMARKS_ID:
        	// Launch bookmarks activity, unless already viewing it
        	if ((callerActivity instanceof BookmarkViewer) == false)
        	{
        		i = new Intent(callerActivity, BookmarkViewer.class);
	        	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        	BookmarkViewer.setMainActivity((FlickrViewerActivity)callerActivity);
	        	callerActivity.startActivity(i);
        	}
        	return true;
        }
        
        return false;
    }
}
