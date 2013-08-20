package com.moceanmobile.mast.samples;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdvancedList extends ListActivity {

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
		getMenuInflater().inflate(R.menu.refresh_menu, menu);
		return true;
	}

	private class ItemsListAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return 50;
		}

		@Override
		public Object getItem(int position)
		{
			int mod = position % 5;
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
			}
			
			return view;
		}
	}
}
