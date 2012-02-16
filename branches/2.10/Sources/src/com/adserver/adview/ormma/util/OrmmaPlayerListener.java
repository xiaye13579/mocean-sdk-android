package com.adserver.adview.ormma.util;

/**
 * 
 * Interface class to receive call backs from Player
 *
 */
public interface OrmmaPlayerListener {	
	
	/**
	 * On completion
	 */
	public void onComplete();
	
	/**
	 * On loading complete
	 */
	public void onPrepared();
	
	/**
	 * On Error
	 */
	public void onError();
}

