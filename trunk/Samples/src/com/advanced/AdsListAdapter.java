package com.MASTAdView.samples.advanced;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.MASTAdView.MASTAdView;
import com.MASTAdView.samples.R;


public class AdsListAdapter extends BaseAdapter {
	protected Context context;
    protected List<MASTAdView> contentItems = new Vector<MASTAdView>();
    protected LayoutInflater mInflater;
    protected int itemResourceId;

	public AdsListAdapter(Context context, Vector<MASTAdView> contentItems) {
		this.context = context;
        mInflater = LayoutInflater.from(context);
        itemResourceId = R.layout.custom_list_item;
		this.contentItems = contentItems;
	}

	@Override
	public int getCount() {
		return contentItems.size();
	}

	@Override
	public Object getItem(int position) {
		return contentItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(itemResourceId, null);

            holder = new ViewHolder();
            holder.frameAdContent = (LinearLayout) convertView.findViewById(R.id.frameAdContent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MASTAdView contentItem = (MASTAdView)contentItems.get(position);
        
        LinearLayout viewParent = (LinearLayout)contentItem.getParent();
        if (viewParent != null) {
        	viewParent.removeAllViews();
		}
        
        holder.frameAdContent.addView(contentItem);
        contentItem.update();
		
        return convertView;
	}
	
    static class ViewHolder {
    	LinearLayout frameAdContent;
    }

}
