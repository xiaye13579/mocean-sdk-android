package com.MASTAdView.samples;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.MASTAdView.MASTAdConstants;
import com.MASTAdView.MASTAdLog;
import com.MASTAdView.samples.simple.Interstitial;

public class ApiDemos extends ListActivity {

	public static final int BANNER_HEIGHT = 100;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        Intent intent = getIntent();

        MASTAdLog.setDefaultLogLevel(MASTAdLog.LOG_LEVEL_3);
        MASTAdLog.setMaximumLogCount(500); // save up to 500 message in memory for display through app
        
        String path = intent.getStringExtra("com.MASTAdView.sample.Path");
        
        if (path == null) {
            path = "";

            String versionName = "";
            try {
    			PackageInfo pinfo;
    			pinfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
    			if(pinfo!=null)
    			{
    				versionName = pinfo.versionName;
    			}						
    			
    		} catch (Exception e) {			
    		}

            setTitle(getTitle()+ " SDK="+MASTAdConstants.SDK_VERSION + " appVersionName="+versionName);
        }
        else setTitle(path);
        	

        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
        getListView().setTextFilterEnabled(true);
        
        MASTAdLog.setDefaultLogLevel(MASTAdLog.LOG_LEVEL_3);
        
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    protected List getData(String prefix) {
        List<Map> myData = new ArrayList<Map>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

               
        if (null == list)
            return myData;

        String[] prefixPath;
        
        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
        }
       
        int len = list.size();
       
        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {        	
            ResolveInfo info = list.get(i);
            String str =list.get(0).getClass().getPackage().toString();
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null
                    ? labelSeq.toString()
                    : info.activityInfo.name;
            
            if ((prefix.length() == 0 || label.startsWith(prefix)) && 
            		info.activityInfo.applicationInfo.packageName.equals( "com.MASTAdView.samples")) {
            	
            	//System.out.println(label);    
                String[] labelPath = label.split("/");

                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];
                
                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1)
                {
                    addItem(myData, nextLabel, activityIntent(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name));
                }
                else 
                {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);
        
        return myData;
    }

    private final static Comparator<Map> sDisplayNameComparator = new Comparator<Map>() {
        private final Collator   collator = Collator.getInstance();

        public int compare(Map map1, Map map2) {
            return collator.compare(map1.get("title"), map2.get("title"));
        }
    };

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }
    
    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, ApiDemos.class);
        result.putExtra("com.MASTAdView.sample.Path", path);
        return result;
    }

    protected void addItem(List<Map> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map map = (Map) l.getItemAtPosition(position);
        Intent intent = (Intent) map.get("intent");
        
        if (intent.getComponent().getClassName().equals(Interstitial.class.getName())) {
    		Rect rectgle= new Rect();
    		getWindow().getDecorView().getWindowVisibleDisplayFrame(rectgle);
    		int statusBarHeight = rectgle.top;		
        	
			intent.putExtra(Interstitial.PARAMETER_STATUS_BAR_HEIGHT, statusBarHeight);
		}
        
        startActivity(intent);
    }

}
