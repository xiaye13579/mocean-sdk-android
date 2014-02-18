package com.moceanmobile.mast.samples;

import com.moceanmobile.mast.MASTAdView;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdvancedListNative extends ListActivity {

	private ItemsListAdapter itemsListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		itemsListAdapter = new ItemsListAdapter();
		
		setListAdapter(itemsListAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	private class ItemsListAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return 24;
		}

		@Override
		public Object getItem(int position)
		{
			int mod = position % 2;
			if (mod == 0)
			{
				return null;
			}
			
			return "Item Position " + String.valueOf(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Object item = getItem(position);
			
			View view = null;
			
			if (item != null)
			{
				view = LayoutInflater.from(getBaseContext()).inflate(R.layout.advanced_list_string_item, parent, false);

				TextView textView = (TextView) view.findViewById(R.id.listEntryText);
				textView.setText(item.toString());
			}
			else
			{
				view = LayoutInflater.from(getBaseContext()).inflate(R.layout.advanced_list_ad_item, parent, false);
				MASTAdView adView = (MASTAdView) view.findViewById(R.id.adView);
				
				if ((position == 4) || (position == 8) || (position == 12))
				{
					adView.setZone(88269);
				}
				else
				{
					adView.setZone(89888);
				}
			}
			
			return view;
		}
	}
}
