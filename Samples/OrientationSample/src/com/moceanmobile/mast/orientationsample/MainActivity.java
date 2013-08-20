package com.moceanmobile.mast.orientationsample;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import com.moceanmobile.mast.MASTAdView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig)
	{
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {

        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {

        }
        
        final MASTAdView adView = (MASTAdView) findViewById(R.id.adView);
        
        // Only update the ad for the new orientation after the layout has completed.
        // Normally it'd be better to not bother with this and let the ad update on it's interval
        // and scale as needed.  In the case this has to be done, update should be called
        // after the layout occurs unless the future sizes are known ahead of time in which
        // case overriding the MASTAdView default/calculated size_x/size_y would work.
        adView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() 
			{
				// Deprecated but the project supports <11.
				adView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				adView.update();
			}
		});
	}
}
