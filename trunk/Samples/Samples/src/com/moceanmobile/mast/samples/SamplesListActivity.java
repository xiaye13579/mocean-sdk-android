package com.moceanmobile.mast.samples;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SamplesListActivity extends ListActivity
{
	SamplesListAdapter samplesListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		samplesListAdapter = new SamplesListAdapter();
		
		samplesListAdapter.addItem(new SamplesItem("Simple", "Image", SimpleImage.class));
		samplesListAdapter.addItem(new SamplesItem("Animated GIF", SimpleAnimatedGIF.class));
		samplesListAdapter.addItem(new SamplesItem("Interstitial", SimpleInterstitial.class));
		samplesListAdapter.addItem(new SamplesItem("Rich Media", SimpleRichMedia.class));
		samplesListAdapter.addItem(new SamplesItem("Text", SimpleText.class));
		
		samplesListAdapter.addItem(new SamplesItem("Advanced", "Animation", AdvancedAnimation.class));
		samplesListAdapter.addItem(new SamplesItem("Bottom", AdvancedBottom.class));
		samplesListAdapter.addItem(new SamplesItem("List", AdvancedList.class));
		samplesListAdapter.addItem(new SamplesItem("Top and Bottom", AdvancedTopAndBottom.class));
		
		samplesListAdapter.addItem(new SamplesItem("Custom", "Custom Ad Setup", CustomConfig.class));
		
		samplesListAdapter.addItem(new SamplesItem("Delegate", "Generic", DelegateGeneric.class));
		samplesListAdapter.addItem(new SamplesItem("MRAID Events", DelegateMRAID.class));
		samplesListAdapter.addItem(new SamplesItem("Third Party Request", DelegateThirdParty.class));
		samplesListAdapter.addItem(new SamplesItem("Logging", DelegateLogging.class));
		samplesListAdapter.addItem(new SamplesItem("No Content Zone", DelegateNoContent.class));
		
		samplesListAdapter.addItem(new SamplesItem("Error", "Hide", ErrorHide.class));
		samplesListAdapter.addItem(new SamplesItem("Image", ErrorImage.class));
		samplesListAdapter.addItem(new SamplesItem("Reset", ErrorReset.class));
		
		super.setListAdapter(samplesListAdapter);
	}
	
	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id)
	{
		SamplesItem item = (SamplesItem) listView.getAdapter().getItem(position);
		
		Intent intent = new Intent(this, item.activity);
		startActivity(intent);
	}
	
	private class SamplesItem
	{
		public final String name;
		public final String header;
		public final Class<?> activity;
		
		public SamplesItem(String header, String name, Class<?> activity)
		{
			this.name = name;
			this.header = header;
			this.activity = activity;
		}
		
		public SamplesItem(String name, Class<?> activity)
		{
			this.name = name;
			this.header = null;
			this.activity = activity;
		}
	}
	
	private class SamplesListAdapter extends BaseAdapter
	{
		private List<SamplesItem> items = new ArrayList<SamplesItem>();
		
		public void addItem(SamplesItem item)
		{
			items.add(item);
		}
		
		@Override
		public int getCount()
		{
			return items.size();
		}

		@Override
		public Object getItem(int position)
		{
			return items.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.samples_list_item, parent, false);

			SamplesItem item = (SamplesItem) getItem(position);
			
			TextView headerView = (TextView) view.findViewById(R.id.header);
			if (TextUtils.isEmpty(item.header) == false)
			{
				headerView.setVisibility(View.VISIBLE);
				headerView.setText(item.header);
			}
			
			TextView nameView = (TextView) view.findViewById(R.id.name);
			nameView.setText(item.name);
			
			return view;
		}
	}
}
