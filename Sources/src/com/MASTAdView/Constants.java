package com.MASTAdView;

import android.graphics.Color;

public class Constants {

	public static final String SDK_VERSION = "2.12pre1";
	public static final int DEFAULT_COLOR = 0xFFFFFFFF;
	
	public static final int AD_RELOAD_PERIOD = 120000; //in milliseconds
	public static final int AD_AUTO_DETECT_PERIOD = 1000; //in milliseconds
	public static final int AD_RELOAD_SHORT_PERIOD = 100; //in milliseconds
	public static final int DEFAULT_REQUEST_TIMEOUT = 20000; //in seconds
	
	public static final String PREFS_FILE_NAME = "AdserverViewPrefs";
	public static final String PREF_IS_FIRST_APP_LAUNCH = "isFirstAppLaunch";
	public static final String FIRST_APP_LAUNCH_URL = "http://www.moceanmobile.com/appconversion.php";

	//public static final String STR_RESPONSE_ERROR = "AdserverViewPrefs";
	
	public static final String STR_IMPRESSION_NOT_SEND  = "Impression is not sent";	
	public static final String STR_ADVETTISER_ID_INVALID =  "advertiserId=%s (valid: int>0)";
	public static final String STR_INVALID_PARAM = "invalid param";
	public static final String STR_EMPTY_SERVER_RESPONS = "empty server response (no ads)";
	
	public static final String STR_ORMMA_ERROR_RESIZE = "Error: resize: Cannot resize an ad that is not in the default state.";
	public static final String STR_ORMMA_ERROR_HIDE = "Error: hide: Cannot hide an ad that is not in the default state.";
	public static final String STR_ORMMA_ERROR_EXPAND = "Error: expand: Cannot expand an ad that is not in the default state.";
	public static final String STR_ORMMA_ERROR_OPEN_MAP = "Error: no Google Api or error in parameters";
	
}
