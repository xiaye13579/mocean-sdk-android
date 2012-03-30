package checkpoint.forms;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.MASTAdView.MASTAdView;

public class TestListAdapter extends BaseAdapter {
	protected LayoutInflater mInflater;
	Context context;
	int maxCount = 1000;
	ArrayList<MASTAdView> ads = new ArrayList<MASTAdView>(maxCount);
	
	public TestListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);

		this.context =context;
		for(int x=0;x<maxCount;x++)
			ads.add(null);
	}
	
	@Override
	public int getCount() {		
		return maxCount;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		MASTAdView adserverView;
		if(ads.get(arg0)==null)
		{
			adserverView = new MASTAdView(context, 8061, 20249);
		    adserverView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			//adserverView.setLayoutParams(new ViewGroup.LayoutParams(320, 50));
			adserverView.update();
		    adserverView.setMaxSizeX(320);
		    adserverView.setMaxSizeY(50);
		    adserverView.setBackgroundColor(0);
			ads.set(arg0, adserverView);
		}else adserverView = ads.get(arg0);
		
		if(arg1==null)
		{
			arg1 = mInflater.inflate(R.layout.list_item, null);
			//arg1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 50));
		}
		((LinearLayout)arg1).removeAllViews();
		((LinearLayout)arg1).addView(adserverView);
		return arg1;
	}

}
