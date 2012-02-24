package com.adserver.adview.ormma;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.HeterogeneousExpandableList;
import android.widget.RelativeLayout;

import com.adserver.adview.MASTAdServerViewCore;
import com.adserver.adview.ormma.OrmmaController.Dimensions;
import com.adserver.adview.ormma.OrmmaController.PlayerProperties;
import com.adserver.adview.ormma.util.OrmmaPlayer;
import com.adserver.adview.ormma.util.OrmmaPlayerListener;
import com.adserver.adview.ormma.util.OrmmaUtils;
import com.adserver.adview.MASTAdServerViewCore.ACTION;

public class OrmmaActionHandler extends Activity {

	private HashMap<ACTION, Object> actionData = new HashMap<ACTION, Object>();
	private RelativeLayout layout;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle data = getIntent().getExtras();
		
		layout = new RelativeLayout(getApplicationContext());
		layout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(layout);
		
		doAction(data);
		
	}

	/**
	 * Perform action - Play audio/video
	 * @param data - Action data
	 */
	private void doAction(Bundle data) {

		String actionData = data.getString(MASTAdServerViewCore.ACTION_KEY);
				
		if(actionData == null)
			return;
		
		MASTAdServerViewCore.ACTION actionType = MASTAdServerViewCore.ACTION.valueOf(actionData); 
		
		switch (actionType) {
		case PLAY_AUDIO: {
			OrmmaPlayer player = initPlayer(data,actionType);			
			player.playAudio();
		}
			break;
		case PLAY_VIDEO: {
			OrmmaPlayer player = initPlayer(data,actionType);
			player.playVideo();
		}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Create and initialize player
	 * @param playData - Play data
	 * @param actionType - type of action
	 * @return
	 */
	OrmmaPlayer initPlayer(Bundle playData,ACTION actionType){				

		PlayerProperties properties = (PlayerProperties) playData.getParcelable(MASTAdServerViewCore.PLAYER_PROPERTIES);

		Dimensions playDimensions = (Dimensions)playData.getParcelable(MASTAdServerViewCore.DIMENSIONS);		
				
		OrmmaPlayer player = new OrmmaPlayer(getApplicationContext());
		player.setPlayData(properties,OrmmaUtils.getData(MASTAdServerViewCore.EXPAND_URL, playData));
		
		RelativeLayout.LayoutParams lp;
		if(playDimensions == null) {
			lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);				
		}
		else {
			// Play video in dimensions given
			lp = new RelativeLayout.LayoutParams(playDimensions.width, playDimensions.height);
			lp.topMargin = playDimensions.y;
			lp.leftMargin = playDimensions.x;		

		}
		player.setLayoutParams(lp);
		layout.addView(player);
		
		this.actionData.put(actionType, player);
		setPlayerListener(player);
		
		return player;
	}
	
	/**
	 * Set listener
	 * @param player - player instance
	 */
	private void setPlayerListener(OrmmaPlayer player){
		player.setListener(new OrmmaPlayerListener() {
			
			@Override
			public void onPrepared() {
				
				
			}
			
			@Override
			public void onError() {				
				finish();
			}
			
			@Override
			public void onComplete() {
				finish();
			}
		});
	}

	@Override
	protected void onStop() {
		
		for(Map.Entry<ACTION, Object> entry: actionData.entrySet()){
			switch(entry.getKey()){
			case PLAY_AUDIO : 
			case PLAY_VIDEO : {
				OrmmaPlayer player = (OrmmaPlayer)entry.getValue();
				player.releasePlayer();
			}			
			break;
			default : break;
		}	
	}
		super.onStop();
	}	
}
