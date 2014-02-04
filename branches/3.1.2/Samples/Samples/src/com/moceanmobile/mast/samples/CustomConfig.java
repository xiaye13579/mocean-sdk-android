package com.moceanmobile.mast.samples;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.moceanmobile.mast.MASTAdView;

public class CustomConfig extends RefreshActivity {
	
	// Using object for tri-state.
	private Integer params_size_x = null;
	private Integer params_size_y = null;
	private Integer params_min_size_x = null;
	private Integer params_min_size_y = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_config_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			showSettingsDialog();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showSettingsDialog() {
		SettingsDialog dialog = new SettingsDialog(this);
		dialog.show();
	}

	private class SettingsDialog extends Dialog {
		public SettingsDialog(Context context) {
			super(context);

			super.setTitle("Settings");
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.custom_dialog);

			fillValues();

			Button resetButton = (Button) findViewById(R.id.reset_button);
			resetButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					resetValues();
					refreshAd();
					dismiss();
				}
			});
			
			Button saveButton = (Button) findViewById(R.id.save_button);
			saveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setViewLayout();
					setAdParams();
					refreshAd();
					dismiss();
				}
			});
		}
		
		private void fillValues() {
			MASTAdView adView = (MASTAdView) CustomConfig.this
					.findViewById(R.id.adView);
			
			// The adView is placed inside a relative layout.  
			// If not, this would be different.  It could also be dynamically checked
			// and then dealt with based on the type of LayoutParams as well.
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) adView.getLayoutParams();
			
			EditText marginTopText = (EditText) findViewById(R.id.margin_top);
			EditText marginLeftText = (EditText) findViewById(R.id.margin_left);
			EditText marginRightText = (EditText) findViewById(R.id.margin_right);
			EditText marginBottomText = (EditText) findViewById(R.id.margin_bottom);
			EditText sizeWidthText = (EditText) findViewById(R.id.size_width);
			EditText sizeHeightText = (EditText) findViewById(R.id.size_height);
			
			marginTopText.setText(String.valueOf(MASTAdView.pxToDp(layoutParams.topMargin)));
			marginLeftText.setText(String.valueOf(MASTAdView.pxToDp(layoutParams.leftMargin)));
			marginRightText.setText(String.valueOf(MASTAdView.pxToDp(layoutParams.rightMargin)));
			marginBottomText.setText(String.valueOf(MASTAdView.pxToDp(layoutParams.bottomMargin)));
			
			int value = layoutParams.width;
			if (value != LayoutParams.MATCH_PARENT)
				value = MASTAdView.pxToDp(value);
			sizeWidthText.setText(String.valueOf(value));
			
			value = layoutParams.height;
			if (value != LayoutParams.MATCH_PARENT)
				value = MASTAdView.pxToDp(value);
			sizeHeightText.setText(String.valueOf(value));
			
			EditText adSizeXText = (EditText) findViewById(R.id.ad_size_x);
			EditText adSizeYText = (EditText) findViewById(R.id.ad_size_y);
			EditText adMinSizeXText = (EditText) findViewById(R.id.ad_min_size_x);
			EditText adMinSizeYText = (EditText) findViewById(R.id.ad_min_size_y);
			
			adSizeXText.setText("");
			if (params_size_x != null)
				adSizeXText.setText(String.valueOf(params_size_x));
			
			adSizeYText.setText("");
			if (params_size_y != null)
				adSizeYText.setText(String.valueOf(params_size_y));
			
			adMinSizeXText.setText("");
			if (params_min_size_x != null)
				adMinSizeXText.setText(String.valueOf(params_min_size_x));
			
			adMinSizeYText.setText("");
			if (params_min_size_y != null)
				adMinSizeYText.setText(String.valueOf(params_min_size_y));
		}

		private void resetValues() {
			params_size_x = null;
			params_size_y = null;
			params_min_size_x = null;
			params_min_size_y = null;

			MASTAdView adView = (MASTAdView) CustomConfig.this
					.findViewById(R.id.adView);

			RelativeLayout.LayoutParams layoutParams = (LayoutParams) adView.getLayoutParams();
			
			layoutParams.width = LayoutParams.MATCH_PARENT;
			layoutParams.height = MASTAdView.dpToPx(100);
			layoutParams.topMargin = 0;
			layoutParams.leftMargin = 0;
			layoutParams.rightMargin = 0;
			layoutParams.bottomMargin = 0;
			adView.setLayoutParams(layoutParams);
			
			adView.getAdRequestParameters().clear();
		}

		private void setViewLayout() {
			MASTAdView adView = (MASTAdView) CustomConfig.this
					.findViewById(R.id.adView);

			RelativeLayout.LayoutParams layoutParams = (LayoutParams) adView.getLayoutParams();
			
			EditText marginTopText = (EditText) findViewById(R.id.margin_top);
			EditText marginLeftText = (EditText) findViewById(R.id.margin_left);
			EditText marginRightText = (EditText) findViewById(R.id.margin_right);
			EditText marginBottomText = (EditText) findViewById(R.id.margin_bottom);
			EditText sizeWidthText = (EditText) findViewById(R.id.size_width);
			EditText sizeHeightText = (EditText) findViewById(R.id.size_height);
			
			layoutParams.topMargin = MASTAdView.dpToPx(Integer.parseInt(marginTopText.getText().toString()));
			layoutParams.leftMargin = MASTAdView.dpToPx(Integer.parseInt(marginLeftText.getText().toString()));
			layoutParams.rightMargin = MASTAdView.dpToPx(Integer.parseInt(marginRightText.getText().toString()));
			layoutParams.bottomMargin = MASTAdView.dpToPx(Integer.parseInt(marginBottomText.getText().toString()));

			int value = Integer.parseInt(sizeWidthText.getText().toString());
			if (value != LayoutParams.MATCH_PARENT)
				value = MASTAdView.dpToPx(value);
			layoutParams.width = value;
			
			value = Integer.parseInt(sizeHeightText.getText().toString());
			if (value != LayoutParams.MATCH_PARENT)
				value = MASTAdView.dpToPx(value);
			layoutParams.height = value;

			adView.setLayoutParams(layoutParams);
		}

		private void setAdParams() {

			EditText adSizeXText = (EditText) findViewById(R.id.ad_size_x);
			EditText adSizeYText = (EditText) findViewById(R.id.ad_size_y);
			EditText adMinSizeXText = (EditText) findViewById(R.id.ad_min_size_x);
			EditText adMinSizeYText = (EditText) findViewById(R.id.ad_min_size_y);
			
			params_size_x = null;
			String value = adSizeXText.getText().toString();
			if (TextUtils.isEmpty(value) == false)
				params_size_x = Integer.valueOf(value);
			
			params_size_y = null;
			value = adSizeYText.getText().toString();
			if (TextUtils.isEmpty(value) == false)
				params_size_y = Integer.valueOf(value);
			
			params_min_size_x = null;
			value = adMinSizeXText.getText().toString();
			if (TextUtils.isEmpty(value) == false)
				params_min_size_x = Integer.valueOf(value);
			
			params_min_size_y = null;
			value = adMinSizeYText.getText().toString();
			if (TextUtils.isEmpty(value) == false)
				params_min_size_y = Integer.valueOf(value);
			
			MASTAdView adView = (MASTAdView) CustomConfig.this
					.findViewById(R.id.adView);
			
			adView.getAdRequestParameters().clear();
			
			if (params_size_x != null)
				adView.getAdRequestParameters().put("size_x", params_size_x.toString());
			
			if (params_size_y != null)
				adView.getAdRequestParameters().put("size_y", params_size_y.toString());
			
			if (params_min_size_x != null)
				adView.getAdRequestParameters().put("min_size_x", params_min_size_x.toString());
			
			if (params_min_size_y != null)
				adView.getAdRequestParameters().put("min_size_y", params_min_size_y.toString());
		}

		private void refreshAd() {
			MASTAdView adView = (MASTAdView) CustomConfig.this
					.findViewById(R.id.adView);
			adView.update(true);
		}
	}
}
