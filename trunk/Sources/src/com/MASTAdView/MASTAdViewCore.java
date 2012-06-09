package com.MASTAdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.MASTAdView.ormma.OrmmaAssetController;
import com.MASTAdView.ormma.OrmmaDisplayController;
import com.MASTAdView.ormma.OrmmaLocationController;
import com.MASTAdView.ormma.OrmmaNetworkController;
import com.MASTAdView.ormma.OrmmaSensorController;
import com.MASTAdView.ormma.OrmmaUtilityController;
import com.MASTAdView.ormma.OrmmaController.Dimensions;
import com.MASTAdView.ormma.OrmmaController.PlayerProperties;
import com.MASTAdView.ormma.OrmmaController.Properties;
import com.MASTAdView.ormma.listeners.LocListener;
import com.MASTAdView.ormma.util.OrmmaPlayer;
import com.MASTAdView.ormma.util.OrmmaUtils;

/**
 * Base ad view class, extending Android web view. Must be derived & extended (see MASTAdView).
 */
public abstract class MASTAdViewCore extends WebView
{
	
	/*static final int ID_CLOSE = 1;
	
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_IMAGES = 2;
	public static final int TYPE_RICHMEDIA = 4;*/
	
	/**
	 * Premium type: premium and non-premium.
	 */
	public static final int PREMIUM_STATUS_BOTH = 2;
	
	/**
	 * Premium type: premium only.
	 */
	public static final int PREMIUM_STATUS_PREMIUM = 1;
	
	/**
	 * Premium type: non-premium.
	 */
	public static final int PREMIUM_STATUS_NON_PREMIUM = 0;
	
	/**
	 * Make server requests no matter if ad viewer is visible to user or not. Default case.
	 */
	public static final int VISIBLE_MODE_CASE1 = 1; 
	
	/**
	 * Make server requests and refresh enable only if ad viewer is visible to user.
	 */
	public static final int VISIBLE_MODE_CASE2 = 2; 
	
	/**
	 * Make server requests no matter if ad viewer is visible to user or not. However refresh is disabled to the ads that are not visible.
	 */
	public static final int VISIBLE_MODE_CASE3 = 3; 
	
	//private static final long AD_RELOAD_PERIOD = 120000; //in milliseconds
	//private static final long AD_STOP_CHECK_PERIOD = 10000; //in milliseconds
	//private static final long AD_RELOAD_SHORT_PERIOD = 100; //in milliseconds
	
	private Integer defaultImageResource;
	protected AdserverRequest adserverRequest;
	private MASTOnAdClickListener adClickListener;
	private MASTOnAdDownload adDownload;
	private MASTOnOrmmaListener ormmaListener;
	private MASTOnThirdPartyRequest onThirdPartyRequest;
	private MASTOnActivityHandler onActivityHandler;
	private Long adReloadPeriod;
	private Integer visibleMode;
	
	protected Context _context;
	private LocationManager locationManager;
	private LocListener listener;
	
	private static final int MESSAGE_RESIZE = 1000;
	private static final int MESSAGE_CLOSE = 1001;
	private static final int MESSAGE_HIDE = 1002;
	private static final int MESSAGE_SHOW = 1003;
	private static final int MESSAGE_EXPAND = 1004;
	private static final int MESSAGE_ANIMATE = 1005;
	private static final int MESSAGE_OPEN = 1006;
	private static final int MESSAGE_PLAY_VIDEO = 1007;
	private static final int MESSAGE_PLAY_AUDIO = 1008;
	private static final int MESSAGE_RAISE_ERROR = 1009;

	// layout constants
	protected static final int BACKGROUND_ID = 101;
	protected static final int PLACEHOLDER_ID = 100;
	private static final int ORMMA_ID = 102;
	
	private static final String EXPAND_DIMENSIONS = "expand_initial_dimensions";
	private static final String EXPAND_PROPERTIES = "expand_properties";
	private static final String RESIZE_WIDTH = "resize_width";
	private static final String RESIZE_HEIGHT = "resize_height";
	private static final String ERROR_MESSAGE = "message";
	private static final String ERROR_ACTION = "action";
	private static final String DIMENSIONS = "expand_dimensions";
	private static final String ACTION_KEY = "action";
	private static final String PLAYER_PROPERTIES = "player_properties";
	private static final String EXPAND_URL = "expand_url";
	private enum ACTION {PLAY_AUDIO, PLAY_VIDEO}
	private enum ViewState {DEFAULT, RESIZED, EXPANDED, HIDDEN}
	private OrmmaAssetController mAssetController;
	private OrmmaDisplayController mDisplayController;
	private OrmmaUtilityController mUtilityController;
	private OrmmaLocationController mLocationController;
	private OrmmaNetworkController mNetworkController;
	private OrmmaSensorController mSensorController;
	private ViewState mViewState = ViewState.DEFAULT;
	private MASTAdViewCore mParentAd = null;
	private static ViewGroup mExpandedFrame;
	private String mDataToInject = null;
	private static String mScriptPath = null;
	private String mContent;
	private HashSet<String> excampaigns = new HashSet<String>();
	private Button buttonClose;
	protected boolean isInterstitial = false;
	protected boolean isShowPreviousAdOnError = true;
	protected boolean isAutoCollapse = true;
	private static ViewGroup mediaPlayerFrame;
	private boolean isShowMediaPlayerFrame = false;
	private Integer locationMinWaitMillis = Constants.DEFAULT_LOCATION_REPEAT_WAIT;   // 5 minutes
	private Float locationMinMoveMeters = Constants.DEFAULT_LOCATION_REPEAT_DISTANCE; // 1000 meters
	
	protected MASTAdLog adLog = new MASTAdLog(this);
	protected Dialog dialog;
	private static OrmmaPlayer player;
	private WebView view;

	//protected boolean isFirstTime;
	protected ReloadTask reloadTask;
	//private ContentThread contentThread;
	private OpenUrlThread openUrlThread;
	private Timer reloadTimer;
	private boolean IsManualUpdate = false;
	
	private String lastRequest;
	private String lastResponse;

	private boolean internalBrowser = false;
	private boolean isExpanded = false;
	private MASTAdViewCore expandParent;
	private int lastX;
	private int lastY;
	private ViewGroup parentView = null;

	// Items needs to handle closing expanded/resized view
	private int mOldHeight;
	private int mOldWidth;
	private Drawable mOldExpandBackground;
	private int mOldExpandBackgroundColor;
	private DisplayMetrics metrics;
	
	
	/**
	 * Provide access to the diagnostic log object created internal to this view
	 * 
	 * @return MASTAdLog usable for diagnostics debug logging
	 */
	public MASTAdLog getLog()
	{
		return adLog;
	}
		
	
	/**
	 * Create view for ad for a specific site/zone combination. 
	 * 
	 * @param context - The reference to the context of Activity.
	 * @param site - The id of the publisher site.
	 * @param zone - The id of the zone of publisher site.
	 */
	public MASTAdViewCore(Context context, Integer site, Integer zone) {
		super(context);
		AutoDetectParameters(context);
		//isFirstTime =true;
		loadContent(context, 
				null, null, null, null, 
				null,   
				site, zone, null, null,
				null, null, null, null, null,
				null, null, null, null, null);
	}
	
	/**
	 * Creation of viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	protected MASTAdViewCore(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		AutoDetectParameters(context);
		initialize(context, attrs);
	}

	/**
	 * Creation of viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 */
	protected MASTAdViewCore(Context context, AttributeSet attrs) {
		super(context, attrs);
		AutoDetectParameters(context);
		initialize(context, attrs);
	}

	/**
	 * Creation of viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 */
	protected MASTAdViewCore(Context context) {
		super(context);
		AutoDetectParameters(context);
		initialize(context, null);
	}
	
	/**
	 * Creation of viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param expanded
	 * @param expandParent
	 */
	protected MASTAdViewCore(Context context, boolean expanded, MASTAdViewCore expandParent) {
		super(context);
		isExpanded =expanded;
		this.expandParent = expandParent;
		AutoDetectParameters(context);
		initialize(context, null);
		mViewState = ViewState.EXPANDED;
	}
	
	/**
	 * Set timeout for ad request calls to the ad server. Default 3000.
	 * @param timeout Timeout value (in milliseconds, from 1000 to 3000)
	 */
	public void setAd_Call_Timeout(int timeout)
	{
		if((timeout>=1000)&&(timeout<=3000))
		adserverRequest.timeout = timeout;
	}
	
	/**
	 * Get ad request timeout value.
	 * @return Current timeout for requests to ad server in milliseconds.
	 */
	public int getAd_Call_Timeout()
	{
		return adserverRequest.timeout;
	}
	
	/**
	 * Set auto-collapse property
	 * @param value True if ad view should automatically close once the data is loaded.
	 */
	public void setAutoCollapse(boolean value)
	{
		isAutoCollapse = value;
	}
	
	/**
	 * Get the auto-collapse property value
	 * @return True if ad should auto-collapse.
	 */
	public boolean getAutoCollapse()
	{
		return isAutoCollapse;
	}
	
	/**
	 * Set the show previous ad on error property.
	 * @param value True if previous ad content should be displayed if loading a new ad encounters an error.
	 */
	public void SetShowPreviousAdOnError(boolean value)
	{
		isShowPreviousAdOnError = value;
	}
	
	/**
	 * Get show previous ad on error property value.
	 * @return True if previous ad should be shown when an error occurs loading a new ad.
	 */
	public boolean GetShowPreviousAdOnError()
	{
		return isShowPreviousAdOnError;
	}
	
	/**
	 * Override webview method to set layout parameters so that local copy of widt/height can be saved.
	 */
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		lastX = params.width;
		lastY = params.height;
		super.setLayoutParams(params);
	}
	
	/**
	 * Is the ad view showing an interstitial view?
	 * @return True if interstitial (or expanded) view is showing.
	 */
	public boolean isInterstitial()
	{
		return isExpanded || isInterstitial;
	}
	
	protected void AutoDetectParameters(Context context)
	{
		_context = context;
		if(adserverRequest==null) adserverRequest = new AdserverRequest(adLog, context);
		
		// Save original screen dimensions for later use
		WindowManager windowManager = (WindowManager) ((Activity)context).getSystemService(Context.WINDOW_SERVICE);
		metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
	}
	
	/**
	 * Get on third party ad request handler.
	 * @return Object implmenting custom on third party request interface.
	 */
	public MASTOnThirdPartyRequest getOnThirdPartyRequest() {
		return onThirdPartyRequest;
	}
	
	/**
	 * Set custom on third party ad request handler.
	 * @param onThirdPartyRequest Object implementing custom on third party request interface. 
	 */
	public void setOnThirdPartyRequest(MASTOnThirdPartyRequest onThirdPartyRequest) {
		this.onThirdPartyRequest = onThirdPartyRequest;
	}

	/**
	 * Interface for third party ad requests, with an event() method invoked when
	 * third party ad requests are being handled. The originating ad view and
	 * associated parameter hashmap are passed to the event handler method. The
	 * parameters will include name/value pairs for the "type", "campaign" and
	 * "track_url" standard items, plus any additional data received in the
	 * ad response.
	 */
	public interface MASTOnThirdPartyRequest {
		public void event(MASTAdView sender, HashMap<String,String> params);
	}

	/**
	 * Get custom on activity handler, if any.
	 * @return Object implementing the MASTOnActivityHandler interface, or null if none.
	 */
	public MASTOnActivityHandler getOnActivityHandler() {
		return onActivityHandler;
	}

	/**
	 * Set custom handler for ad view activity.
	 * @param onActivityHandler Object implementing custom on activity handler interface.
	 */
	public void setOnActivityHandler(MASTOnActivityHandler onActivityHandler) {
		this.onActivityHandler = onActivityHandler;
	}

	/**
	 * Interface which supports custom callbacks to be invoked with ad view is attached to
	 * or detached from an activity (window.) The ad view from which the event originated
	 * is passed as a parameter.
	 */
	public interface MASTOnActivityHandler {
		public void onAttachedToActivity(MASTAdView sender);
		public void onDetachedFromActivity(MASTAdView sender);
	}
	
	/**
	 * Get interface for ad view with a click() method which will be invoked when loading a URL.
	 */
	public MASTOnAdClickListener getOnAdClickListener() {
		return adClickListener;
	}
	
	/**
	 * Set interface for ad view with a click() method which will be invoked when loading a URL.
	 * @param adClickListener
	 */
	public void setOnAdClickListener(MASTOnAdClickListener adClickListener) {
		this.adClickListener = adClickListener;
	}
	
	/**
	 * The interface for ad view which will be invoked when loading a URL.
	 * @return Boolean True if ALL click behavior has been handled and the default SDK processing
	 * should be skipped, false if default SDK logic to show URL in browser should still run.
	 */
	public interface MASTOnAdClickListener {
		public boolean click(MASTAdView sender, String url);
	}
	
	/**
	 * Get any registered Ormma event listener
	 * @return Object implementing the Ormma event listener interface, if registered, or null otherwise.
	 */
	public MASTOnOrmmaListener getOnOrmmaListener() {
		return ormmaListener;
	}

	/**
	 * Interface for Ormma event listener. One method, event, is invoked when Ormma
	 * events (such as a resize, expand, close, etc.) The source ad view of the event,
	 * event name, and associated parameters are passed to the event() method.
	 *
	 */
	public interface MASTOnOrmmaListener {
		public void event(MASTAdView sender, String name, String params);
	}
	
	/**
	 * Setup a listener to be notified when Ormma events occur, as described in the
	 * Ormma event listener interface.
	 * @param ormmaListener Object implementing the Ormma event listener interface
	 */
	public void setOnOrmmaListener(MASTOnOrmmaListener ormmaListener) {
		this.ormmaListener = ormmaListener;
	}

	/**
	 * The interface for ad download events. During normal processing begin() and end()
	 * will be invoked. If an error occurs, the error() method will be invoked. All methods are
	 * passed the ad view from which the events originated, and for the error case a string error
	 * message is provided. For example, if the server does not return an ad, the error method will
	 * be invoked and the error string will contain the message defined in Constants.STR_EMPTY_SERVER_RESPONS.
	 */
	public interface MASTOnAdDownload {
		/**
		 * This event is fired before banner download begins. 
		 */
		public void begin(MASTAdView sender);
		/**
		 * This event is fired after banner content fully downloaded. 
		 */
		public void end(MASTAdView sender);
		/**
		 * This event is fired after fail to download content. 
		 */
		public void error(MASTAdView sender, String error);
	}
	
	/**
	 * Get object for handling ad download events.
	 */
	public MASTOnAdDownload getOnAdDownload() {
		return adDownload;
	}

	/**
	 * Set handler for ad download events.
	 * @param adDownload Object implementing custom ad download interface.
	 */
	public void setOnAdDownload(MASTOnAdDownload adDownload) {
		this.adDownload = adDownload;
	}

	/**
	 * Optional.
	 * Get Custom Parameters, to be passed to back-end server to help with ad selection. No default.
	 * @param customParameters Hashtable of custom parameter key/values.
	 */
	public Hashtable<String, String> getCustomParameters() {
		if(adserverRequest != null) {
			return adserverRequest.getCustomParameters();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Custom Parameters.
	 * @param customParameters Hashtable of custom parameter key/values.
	 */
	public void setCustomParameters(Hashtable<String, String> customParameters) {
		if(adserverRequest != null) {
			adserverRequest.setCustomParameters(customParameters);
		}
	}
	
	/**
	 * Optional.
	 * If set to true, the ad server will send a client side impression tracking pixel with each ad, 
	 * regardless of if the campaign has this property set or not.
	 * Impressions will not be counting if this pixel does not render on the device.
	 * @param value
	 */
	public void setTrack(Boolean value) {
		if(adserverRequest != null) {
			adserverRequest.setTrack(value);
		}
	}
	
	/**
	 * Get track setting. 
	 * @return Boolean setting, if true tracking impressions are being sent to back-end.
	 */
	public Boolean getTrack() {
		if(adserverRequest != null) {
			return adserverRequest.getTrack() == null? null : adserverRequest.getTrack()==1;
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Get image resource identifier which will be shown during ad loading if there is no ad content in cache.
	 */
	public Integer getDefaultImage() {
		return defaultImageResource==null ? 0 : defaultImageResource;
	}
	
	/**
	 * Optional.
	 * Set image resource which will be shown during ad loading if there is no ad content in cache.
	 * @param defaultImage Resource identifier for default image.
	 */
	public void setDefaultImage(Integer defaultImage) {
		defaultImageResource = defaultImage;
	}

	/**
	 * Get the last request string sent to the back-end to retrieve an ad. 
	 * @return Last string sent to ad server.
	 */
	public String GetLastRequest()
	{
		return lastRequest;
	}
	
	/**
	 * Get the last response string received from the back-end when retrieving an ad.
	 * @return Last response received from ad server.
	 */
	public String GetLastResponse()
	{
		return lastResponse;
	}
	
	/**
	 * Optional.
	 * Get banner refresh interval (in seconds).
	 */
	public Integer getUpdateTime() {
		if(adReloadPeriod != null) {
			return new Long(adReloadPeriod/1000).intValue();
		} else {
			return new Long(Constants.AD_RELOAD_PERIOD/1000).intValue();
		}
	}

	/**
	 * Set banner refresh interval (in seconds). Once an ad has finished loading, the timer starts
	 * and a new ad will be loaded after this amount of time has elapsed. Default 120 seconds.
	 * If 0, ads are not updated automatically (use the update() method for a manual update.)
	 */
	public void setUpdateTime(Integer updateTime) {
		if(updateTime != null) {
			adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_INFO, "setUpdateTime", String.valueOf(updateTime));
			this.adReloadPeriod = new Long(updateTime*1000); //in milliseconds
			update(false);
		}
	}
	
	private void initialize(Context context, AttributeSet attrs) {
		if(attrs != null) {
			//isFirstTime =true;
			Integer logLevel = getIntParameter(attrs.getAttributeValue(null, "logLevel"));
			if(logLevel!=null) setLogLevel(logLevel);
			
			Integer site = getIntParameter(attrs.getAttributeValue(null, "site"));
			Integer zone = getIntParameter(attrs.getAttributeValue(null, "zone"));
			Boolean isTestModeEnabled = getBooleanParameter(attrs.getAttributeValue(null, "test"));
			Integer premium = attrs.getAttributeIntValue(null, "premium",PREMIUM_STATUS_BOTH); 
			String keywords = attrs.getAttributeValue(null, "keywords");
			//Integer adsType = attrs.getAttributeIntValue(null, "adsType",ADS_TYPE_TEXT_AND_IMAGES);
			Integer minSizeX = getIntParameter(attrs.getAttributeValue(null, "minSizeX"));
			Integer minSizeY = getIntParameter(attrs.getAttributeValue(null, "minSizeY"));
			Integer maxSizeX = getIntParameter(attrs.getAttributeValue(null, "maxSizeX"));
			Integer maxSizeY = getIntParameter(attrs.getAttributeValue(null, "maxSizeY"));
			Integer paramBG = GetColor(attrs.getAttributeValue(null, "backgroundColor"));
			Integer type = getIntParameter(attrs.getAttributeValue(null, "type"));
			
			Boolean locationDetection= getBooleanParameter(attrs.getAttributeValue(null, "locationDetection"));
			locationMinWaitMillis = getIntParameter(attrs.getAttributeValue(null, "locationMinWaitMillis"));
			locationMinMoveMeters = getFloatParameter(attrs.getAttributeValue(null, "locationMinMoveMeters"));
			Boolean internelBr = getBooleanParameter(attrs.getAttributeValue(null, "internalBrowser")); 
		
			Integer textColor = GetColor(attrs.getAttributeValue(null, "textColor"));
			String adserverURL = attrs.getAttributeValue(null, "adserverURL");
			
			String image = attrs.getAttributeValue(null, "defaultImage");
			Integer defaultImage=null;
			if(image!=null)
				defaultImage = context.getResources().getIdentifier(image, null, context.getPackageName());
			//Integer defaultImage = getIntParameter(attrs.getAttributeValue(null, "defaultImage"));
			
			setUpdateTime(getIntParameter(attrs.getAttributeValue(null, "updateTime")));
			
			String latitude = attrs.getAttributeValue(null, "latitude");
			String longitude = attrs.getAttributeValue(null, "longitude");
			String country = attrs.getAttributeValue(null, "country");
			String region = attrs.getAttributeValue(null, "region");
			String city = attrs.getAttributeValue(null, "city");
			String area = attrs.getAttributeValue(null, "area");
			//String metro = attrs.getAttributeValue(null, "metro");
			String dma = attrs.getAttributeValue(null, "dma");
			String zip = attrs.getAttributeValue(null, "zip");
			String carrier = attrs.getAttributeValue(null, "carrier");
			
			String ua = attrs.getAttributeValue(null, "ua");
			Integer visibleMode = getIntParameter(attrs.getAttributeValue(null, "visibleMode"));
			
			String customParameters = attrs.getAttributeValue(null, "customParameters");
			Hashtable<String, String> cp= null;
			if(customParameters != null)
			{
				cp = new Hashtable<String, String>();
				String str[] = customParameters.split(",");
				for(int x=0;x<str.length/2;x++)
				{
					cp.put(str[x*2], str[x*2+1]);
				}
				
			}
			
			if(visibleMode != null) {
				this.visibleMode = visibleMode;
			}
			
			if(adserverRequest==null) adserverRequest = new AdserverRequest(adLog, context);
			if(adserverURL!=null) setAdserverURL(adserverURL);
			if(city!=null)setCity(city);
			if(area!=null)setArea(area);
			//if(metro!=null)setMetro(metro);
			if(dma!=null)setDma(dma);
			if(zip!=null)setZip(zip);
			if(locationDetection!=null) setLocationDetection(locationDetection);
			if(internelBr != null) setInternalBrowser(internelBr);
			if(type != null) setType(type);
			
			loadContent(context, 
					minSizeX, minSizeY, maxSizeX, maxSizeY, 
					defaultImage,   
					site, zone, keywords, latitude,
					longitude, ua, premium, isTestModeEnabled, country,
					region, paramBG, textColor, carrier,
					cp);
			
			
			
		}else loadContent(context, 
				null, null, null, null, 
				null,   
				null, null, null, null,
				null, null, null, null, null,
				null, null, null, null,
				null);
	}
	
	private Integer GetColor(String colorString)
	{
		if(colorString==null) return null;
		int a,r,g,b;
		
		int length = colorString.length();
		if((length>0)&&(colorString.charAt(0)=='#'))
		{
			switch(length)
			{
			case 4:	
				a=255;
				r= Integer.decode("#"+colorString.substring(1,2))*17;
				g= Integer.decode("#"+colorString.substring(2,3))*17;
				b= Integer.decode("#"+colorString.substring(3,4))*17;
				break;
			case 5:
				a= Integer.decode("#"+colorString.substring(1,2))*17;
				r= Integer.decode("#"+colorString.substring(2,3))*17;
				g= Integer.decode("#"+colorString.substring(3,4))*17;
				b= Integer.decode("#"+colorString.substring(4,5))*17;
				break;
			case 7: case 9:
				return Color.parseColor(colorString);				
			default: return Constants.DEFAULT_COLOR;	
			}
			
			return Color.argb(a, r, g, b);
		}else return Constants.DEFAULT_COLOR;		
	}
	
	private void loadContent(Context context,
			Integer minSizeX, Integer minSizeY, Integer sizeX, Integer sizeY, 
			Integer defaultImage,   
			Integer site, Integer zone, 
			String keywords, String latitude,
			String longitude, String ua, Integer premium,
			Boolean isTestModeEnabled, String country,
			String region, 
			Integer paramBG, Integer paramLINK, String carrier, 
			Hashtable<String, String> customParameters) {
		
		String logString = "SDK version = "+Constants.SDK_VERSION+
				"; DeviceModel = " + android.os.Build.MODEL+"; DeviceOsVersion = " +android.os.Build.VERSION.RELEASE+
				"; PackageName = "+context.getPackageName();
		try {
			PackageInfo pinfo;
			pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if(pinfo!=null)
			{
				logString += "; versionName="+pinfo.versionName;
			}						
			
		} catch (Exception e) {			
		}
		
		
		
		adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_INFO, "created", logString);
		//android.content.pm.PackageManager.
		
		
		if(isAutoCollapse) this.setVisibility(View.INVISIBLE);
		view = this;
		if(adserverRequest==null) adserverRequest = new AdserverRequest(adLog, context);
		//adserverRequest.InitDefaultParameters(context);
		adserverRequest.setUa(ua);
		adserverRequest.setCount(1);
		adserverRequest.setSizeRequired(0);
		setSite(site);
		setKeywords(keywords);
		setPremium(premium);
		setZone(zone);
		setTest(isTestModeEnabled);
		setCountry(country);
		setRegion(region);
		setLatitude(latitude);
		setLongitude(longitude);
		if(paramBG!=null)setBackgroundColor(paramBG);
		if(paramLINK!=null)setTextColor(paramLINK);
		setCarrier(carrier);
		setMinSizeX(minSizeX);
		setMinSizeY(minSizeY);
		setMaxSizeX(sizeX);
		setMaxSizeY(sizeY);		
		setCustomParameters(customParameters);		
		
		defaultImageResource = defaultImage;
		
		WebSettings webSettings = getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		mAssetController = new OrmmaAssetController(this, context);
		mDisplayController = new OrmmaDisplayController(this, context);
		mUtilityController = new OrmmaUtilityController(this, context);
		mLocationController = new OrmmaLocationController(this, context);
		mNetworkController = new OrmmaNetworkController(this, context);
		mSensorController = new OrmmaSensorController(this, context);
		
		addJavascriptInterface(mDisplayController, "ORMMADisplayControllerBridge");
		addJavascriptInterface(mUtilityController, "ORMMAUtilityControllerBridge");
		addJavascriptInterface(mLocationController, "ORMMALocationControllerBridge");
		addJavascriptInterface(mNetworkController, "ORMMANetworkControllerBridge");
		addJavascriptInterface(mSensorController, "ORMMASensorControllerBridge");
		addJavascriptInterface(mAssetController, "ORMMAAssetsControllerBridge");
		
		setScriptPath();
		
		setWebViewClient(new AdWebViewClient(context));
		setWebChromeClient(mWebChromeClient);
		
		
		// Pre-load header (empty body) with ormma / mraid javascrpt code
		String dataOut = setupViewport(true, null);
		super.loadDataWithBaseURL(null, dataOut, "text/html", "UTF-8", null);
		//injectJavaScriptFromFile(mScriptPath); 
		
		
		
				
		//ViewGroup.LayoutParams lp = getLayoutParams();
		//((ViewGroup.LayoutParams)getLayoutParams()). = Gravity.RIGHT;
		
		buttonClose = new Button(_context);
		
		
		if(isInterstitial())
		{
			buttonClose.setLayoutParams(new ViewGroup.LayoutParams(30,30));
			buttonClose.setBackgroundDrawable(InternelBrowser.GetSelector(_context,"b_close.png", "b_close.png", "b_close.png"));
			//if(!isExpanded) buttonClose.setVisibility(View.INVISIBLE);
		}
		else
		{
			buttonClose.setLayoutParams(new ViewGroup.LayoutParams(30,30));
			buttonClose.setBackgroundDrawable(InternelBrowser.GetSelector(_context,"b_close_s.png", "b_close_s.png", "b_close_s.png"));
		}
		
		buttonClose.setVisibility(View.INVISIBLE);
		
		buttonClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(isInterstitial() && !isExpanded)
						{
							InterstitialClose();
						}else
						{
							injectJavaScript("ormma.close();");						
						}
					}
				});
			}
		});
		
		LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		//ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		ll.setGravity(Gravity.RIGHT);
		ll.addView(buttonClose);
		
		addView(ll);
		//StartLoadContent(getContext(), this);
	}
	
	@Override
	protected void onAttachedToWindow() {
		
		if(reloadTimer==null)
		{
			reloadTimer = new Timer();
			StartTimer(getContext(), view);
		}
		
		//StartLoadContent(getContext(), this);
		
		super.onAttachedToWindow();
		
		if(onActivityHandler != null) {
			onActivityHandler.onAttachedToActivity((MASTAdView)this);
		}	
		if(getBackgroundColor()==0) invalidate();
	}
	
	private void stopTimer(boolean remove)
	{
		if(reloadTimer != null) {
			try {
				reloadTimer.cancel();
				if (remove)
				{
					reloadTimer = null;
				}
				adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "stopTimer", "timer stopped");
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "stopTimer", e.getMessage());				
			}
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		if((locationManager != null) && (listener != null)) {
			locationManager.removeUpdates(listener);
		}
		
		//removeAllViews();
		stopTimer(true);
		
		/*if(contentThread != null) {
			try {
				contentThread.interrupt();
			} catch (Exception e) {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "onDetachedFromWindow", e.getMessage());
			}
		}*/
		ContentManager.getInstance(this).stopLoadContent(this);

		if(mNetworkController != null) {
			mNetworkController.stopAllNetworkListeners();
		}
		
		if(mDisplayController != null) {
			mDisplayController.stopAllOrientationListeners();
		}
		
		super.onDetachedFromWindow();
		
		if(onActivityHandler != null) {
			onActivityHandler.onDetachedFromActivity((MASTAdView)this);
		}							
	}

	@Override
	protected void onSizeChanged(int w, int h, final int ow, final int oh) {
		if (mViewState != ViewState.DEFAULT)
		{
			stopTimer(false); // expand/resize up, cancel refresh timer
		}
		
		
		// If view state is expanded and the containing frame is not null and the new width and/or height
		// is smaller than the previous, then this is the result of a configuration change (rotate) event;
		// in this case, the initial resize from Android will be constrained by the old screen geometry,
		// so we need to force a follow-up event to correct for it and allow the expanded view to fill
		// the screen again. Note that we can't just change the size parameters and continue along in this
		// function because Android calls this late in the process of sizing and laying out components
		// on the screen.
		if ((mViewState == ViewState.EXPANDED) && (mExpandedFrame != null)) 
		{
			if ((w <= ow) && (h <= oh))
			{
				final View adView = this;
					
				handler.post(new Runnable() {
					@Override
					public void run() {				
						ViewGroup.LayoutParams lp = getLayoutParams();
						lp.width = ViewGroup.LayoutParams.FILL_PARENT;
						lp.height = ViewGroup.LayoutParams.FILL_PARENT;
						adView.setLayoutParams(lp);
						adView.requestLayout();
						
						int nw = adView.getWidth();
						int nh = adView.getHeight();
			
						// if original (default view) width was full screen, update it to still use full
						// screen width after close now that we have rotated.
						if ((mOldWidth == metrics.widthPixels) && (mOldWidth != adView.getWidth()))
						{
							mOldWidth = adView.getWidth();
							
							WindowManager windowManager = (WindowManager) ((Activity)_context).getSystemService(Context.WINDOW_SERVICE);
							windowManager.getDefaultDisplay().getMetrics(metrics);
						}
						
						onSizeChanged(nw, nh, ow, oh);
					}
				});
				
				return;
			}
			else
			{
				// if original (default view) width was full screen, update it to still use full
				// screen width after close now that we have rotated.
				if ((mOldWidth == metrics.widthPixels) && (mOldWidth != this.getWidth()))
				{
					mOldWidth = this.getWidth();
					
					WindowManager windowManager = (WindowManager) ((Activity)_context).getSystemService(Context.WINDOW_SERVICE);
					windowManager.getDefaultDisplay().getMetrics(metrics);
				}
			}
		}
		
		String script = String.format(
				"Ormma.fireEvent(ORMMA_EVENT_SIZE_CHANGE, {dimensions : {width : %d, height: %d}});", w, h);
		injectJavaScript(script);
		super.onSizeChanged(w, h, ow, oh);
		adserverRequest.sizeX = w;
		adserverRequest.sizeY = h;
	}

	/**
	 * Override the default webview onKeyDown() method. 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mViewState == ViewState.EXPANDED)
			{
				return true;
			}
        }
        
		return super.onKeyDown(keyCode, event);
	}

	
	private void onStateChange(ViewState state)
	{
		String newState;
		switch(state)
		{
			case EXPANDED:
				newState = "expanded";
				break;
			case HIDDEN:
				newState = "hidden";
				break;
			case RESIZED:
				newState = "resized";
				break;
			default:
				newState = "default";
		}
		
		String script = String.format("Ormma.fireEvent(ORMMA_EVENT_STATE_CHANGE, \"%s\");", newState); 
		injectJavaScript(script);
	}
	
	
	private void closeRunnable(final MASTAdViewCore view)
	{
		view.handler.post(new Runnable() {
			@Override
			public void run() { 
				closeView(false); // sets view state
				onStateChange(mViewState); // manually force state change; the close below should trigger this, but it doesn't reliably happen
				view.injectJavaScript("ormma.close(); ormma.show();"); // close (all) ad views, then show default again; kind of ugly, but necessary to get back to visible default state
			}
		});
	}
	
	/**
	 * Override the default webview onKeyUp() method. 
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mViewState == ViewState.EXPANDED) {
				if (expandParent != null) {
					closeRunnable(expandParent);
				} else {
					closeRunnable(this);
				}
				
				return true;
			}
        }
		return super.onKeyUp(keyCode, event);
	}
	
	
	/**
	 * Immediately update ad view contents.
	 */
	public void update()
	{
		if(reloadTimer==null)
		{
			reloadTimer = new Timer();			
		}
		update(true);
	}
	
	private void update(boolean isManual) {
		if(isShown() || isManual) 
		{
			
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "update", "");
			if(isManual) IsManualUpdate = true;
			hideVirtualKeyboard();
			StartLoadContent(getContext(), this);
		}
	}
	
	/**
	 * Property controlling display of standard or custom close button when ad view is expanded.
	 * NOTE: even if a custom close is displayed by the ad (in any location), per the ormma standard
	 * the upper right hand corner will have a transparent close region that is still active.
	 * @param custom True if a custom button will be provided by ad, false if standard SDK button to be used.
	 */
	public void useCloseButton(final boolean custom) {
		handler.post(new Runnable() {			
			@Override
			public void run() {
				if (custom) {
					buttonClose.setVisibility(View.VISIBLE);
				} else {
					// per ormma standard, leave transparent clickable region where close button would be
					buttonClose.setBackgroundColor(Color.TRANSPARENT);
					buttonClose.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private void StartLoadContent(Context context, WebView view)
	{
		if(reloadTask!=null)
		{
			reloadTask.cancel();
			reloadTask = null;
		}
		
		if(ContentManager.getInstance(this).getAutoDetectParameters().equals(""))
		{
			IsManualUpdate = true;
			StartTimer(context, view);
			return;
		}
		
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
        boolean isRequestAd, isRefreshAd;
		
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "StartLoadContent", "");
		
		boolean isShownView = view.isShown() || IsManualUpdate;//&&isScreenOn;
		
		IsManualUpdate = false;		
		
		if((getSite()==0) || (getZone()==0))
		{
			StartTimer(context,view);
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_WARNING, "StartLoadContent", "site=0 or zone=0");
			return;
		}
		
		if(visibleMode == null) {
			visibleMode = VISIBLE_MODE_CASE2;
		}
		
    	switch (visibleMode) {
		case VISIBLE_MODE_CASE1:
			isRequestAd = true;
			isRefreshAd = true;
			break;
		case VISIBLE_MODE_CASE2:
			isRequestAd = isShownView;
			isRefreshAd = isShownView;
			break;
		case VISIBLE_MODE_CASE3:
			isRequestAd = true;
			isRefreshAd = isShownView;
			break;
		default:
			isRequestAd = true;
			isRefreshAd = true;
			break;
		}
		
		if ((defaultImageResource!=null) && (getBackground()==null))
		{
			try {
				handler.post(new SetBackgroundResourceAction(view, defaultImageResource));
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "StartLoadContent", e.getMessage());
			}
		}
		
		InterceptOnAdDownload interceptOnAdDownload = new InterceptOnAdDownload(context,view);
		
		if(isRequestAd ) {
			try {
				if(mViewState != ViewState.EXPANDED) {
					if(adserverRequest != null) {				
						if (mViewState == ViewState.RESIZED)
						{
							// Ad view is going to reload & resize to default state; we need our state to match that
							mViewState = ViewState.DEFAULT;
						}

						interceptOnAdDownload.begin((MASTAdView)this);
						adserverRequest.setExcampaigns(getExcampaignsString());
						String url = adserverRequest.createURL();
						lastRequest = url;
						RequestCounter++;
						adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "requestGet["+String.valueOf(RequestCounter)+"]" , url);
						ContentManager.getInstance(this).startLoadContent(this, adserverRequest.createURL());
					}
				}//else StartTimer(context, view);
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "StartLoadContent.requestGet", e.getMessage());
				interceptOnAdDownload.error((MASTAdView)this,e.getMessage());
			}
		}
		
	}
	
	/**
	 * Set the visibility of the ad view to one of the standard Android visibility values.
	 * @param visibility Int parameter, one of standard values: View.VISIBLE, View.INVISIBLE or View.GONE.
	 */
	public void setAdVisibility(final int visibility)
	{
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				setVisibility(visibility);
			}
		});
	}
	
	
	private String injectionHeaderCode = null;
	private String injectionBodyCode = null;
		
	
	/**
	 * Customize the "HTML" (or javascript/css) code to be inserted into the HTML HEAD when creating
	 * webview for ad content. By default this will contain the string:
	 * 
	 * <meta name=\"viewport\" content=\"target-densitydpi=device-dpi\"/>
	 * 
	 * @param value String content to be inserted, or null to use built-in default (same as 2.10)
	 */
	public void setInjectionHeaderCode(String value)
	{
		injectionHeaderCode = value;
	}
	
	
	/**
	 * Get current injection header code string.
	 * @return Current injection header value.
	 */
	public String getInjectionHeaderCode()
	{
		if (injectionHeaderCode != null)
		{
			return injectionHeaderCode;
		}
		else
		{
			// Default fragment as of 2.10 SDK
			return "<meta name=\"viewport\" content=\"target-densitydpi=device-dpi\"/>";
		}
	}
	

	/**
	 * Customize the "HTML" (or javascript/css) code to be inserted into the HTML BODY when creating
	 * webview for ad content. By default this will contain the string:
	 *  
	 * <body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\">
	 * 
	 * @param value String content to be inserted, or null to use built-in default (same as 2.10.) NOTE: This MUST include the HTML <body> tag!
	 */
	public void setInjectionBodyCode(String value)
	{
		injectionBodyCode = value;
	}
	
	
	/**
	 * Get current injection body code string.
	 * @return Current injection body value.
	 */
	public String getInjectionBodycode()
	{
		if (injectionBodyCode != null)
		{
			return injectionBodyCode;
		}
		else
		{
			// Default 2.10 SDK value
			return "<body style=\"margin: 0px; padding: 0px; width: 100%; height: 100%\">";
		}
	}
	
	
	// Create viewport for showing ad; version 2.9 and earlier had a "bug" which caused
	// ad creative to be scaled on device to the device dpi; version 2.10 introduced a fix
	// for this, but the change in behavior caused some issues. A deprecated flag allowed
	// reverting to the old behavior. Per a client suggestion, another fix is being introduced
	// which allows the app developer to customer the header and/or body code to be injected.
	private String setupViewport(boolean headerOnly, String body)
	{
		StringBuffer data = new StringBuffer("<html><head>");
		
		// Insert our javascript bridge library; this is always required
		data.append("<style>*{margin:0;padding:0}</style>");
		data.append("<script src=\"file://");
		data.append(mScriptPath);
		data.append("\" type=\"text/javascript\"></script>");
		
		data.append(getInjectionHeaderCode());
	
		if (headerOnly)
		{
			data.append("</head><body>");
		}
		else
		{
			data.append("</head>");
			data.append(getInjectionBodycode());
			
			if (body != null)
			{
				data.append(body);
			}
		}
		
		data.append("</body></html>");
		
		//System.out.println("SetupViewport: final string: " + data.toString());
		return data.toString();
	}
	
	
	protected void setResult(String data, String error)
	{
		lastResponse=data;
		
		if(error!=null)
		{		
			adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_ERROR, "requestGet result["+String.valueOf(RequestCounter)+"][ERROR]", error);
			//if(onAdEventHandler!= null)onAdEventHandler.error(this, error);
			if(adDownload!= null) adDownload.error((MASTAdView)this,error);
			StartTimer(getContext(),view);
			
			
			if((mContent!=null) && (!mContent.equals("")) && isShowPreviousAdOnError)
			{
				return;
			}
				if (defaultImageResource!=null)
				{
					try {
						handler.post(new SetBackgroundResourceAction(view, defaultImageResource));
					} catch (Exception e) {
						adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "setResult", e.getMessage());
					}
				}else
				{
					if(isAutoCollapse) this.setAdVisibility(View.INVISIBLE);
					else
					{	
						data = setupViewport(false, null);					
						//view.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
						loadWebViewContent(null, data, null);
					}
				}

			return; // end error case
		}
		
		//isFirstTime = false;
		//if(isAutoCollapse) this.setAdVisibility(View.VISIBLE);
		final Context context = getContext();
		
		adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "requestGet result["+String.valueOf(RequestCounter)+"]", data);
		try {
			if((data != null) && (data.length() > 0)) {
				String dataToLowercase = data.toLowerCase();
				if ((dataToLowercase.contains("invalid params")) || (dataToLowercase.contains("error: -1")))
				{	
					InterstitialClose();
					StartTimer(getContext(),view);
					if(adDownload!= null) adDownload.error((MASTAdView)this, "invalid params");
				}else
				{
					//if(isRefreshAd || isFirstTime) 
					{
						//handler.post(new RemoveAllChildViews(view));
						String externalCampaignData = Utils.scrapeIgnoreCase(data, "<external_campaign", "</external_campaign>");
						if((externalCampaignData != null) && (externalCampaignData.length() > 0)) {
							String type = Utils.scrapeIgnoreCase(externalCampaignData, "<type>", "</type>");
							String campaignId = Utils.scrapeIgnoreCase(externalCampaignData, "<campaign_id>", "</campaign_id>");
							String trackUrl = Utils.scrapeIgnoreCase(externalCampaignData, "<track_url>", "</track_url>");
							String externalParams = Utils.scrapeIgnoreCase(externalCampaignData, "<external_params>", "</external_params>");
							//interceptOnAdDownload.SetCampaingId(campaignId);
							
							if(onThirdPartyRequest!=null)
							{
								try
								{
									HashMap<String, String> params = new HashMap<String, String>();
									params.put("type", type);
									params.put("campaignId", campaignId);
									params.put("trackUrl", trackUrl);
									String[] args = externalParams.split("</param>");
									for(int x=0;x<args.length;x++)
									{
										String[] vals = args[x].split("\">");
										String val = vals.length>1 ? vals[1] : "";
										String arg = vals[0].split("\"")[1];
										params.put(arg, val);
									}
									
									onThirdPartyRequest.event((MASTAdView) this, params);
								}catch (Exception e) {
									adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "onThirdPartyRequest", e.getMessage());										
								}
									StartTimer(context, view);
							} else RestartExcampaings(campaignId,context,view);							
						} else
						 {
							handler.post(new RemoveAllChildViews(view));
							String videoData="";// = Utils.scrapeIgnoreCase(data, "<video", "/>");
							
							if((videoData != null) && (videoData.length() > 0)) {
								String videoUrl = Utils.scrapeIgnoreCase(videoData, "src=\"", "\"");
								String clickUrl = Utils.scrapeIgnoreCase(data, "href=\"", "\"");
								handler.post(new SetupVideoAction(context, view, videoUrl, clickUrl));
								// StartTimer(context,view);
								stopTimer(false);
							} else {
								String dataOut="";
								dataOut = setupViewport(false, data);
								
								mContent = dataOut;
								
								// Moved this into runnable posted to UI thread to eliminate webview warning
								// logged with each invocation with Android 4.0.
								//view.loadDataWithBaseURL(null, dataOut, "text/html", "UTF-8", null);
								
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										view.loadDataWithBaseURL(null, mContent, "text/html", "UTF-8", null);
										getLayoutParams().width = lastX;
										getLayoutParams().height = lastY;
										requestLayout();
									}
								});
								
								StartTimer(context,view);
							}							
						}
					}
				}
			}else
			{
				//adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_WARNING, "contentThreadAction", "data = null isShownView="+Boolean.toString(isShownView));
				
				//if(isShownView)
				{
					InterstitialClose();
					if(adDownload!= null) adDownload.error((MASTAdView)this,Constants.STR_EMPTY_SERVER_RESPONS);
				}
				StartTimer(context,view);
			}
		} catch (Exception e) {
			adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "StartLoadContent", e.getMessage());
			StartTimer(context,view);
		}
	}
	
	private void StartTimer(Context context, WebView view)
	{
		{	
			try
			{
				if(reloadTimer== null) return;
				
				if(reloadTask!=null)
				{
					reloadTask.cancel();
					reloadTask = null;
				}
				
				ReloadTask newReloadTask = new ReloadTask(context, view);
				
				if(IsManualUpdate)
				{
					if(ContentManager.getInstance(this).getAutoDetectParameters().equals(""))
					{
						adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "AutoDetectParameters, StartTimer", String.valueOf(Constants.AD_AUTO_DETECT_PERIOD/1000));
						reloadTimer.schedule(newReloadTask, Constants.AD_AUTO_DETECT_PERIOD);
						reloadTask = newReloadTask;
						return;
					}
					adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "Manual Update, StartTimer", String.valueOf(Constants.AD_RELOAD_SHORT_PERIOD/1000));
					reloadTimer.schedule(newReloadTask, Constants.AD_RELOAD_SHORT_PERIOD);
					reloadTask = newReloadTask;
					return;
				}
				
				if((adReloadPeriod != null) && (adReloadPeriod >= 0)) {
					if(adReloadPeriod > 0) {
						adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "StartTimer", String.valueOf(adReloadPeriod/1000));					
						reloadTimer.schedule(newReloadTask, adReloadPeriod);
					} else {
						adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "StartTimer", "stopped");
					}
				} else {
					reloadTimer.schedule(newReloadTask, Constants.AD_RELOAD_PERIOD);
					adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "StartTimer", String.valueOf(Constants.AD_RELOAD_PERIOD/1000)+" default");
				}
				
				reloadTask = newReloadTask; 
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_3,MASTAdLog.LOG_TYPE_ERROR,"StartTimer",e.getMessage());
			}
		}
	}
		
	protected void InterstitialClose()
	{
		
	}
	
	private void RestartExcampaings(String campaignId,Context context, WebView view)
	{
		adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_WARNING, "RestartExcampaings", campaignId);
		if (excampaigns.contains(campaignId))
			StartTimer(context, view);
		else
		{
			excampaigns.add(campaignId);
			update(false);
		}
	}
	
	private void loadWebViewContent(final String baseUrl, final String data, final String historyUrl)
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				view.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", historyUrl);	
			}
		});
	}
	
	
	private class InterceptOnAdDownload implements MASTOnAdDownload
	{
		private Context context;
		private WebView view;
		String campaignId = null;
		int childCount;
		
		public InterceptOnAdDownload(Context context, WebView view) {
			this.context = context;
			this.view = view;
			this.childCount = view.getChildCount();					
		}
		
		public void SetCampaingId(String campaignId)
		{
			this.campaignId = campaignId;
		}
		
		
		@Override
		public void begin(final MASTAdView sender) {
			if(adDownload!= null) adDownload.begin(sender);
		}

		@Override
		public void end(final MASTAdView sender) {
			//view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
			loadWebViewContent(null, "", null);
			StartTimer(context, view);			
			if(adDownload!= null) adDownload.end(sender);
		}

		@Override
		public void error(final MASTAdView sender, final String error) {
			if(campaignId != null)
				RestartExcampaings(campaignId,context, view);
			else StartTimer(context, view);
			if(adDownload!= null) adDownload.error(sender,error);
		}
	}

	private String getExcampaignsString() {
		StringBuilder result = new StringBuilder();
		
		for (Iterator<String> iterator = excampaigns.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			result.append(id);
			result.append(",");
		}
		
		if(excampaigns.size() > 0) {
			result.deleteCharAt(result.length()-1);
		}
		
		return result.toString();
	}
	
	private class RemoveAllChildViews implements Runnable {
		private ViewGroup view;
		
		public RemoveAllChildViews(ViewGroup view) {
			this.view = view;
		}

		@Override
		public void run() {
			try {
				//view.removeAllViews();
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "RemoveAllChildViews", e.getMessage());
			}
		}
	}	
	
	private class SetupVideoAction implements Runnable {
		private Context context;
		private WebView view;
		private String url;
		private String clickUrl;
		
		public SetupVideoAction(Context context, WebView view, String url,
				String clickUrl) {
			this.context = context;
			this.view = view;
			this.url = url;
			this.clickUrl = clickUrl;			
		}

		@Override
		public void run() {
			try {
				if((url != null) && (url.length() > 0)) {
					VideoView videoView = new VideoView(context);
					videoView.setLayoutParams(view.getLayoutParams());
					videoView.setMediaController(new MediaController(context));
					Uri video = Uri.parse(url);
					videoView.setVideoURI(video);
					
					videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
		                public void onCompletion(MediaPlayer mp) {
	                        try {
	                        	mp.seekTo(0);
	                        	mp.start();
	                        } catch(Exception e){
	                        	adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "SetupVideoAction", e.getMessage());
	                        }
		                }
					});
					
					videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
						
						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {							
							adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "Play video",
									"what="+String.valueOf(what)+";extra="+String.valueOf(extra) );
							return true;
						}
					});
					
					if((clickUrl != null) && (clickUrl.length() > 0)) {
						videoView.setOnClickListener(new View.OnClickListener(){
							@Override
							public void onClick(View v) {
								openUrlInExternalBrowser(context, clickUrl);
							}
						});
						videoView.setOnTouchListener(new View.OnTouchListener(){
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								if(event.getAction() == MotionEvent.ACTION_DOWN) {
									openUrlInExternalBrowser(context, clickUrl);
								}
								return false;
							}
						});
					}
					
					view.addView(videoView);
					videoView.start();
					
					view.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
				}
			} catch (Exception e) {
			}
		}
	}

	private class AdWebViewClient extends WebViewClient {
		private Context context;
		
		public AdWebViewClient(Context context) {
			this.context = context;
		}

		
		// Default logic for an ad click event
		protected void defaultOnAdClickHandler(MASTAdView view, String url)
		{
			Context context = view.getContext();
			int isAccessNetworkState = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);	
	    	if (isAccessNetworkState == PackageManager.PERMISSION_GRANTED)
	    	{
				if (isInternetAvailable(context))
				{
					openUrlInExternalBrowser(context, url);
				}
				else 
				{
					Toast.makeText(context, "Internet is not available", Toast.LENGTH_LONG).show();
				}
	    	}
	    	else if (isAccessNetworkState == PackageManager.PERMISSION_DENIED)
	    	{
				openUrlInExternalBrowser(context, url);
	    	}
		}
		

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try
			{
				adLog.log(MASTAdLog.LOG_LEVEL_2,MASTAdLog.LOG_TYPE_INFO,"OverrideUrlLoading",url);
				if(adClickListener != null) {
					if (adClickListener.click((MASTAdView)view, url) == false)
					{
						// If click() method returns false, continue with default logic
						defaultOnAdClickHandler((MASTAdView)view, url);
					}
				}else {
					defaultOnAdClickHandler((MASTAdView)view, url);
				}
			}catch(Exception e){
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "shouldOverrideUrlLoading", e.getMessage());
			}
			
			return true;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			if(mDisplayController != null) {
				mDisplayController.setDefaultPosition();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			((MASTAdViewCore) view).onPageFinished();
			
			if(isAutoCollapse) setAdVisibility(View.VISIBLE);
			
			if(adDownload != null) {
				adDownload.end((MASTAdView)view);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			if(adDownload != null) {
				adDownload.error((MASTAdView)view, description);
			}
		}
	}
	
	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			// Handle alert message from javascript
			return super.onJsAlert(view, url, message, result);
		}
	};
	
	private class ReloadTask extends TimerTask {
		private Context context;
		private WebView view;
		
		public ReloadTask(Context context, WebView view) {
			this.context = context;
			this.view = view;			
		}

		@Override
		public void run() {
			StartLoadContent(context, view);
		}
	}
	
	private class SetBackgroundResourceAction implements Runnable {
		private WebView view;
		private Integer backgroundResource;
		
		public SetBackgroundResourceAction(WebView view, Integer backgroundResource) {
			this.view = view;
			this.backgroundResource = backgroundResource;
		}
		
		@Override
		public void run() {
			try {
				if(backgroundResource != null) {
					view.setBackgroundResource(backgroundResource);
					view.setBackgroundColor(0);					
				}
			} catch (Exception e) {
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "SetBackgroundResourceAction", e.getMessage());
			}
		}
	}	
	
	private class OpenUrlThread extends Thread {
		MASTAdViewCore ad;
		Context context;
		String url;
		
		public OpenUrlThread(Context context,MASTAdViewCore ad, String url) {
			this.ad = ad;
			this.context =context;
			this.url = url;
		}

		@Override
		public void run() {
			ad._openUrlInExternalBrowser(context, url);
		}
	}

	private void openUrlInExternalBrowser(Context context, String url) {
		if(url==null) return;
		if((openUrlThread==null) || (openUrlThread.getState().equals(Thread.State.TERMINATED)))
		{
			openUrlThread = new OpenUrlThread(getContext(), this,url);
			openUrlThread.start();
		} else if(openUrlThread.getState().equals(Thread.State.NEW))
			openUrlThread.start();
	}
		
	
	private void _openUrlInExternalBrowser(final Context context, final String url) {
			String lastUrl = null;
			String newUrl =  url;
			URL connectURL;
			while(!newUrl.equals(lastUrl) )
			{
				lastUrl = newUrl;
				try {					
					connectURL = new URL(newUrl);					
					HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
					newUrl = conn.getHeaderField("Location");
					if(newUrl==null)newUrl=conn.getURL().toString();
				} catch (Exception e) {
					newUrl = lastUrl;
				}				
			}
			
			if(newUrl==null) newUrl = url;
			
			Uri uri = Uri.parse(newUrl);
			if(internalBrowser && (uri.getScheme().equals("http") || uri.getScheme().equals("https")))
			{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							try
							{
								new InternelBrowser(context,url).show();
							}catch (Exception e) {
								adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "openUrlInInternalBrowser", e.getMessage());
							}
						}
					});
			}
			else
			{
				try
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUrl));
					context.startActivity(intent);
				} catch (Exception e) {
					adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "openUrlInExternalBrowser","url="+ newUrl+"; error="+e.getMessage());
				}
			}		
	}
	
	private boolean isInternetAvailable(Context context) {
    	boolean result = false;
    	ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	
		if ((networkInfo != null) && (networkInfo.isAvailable())) {
			result = true ;
		}
    	
    	return result;
    }
	
	private static void sendGetRequest(String url) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		client.execute(get);
	}
	
	static int RequestCounter = 0;
	
	private Float getFloatParameter(String stringValue)
	{
		if(stringValue != null) {
			try
			{
				return  Float.parseFloat(stringValue);
			}catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	private Integer getIntParameter(String stringValue) {
		if(stringValue != null) {
			try
			{
				return (int) Long.decode(stringValue).longValue();
			}catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	private static Boolean getBooleanParameter(String stringValue) {
		if(stringValue != null) {
			return Boolean.parseBoolean(stringValue);
		} else {
			return null;
		}
	}

	/**
	 * Inject string into webview for execution as javascript.
	 * NOTE: Handle carefully, this has security implications! 
	 * @param str Code string to be run; javascript: prefix will be prepended automatically.
	 */
	public void injectJavaScript(String str) {
		try
		{
			//System.out.println("inject javascript: " + str);
			super.loadUrl("javascript:" + str);
		}catch (Exception e) {
			//Log.e("injectJavaScript", e.getMessage()+" "+str);
			//System.out.println("injectJavaScript: " + e.getMessage()+" "+str);
		}
	}

	/*
	public void injectJavaScriptFromFile(String path)
	{
		if ((path == null) || (path.length() < 1))
		{
			return;
		}
		
		try
		{
			//System.out.println("inject javascript from path: " + path);
			//super.loadUrl("javascript:" + str);
			String dataOut = "(" + readFile(path) + ")";
			super.loadData(dataOut, "text/javascript", "ASCII"); 
		}catch (Exception e) {
			//Log.e("injectJavaScript", e.getMessage()+" "+str);
			//System.out.println("injectJavaScript: " + e.getMessage()+" "+str);
		}
	}
	*/
	
	/**
	 * Get view state name.
	 * @return One of the string: DEFAULT, RESIZED, EXPANDED, HIDDEN.
	 */
	public String getState(){
		return mViewState.toString().toLowerCase();
	}

	
	private boolean closeView(boolean inject)
	{
		buttonClose.setVisibility(View.INVISIBLE);
		ViewGroup.LayoutParams lp = getLayoutParams();
		
		switch (mViewState) {
		case RESIZED:
			ormmaEvent("close","viewState=resized");

			lp.height = mOldHeight;
			lp.width = mOldWidth;
			requestLayout();
			mViewState = ViewState.DEFAULT;
			StartTimer(getContext(), view);
			return true;
		case EXPANDED:
			if (inject)
			{
				if (expandParent != null) {
					expandParent.injectJavaScript("ormma.close();");
				} else {
					injectJavaScript("ormma.close();");
				}
			}

			if (parentView != null) {
				mExpandedFrame.removeAllViews();
				view.setBackgroundColor(mOldExpandBackgroundColor);
				view.setBackgroundDrawable(mOldExpandBackground);
				parentView.addView(view, new LinearLayout.LayoutParams(mOldWidth, mOldHeight));
				parentView = null;
			}
			
			if (mExpandedFrame != null) {
				ormmaEvent("close","viewState=expanded");
				closeExpanded(mExpandedFrame);
				mViewState = ViewState.DEFAULT;
			}
			
			lp.height = mOldHeight;
			lp.width = mOldWidth;
			
			requestLayout();
			StartTimer(getContext(), view);
			
			return true;		
		};
		
		return false;
	}
	
	
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			switch (msg.what) {
				case MESSAGE_RESIZE: {
					if (mViewState == ViewState.DEFAULT) {
						stopTimer(false); // stop ad refresh timer
						int x = lastX;
						int y = lastY;
						mViewState = ViewState.RESIZED;
						ViewGroup.LayoutParams lp = getLayoutParams();
						mOldHeight = lp.height;
						mOldWidth = lp.width;
						ormmaEvent("resize", "mOldWidth="+String.valueOf(mOldWidth)+";OldHeight="+String.valueOf(mOldWidth)+
								   ";width="+String.valueOf(lp.width)+";height="+String.valueOf(lp.height));
						
						lp.height = data.getInt(RESIZE_HEIGHT, lp.height);
						lp.width = data.getInt(RESIZE_WIDTH, lp.width);
						requestLayout();
						lastX = x;
						lastY = y;
					} else {
						ormmaEvent("error",Constants.STR_ORMMA_ERROR_RESIZE);
					}
					break;
				}				
				case MESSAGE_CLOSE: {
					closeView(false);
					break;
				}
				case MESSAGE_HIDE: {
					if (mViewState == ViewState.DEFAULT) {
						ormmaEvent("hide","");
						setVisibility(View.INVISIBLE);
					} else {
						ormmaEvent("error",Constants.STR_ORMMA_ERROR_HIDE);
					}
					break;
				}
				case MESSAGE_SHOW: {
					ormmaEvent("show","");
					setVisibility(View.VISIBLE);
					break;
				}
				case MESSAGE_EXPAND: {
					if (mViewState == ViewState.DEFAULT) {
						stopTimer(false); // stop ad refresh timer
						hideVirtualKeyboard();
						ViewGroup.LayoutParams lp = getLayoutParams();
						mOldHeight = lp.height;
						mOldWidth = lp.width;
						mOldExpandBackground = getBackground();
						mOldExpandBackgroundColor = getBackgroundColor();

						if (mOldHeight == ViewGroup.LayoutParams.WRAP_CONTENT)
						{
							// Can't restore view to minimized size with wrap content; lock current size in place.
							mOldHeight = getHeight();
						}
						
						ormmaEvent("expand","");
						mViewState = ViewState.EXPANDED;
						expandInUIThread((Dimensions) data.getParcelable(EXPAND_DIMENSIONS), data.getString(EXPAND_URL),
								(Properties) data.getParcelable(EXPAND_PROPERTIES));
					} else {
						ormmaEvent("error",Constants.STR_ORMMA_ERROR_EXPAND);
					}
					break;
				}
				case MESSAGE_PLAY_AUDIO: {
					stopTimer(false); // stop ad refresh timer
					ormmaEvent("playaudio","");
					handler.post(new SetupOrmmaAudioPlayer(data));
					break;
				}
				case MESSAGE_PLAY_VIDEO: {
					stopTimer(false); // stop ad refresh timer
					ormmaEvent("playvideo","fulscreen=false");
					handler.post(new SetupOrmmaPlayer(view, data));
					break;
				}
				case MESSAGE_RAISE_ERROR:{
					String strMsg = data.getString(ERROR_MESSAGE);
					String action = data.getString(ERROR_ACTION);
					String injection = "window.ormmaview.fireErrorEvent(\""+strMsg+"\", \""+action+"\")";
					ormmaEvent("error","msg="+strMsg+";action="+action);
					injectJavaScript(injection);
					break;
				}
			}
			
			super.handleMessage(msg);
		}
	};
	
	/**
	 * Signal an condition, and invoke handler code to run on the main UI thread.
	 * @param strMsg Error message
	 * @param action Action string
	 */
	public void raiseError(String strMsg, String action){
		
		Message msg = handler.obtainMessage(MESSAGE_RAISE_ERROR); // mHandler

		Bundle data = new Bundle();
		data.putString(ERROR_MESSAGE, strMsg);
		data.putString(ERROR_ACTION, action);
		msg.setData(data);
		handler.sendMessage(msg); // mHandler
	}

	/**
	 * Signal a resize event for the ad view, and invoke handler code to run on the main UI thread.
	 * @param width View width in pixels. 
	 * @param height View height in pixels.
	 */
	public void resize(int width, int height) {
		Message msg = handler.obtainMessage(MESSAGE_RESIZE); // mHandler

		Bundle data = new Bundle();
		data.putInt(RESIZE_WIDTH, width);
		data.putInt(RESIZE_HEIGHT, height);
		msg.setData(data);

		handler.sendMessage(msg); // mHandler
	}
	
	private boolean ormaEnabled = false;
	
	/**
	 * Signal an ormma event to the ormma listener.
	 * @param name String event name
	 * @param params String event parameters
	 */
	public void ormmaEvent(String name, String params)
	{
		//System.out.println("ormma event: " + name + ": " + params);
		if(ormmaListener!=null)
		{	
			if(!ormaEnabled) ormmaListener.event((MASTAdView)this, "ormmaenabled", "");
			ormaEnabled = true;
			if(params!=null) params = params.replace(";", "&");
			ormmaListener.event((MASTAdView)this, name, params); 
		}
	}
	
	/**
	 * Send a close message to the ad view message handler, which will invoke the close logic
	 * on the main UI thread, including injecting an Ormma close event if appropriate.
	 */
	public void close() {
		handler.sendEmptyMessage(MESSAGE_CLOSE); // mHandler		
	}

	/**
	 * Send a hide message to the ad view message handler, which will invoke the hide logic
	 * on the main UI thread, including injecting an Ormma hide event if appropriate.
	 */
	public void hide() { 
		handler.sendEmptyMessage(MESSAGE_HIDE); // mHandler
		if(isInterstitial() && !isExpanded) InterstitialClose();
	}

	/**
	 * Send a show message to the ad view message handler, which will invoke the show logic
	 * on the main UI thread, including injecting an Ormma show event if appropriate.
	 */
	public void showAdView() {
		handler.sendEmptyMessage(MESSAGE_SHOW); // mHandler
	}

	/**
	 * Send an expand message to the ad view message handler, which will invoke the expand logic
	 * on the main UI thread, including injecting an Ormma expand event if appropriate.
	 * @param dimensions Dimensions to which the view should expand
	 * @param URL URL for content to show in expanded view, if not included in ad
	 * @param properties Ormma expand properties
	 */
	public void expand(Dimensions dimensions, String URL, Properties properties) {
		Message msg = handler.obtainMessage(MESSAGE_EXPAND); // mHandler
		Bundle data = new Bundle();
		data.putParcelable(EXPAND_DIMENSIONS, dimensions);
		data.putString(EXPAND_URL, URL);
		data.putParcelable(EXPAND_PROPERTIES, properties);
		msg.setData(data);
		handler.sendMessage(msg); // mHandler
	}

	private void hideVirtualKeyboard()
	{

		// Try to hide virtual keyboard
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(this.getApplicationWindowToken(), 0);
	}
	
	protected void closeExpanded(View expandedFrame) {
		((ViewGroup)((Activity) getContext()).getWindow().getDecorView()).removeView(expandedFrame);
		requestLayout();
	}
	
	private void expandInUIThread(Dimensions dimensions, String URL, Properties properties) {
		/*boolean dontLoad = false;
		if (URL == null || URL.equals("undefined")) {
			URL = getUrl();
			dontLoad = true;
		}*/

		dimensions.width = dimensions.width == 0 ? ViewGroup.LayoutParams.FILL_PARENT : dimensions.width;
		//System.out.println("expandInUI: width=" +dimensions.width);
		dimensions.height = dimensions.height == 0 ? ViewGroup.LayoutParams.FILL_PARENT : dimensions.height;
		//System.out.println("expandInUI: height=" +dimensions.height);

		
		// Is the size information wrong? Test cases where javascript is not updated.
		if (dimensions.width < metrics.widthPixels)
		{
			dimensions.width = metrics.widthPixels;
		}
		
		
		if(mExpandedFrame!=null) ((ViewGroup)((Activity) getContext()).getWindow().getDecorView()).removeView(mExpandedFrame);
		 
		mExpandedFrame = new RelativeLayout(getContext());
		android.widget.RelativeLayout.LayoutParams adLp = new RelativeLayout.LayoutParams(
				dimensions.width, dimensions.height);
		int contentViewTop= ((Activity) getContext()).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop(); 
		
		adLp.leftMargin = dimensions.x;
		adLp.topMargin = contentViewTop + dimensions.y;
		
		android.view.WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);

		if (URL == null || URL.equals("undefined")) {
			parentView = (ViewGroup)getParent(); 
			parentView.removeView(this);
			setExpandBackgroundColor(properties, this);
			mExpandedFrame.addView(this,adLp);
			this.useCloseButton(!properties.useCustomClose);
			requestFocus();
		} else {
			MASTAdView expandedView = new MASTAdView(getContext(), true, this);
			setExpandBackgroundColor(properties, expandedView);
			expandedView.setAutoCollapse(false);
			expandedView.setVisibility(View.VISIBLE);
			mExpandedFrame.addView(expandedView, adLp);

			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(URL);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				String responseValue = EntityUtils.toString(entity, "UTF-8");				
				expandedView.loadDataWithBaseURL(null, responseValue, "text/html", "UTF-8", null);
			} catch (Exception e) {
				e.printStackTrace();
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "expandInUIThread", e.getMessage());
			}						
			
			Button buttonClose = new Button(_context);
			buttonClose.setBackgroundDrawable(InternelBrowser.GetSelector(_context,"b_close.png", "b_close.png", "b_close.png"));
			buttonClose.setLayoutParams(new ViewGroup.LayoutParams(30,30));
			buttonClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							injectJavaScript("ormma.close();");						
						}
					});
				}
			});
			LinearLayout ll = new LinearLayout(_context);
			ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			ll.setGravity(Gravity.RIGHT);
			ll.addView(buttonClose);
			mExpandedFrame.addView(ll, adLp);
			expandedView.requestFocus();
		}
		
		((ViewGroup)((Activity) getContext()).getWindow().getDecorView()).addView(mExpandedFrame, lp);
	}

	private void setExpandBackgroundColor(Properties properties, View view) {
		if(properties.useBackground) {
			int opacity = new Float(255*properties.backgroundOpacity).intValue();
			if(opacity < 0) opacity = 0;
			if(opacity > 255) opacity = 255;
			view.setBackgroundColor(
					Color.argb(opacity, 
							Color.red(properties.backgroundColor), 
							Color.green(properties.backgroundColor), 
							Color.blue(properties.backgroundColor))
					);
		}
	}
	
	private void loadExpandedUrl(String Url, MASTAdViewCore parentAd, ViewGroup expandedFrame, boolean dontLoad) {
		mParentAd = parentAd;
		mExpandedFrame = expandedFrame;
		mViewState = ViewState.EXPANDED;
		loadUrl(Url, dontLoad, mDataToInject);
	}
	
	/**
	 * Load content into ad view from a URL or from a string of data. 
	 * @param url URL to load, or to set as source of data if loading from string.
	 * @param dontLoad Boolean flag, if true use content string rather than fetching URL content.
	 * @param dataToInject String of data to inject into webview instead of loading from URL, if dontLoad flag is set.
	 */
	public void loadUrl(String url, boolean dontLoad, String dataToInject) {
		mDataToInject = dataToInject;
		if (!dontLoad) {
			try {
				if((url != null) && (url.length() > 0)) {
					super.loadUrl(url);
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "loadUrl", e.getMessage());
				return;
			}
		} else {
			if((mContent != null) && (mContent.length() > 0)) {
				super.loadDataWithBaseURL(null, mContent, "text/html", "UTF-8", null);
			}
		}
	}
	
	protected void onPageFinished() {
		if (mDataToInject != null){
			injectJavaScript(mDataToInject);
		}
		
		injectJavaScript("Ormma.ready();");
	}
	
	private synchronized void setScriptPath(){
		if (mScriptPath == null){
			mScriptPath = mAssetController.copyTextFromJarIntoAssetDir("/OrmmaAdController.js",
			"/OrmmaAdController.js");
		}
	}

	/**
	 * Set content of ad view.
	 * @param content String data to load into webview.
	 */
	public void setContent(String content) {
		mContent = content;
		if (isExpanded) loadDataWithBaseURL(null, mContent, "text/html", "UTF-8", null);
	}

	/**
	 * Set the id of the publisher site, used when sending request for ads to back-end. REQUIRED.
	 * @param site Id of the ad publisher site to use when retrieving ads.
	 */
	public void setSite(Integer site) {
		if(adserverRequest != null) {
			adserverRequest.setSite(site);
		}
	}
	
	/**
	 * Get the id of the publisher site used when retrieving ads from server.
	 */
	public Integer getSite() {
		if(adserverRequest != null) {
			return adserverRequest.getSite();
		} else {
			return 0;
		}
	}
	
	/**
	 * Set the id of the zone to send to back-end when retrieving ads. REQURIED.
	 * @param zone Int id of zone to send to server.
	 */
	public void setZone(Integer zone) {
		if(adserverRequest != null) {
			adserverRequest.setZone(zone);
		}
	}

	/**
	 * Get the id of the zone sent to back-end when retrieving ads.
	 */
	public Integer getZone() {
		if(adserverRequest != null) {
			return adserverRequest.getZone();
		} else {
			return 0;
		}
	}
	
	/**
	 * Optional.
	 * Set Default setting is test mode where, if the ad code is properly installed, 
	 * the ad response is "Test MODE".
	 * @param enabled
	 */
	public void setTest(Boolean enabled) {
		if(adserverRequest != null) {
			adserverRequest.setTestModeEnabled(enabled);
		}
	}

	/**
	 * Optional.
	 * Get test mode setting.
	 */
	public Boolean getTest() {
		if(adserverRequest != null) {
			return adserverRequest.getTestModeEnabled();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, 
	 * PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). 
	 * Can be used only by premium publishers.
	 * @param premium
	 */
	public void setPremium(Integer premium) {
		if(adserverRequest != null) {
			adserverRequest.setPremium(premium);
		}
	}

	/**
	 * Optional.
	 * Get Filter by premium.
	 */
	public Integer getPremium() {
		if(adserverRequest != null) {
			return adserverRequest.getPremium();
		} else {
			return PREMIUM_STATUS_BOTH;
		}
	}
	
	/**
	 * Optional.
	 * Set Keywords to search ad, delimited by commas, to be passed to back-end server to help with ad selection. No default.
	 * @param keywords
	 */
	public void setKeywords(String keywords) {
		if(adserverRequest != null) {
			adserverRequest.setKeywords(keywords);
		}
	}
	
	/**
	 * Optional.
	 * Get Keywords to search ad delimited by commas.
	 */
	public String getKeywords() {
		if(adserverRequest != null) {
			return adserverRequest.getKeywords();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set minimum width of ad to request from back-end. Should be equal to or less than maximum value.
	 * If they are equal, the back-end may not find a suitable ad to fit in the available space. 
	 * @param minSizeX Int minimum pixel size of ad to request.
	 */
	public void setMinSizeX(Integer minSizeX) {
		if((adserverRequest != null)) {
			adserverRequest.setMinSizeX(minSizeX);
		}
	}
	
	/**
	 * Optional.
	 * Get minimum width of ad to request from server. 
	 */
	public Integer getMinSizeX() {
		if(adserverRequest != null) {
			return adserverRequest.getMinSizeX();
		} else {
			return 0;
		}
	}
	
	/**
	 * Optional.
	 * Set minimum height of ad to request from back-end. Should be equal to or less than maximum value.
	 * If they are equal, the back-end may not find a suitable ad to fit in the available space.
	 * @param minSizeY Int minimum pixel size to request.
	 */
	public void setMinSizeY(Integer minSizeY) {
		if((adserverRequest != null)) {
			adserverRequest.setMinSizeY(minSizeY);
		}
	}
	
	/**
	 * Optional.
	 * Get minimum height of ad to be requested from server.
	 */
	public Integer getMinSizeY() {
		if(adserverRequest != null) {
			return adserverRequest.getMinSizeY();
		} else {
			return 0;
		}
	}
	
	/**
	 * Optional.
	 * Set maximum width of ad to request from back-end. Should be equal to or greater than minimum value.
	 * If they are equal, the back-end may not find a suitable ad to fit in the available space.
	 * @param maxSizeX Int pixel width to send in ad request.
	 */
	public void setMaxSizeX(Integer maxSizeX) {
		if((adserverRequest != null)) {
			adserverRequest.setSizeX(maxSizeX);
		}
	}
	
	/**
	 * Optional.
	 * Get maximum width of ad to be requested from server.
	 */
	public Integer getMaxSizeX() {
		if(adserverRequest != null) {
			return adserverRequest.getSizeX();
		} else {
			return 0;
		}
	}
	
	/**
	 * Optional.
	 * Set maximum height of ad to request from back-end. Should be equal to or greater than minimum value.
	 * If they are equal, the back-end may not find a suitable ad to fit in the available space.
	 * @param maxSizeY Int pixel height to send in ad request.
	 */
	public void setMaxSizeY(Integer maxSizeY) {
		if((adserverRequest != null) ) {
			adserverRequest.setSizeY(maxSizeY);
		}
	}
	
	/**
	 * Optional.
	 * Get maximum height of ad to be requested from server. 
	 */
	public Integer getMaxSizeY() {
		if(adserverRequest != null) {
			return adserverRequest.getSizeY();
		} else {
			return 0;
		}
	}
	
	int bgColor = Constants.DEFAULT_COLOR;
	
	/**
	 * Optional.
	 * Set Background color of ad view. Can use named color values defined by app or android (such as Color.BLACK)
	 * or number value with or without alpha (such as 0x0000FF - opaque blue, or 0xA00000FF - partly transparent blue.)
	 * 
	 * @param backgroundColor Int color value for background.
	 */
	@Override
	public void setBackgroundColor(int backgroundColor) {
		if(adserverRequest != null) {
			try
			{
				bgColor = backgroundColor;
				adserverRequest.setParamBG(backgroundColor);
				super.setBackgroundColor(backgroundColor);
			}catch(Exception e)
			{
				adLog.log(MASTAdLog.LOG_LEVEL_1, MASTAdLog.LOG_TYPE_ERROR, "AdServerViewCore.setBackgroundColor", e.getMessage());
			}
		}	
	}

	/**
	 * Optional.
	 * Get Background color of ad view. Default is opaque white.
	 */
	public int getBackgroundColor() {
		if(adserverRequest != null) {
			return bgColor;
		} else {
			return Constants.DEFAULT_COLOR;
		}
	}

	/**
	 * Optional.
	 * Set Text color of links. Can use named color values defined by app or android (such as Color.BLACK)
	 * or number value with or without alpha (such as 0x0000FF - opaque blue, or 0xA00000FF - partly transparent blue.)
	 * @param textColor
	 */
	public void setTextColor(int textColor) {
		if(adserverRequest != null) {
			adserverRequest.setParamLINK(textColor);
		}
	}
	
	/**
	 * Optional.
	 * Get Text color value for links.
	 */
	public int getTextColor() {
		if(adserverRequest != null) {
			return adserverRequest.getParamLINK();
		} else {
			return Constants.DEFAULT_COLOR;
		}
	}

	/**
	 * Optional (and for advanced users only.)
	 * Overrides the URL of ad server SDK will communicate with. Only change this if you are certain of what you are doing.
	 * 
	 * @param adserverURL String value for ad server. Default is: "http://ads.mocean.mobi/ad".
	 */
	public void setAdserverURL(String adserverURL) {
		if(adserverRequest != null) {
			adserverRequest.setAdserverURL(adserverURL);
		}
	}

	/**
	 * Optional.
	 * Get URL of ad server this ad view will use when communicating with back end.
	 */
	public String getAdserverURL() {
		if(adserverRequest != null) {
			return adserverRequest.getAdserverURL();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set user location latitude value (given in degrees.decimal degrees). No default value. If location
	 * detection is enabled, the specified value will be overridden after the next device location fix.
	 * @param latitude Latitude in degrees.decimal format.
	 */
	public void setLatitude(String latitude) {
		if((adserverRequest != null) && (latitude != null)) {
			adserverRequest.setLatitude(latitude);
		}
	}
	
	/**
	 * Optional.
	 * Get user location latitude value (given in degrees.decimal degrees).
	 */
	public String getLatitude() {
		if(adserverRequest != null) {
			String latitude = adserverRequest.getLatitude();

			if(latitude != null) {
				return latitude;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set user location longtitude value (given in degrees.decimal degrees). No default value. If location
	 * detection is enabled, the specified value will be overridden after the next device location fix.
	 * @param longitude Longitude value (given in degrees.decimal degrees).
	 */
	public void setLongitude(String longitude) {
		if((adserverRequest != null) && (longitude != null)) {
			adserverRequest.setLongitude(longitude);
		}
	}
	
	/**
	 * Optional.
	 * Get user location longtitude value (given in degrees.decimal degrees).
	 */
	public String getLongitude() {
		if(adserverRequest != null) {
			String longitude = adserverRequest.getLongitude();

			if(longitude != null) {
				return longitude;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Set Country of ad viewer, to be passed to back-end server to help with ad selection; use ISO 3166 format.
	 * By default no value is sent and the back-end uses the IP address to detect the country of the caller.
	 * 
	 * @param country String name of country.
	 */
	public void setCountry(String country) {
		if(adserverRequest != null) {
			adserverRequest.setCountry(country);
		}
	}
	
	/**
	 * Optional.
	 * Get Country of ad viewer.
	 */
	public String getCountry() {
		if(adserverRequest != null) {
			return adserverRequest.getCountry();
		} else {
			return null;
		}
	}

	/**
	 * Optional.
	 * Set Region of viewer, to be passed to back-end server to help with ad selection. No default.
	 * ISO 3166-2 is used for United States and Canada and FIBS 10-4 is used for other countries.
	 *  
	 * @param region String region name.
	 */
	public void setRegion(String region) {
		if(adserverRequest != null) {
			adserverRequest.setRegion(region);
		}
	}
	
	/**
	 * Optional.
	 * Get Region of ad viewer.
	 */
	public String getRegion() {
		if(adserverRequest != null) {
			return adserverRequest.getRegion();
		} else {
			return null;
		}
	}

	/**
	 * Optional, for US only.
	 * Set City of the device user (with state), to be passed to back-end server to help with ad selection. No default.
	 * @param city
	 */
	public void setCity(String city) {
		if(adserverRequest != null) {
			adserverRequest.setCity(city);
		}
	}
	
	/**
	 * Optional, for US only.
	 * Get City of the device user (with state). 
	 */
	public String getCity() {
		if(adserverRequest != null) {
			return adserverRequest.getCity();
		} else {
			return null;
		}
	}
		
	/**
	 * Optional.
	 * Type of ads to be returned (1 - text, 2 - image, 4 - richmedia ad). 
	 * You can set different combinations with these values. 
	 * For example, 3 = 1 + 2 (text + image), 7 = 1 + 2 + 4 (text + image + richmedia)
	 *   
	 * @param type Int ad type, default: 3 (text or image)
	 */
	public void setType(Integer type) {
		if(adserverRequest != null) {
			adserverRequest.setType(type);
		}
	}

	/**
	 * Optional.
	 * Return the type of ad to be returned for display.
	 * 
	 * @return Int ad type, which can be be a combination of these values: 1 - text, 2 - image, 4 - richmedia.
	 */
	public Integer getType() {
		if(adserverRequest != null) {
			return adserverRequest.getType();
		}else return null;
	}
	
	/**
	 * Optional, for US only.
	 * Set Area code of a user. 
	 * @param area String area code value.
	 */
	public void setArea(String area) {
		if(adserverRequest != null) {
			adserverRequest.setArea(area);
		}
	}

	/**
	 * Optional, for US only.
	 * Get Area code of a user. 
	 */
	public String getArea() {
		if(adserverRequest != null) {
			return adserverRequest.getArea();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional, for US only.
	 * Set Metro code of a user.
	 * DEPRECATED - use DMS instead. 
	 * @param metro
	 */
	@Deprecated
	public void setMetro(String metro) {
		if(adserverRequest != null) {
			adserverRequest.setMetro(metro);
		}
	}
	
	/**
	 * Optional, for US only.
	 * Get Metro code of a user.
	 * DEPRECATED - use DMS instead. 
	 */
	@Deprecated
	public String getMetro() {
		if(adserverRequest != null) {
			return adserverRequest.getMetro();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional, for US only.
	 * Set Dma code of a user, to be passed to back-end server to help with ad selection. REPLACES METRO. 
	 * @param dma
	 */
	public void setDma(String dma) {
		if(adserverRequest != null) {
			adserverRequest.setDma(dma);
		}
	}
	
	/**
	 * Optional, for US only.
	 * Get Dma code of a user. REPLACES METRO. 
	 */
	public String getDma() {
		if(adserverRequest != null) {
			return adserverRequest.getDma();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional, for US only.
	 * Set Zip/Postal code of user, to be passed to back-end server to help with ad selection. 
	 * @param zip
	 */
	public void setZip(String zip) {
		if(adserverRequest != null) {
			adserverRequest.setZip(zip);
		}
	}
	
	/**
	 * Optional, for US only.
	 * Get Zip/Postal code of user. 
	 */
	public String getZip() {
		if(adserverRequest != null) {
			return adserverRequest.getZip();
		} else {
			return null;
		}
	}
	
	/**
	 * Optional.
	 * Set User carrier name, to be passed to back-end server to help with ad selection.
	 * 
	 * @param carrier String name of carrier. No default value.
	 */
	public void setCarrier(String carrier) {
		if(adserverRequest != null) {
			adserverRequest.setCarrier(carrier);
		}
	}
	
	/**
	 * Optional.
	 * 
	 * Get User carrier name.
	 */
	public String getCarrier() {
		if(adserverRequest != null) {
			return adserverRequest.getCarrier();
		} else {
			return null;
		}
	}
	
	/**
	 * Set log level to one of the log level values defined in he MASTAdLog class
     * (corresponding to errors, errors + warnings, or everything including server traffic.)
     * 
	 * @param logLevel Int log level to control which messages will be sent to the logs.
	 */
	public void setLogLevel(int logLevel)
	{
		adLog.setLogLevel(logLevel);
	}
	
	/**
	 * Set flag controlling use of internal browser field when opening ad URLs.
	 * 
	 * @param value True to open URL with internal browser, false to launch full standard browser.
	 */
	public void setInternalBrowser(boolean value)
	{
		internalBrowser = value;
	}

	/**
	 * Get current setting for internal browser usage.
	 * 
	 * @return True if ad URLs will be opened in internal browser, false if standard browser will be launched.
	 */
	public boolean getInternalBrowser()
	{
		return internalBrowser;
	}
	
	/**
	 * Optional.
	 * Set minimum distance (in meters) location must change for app to be notified of new location from the GPS system.
	 * Use 0 (the default) if no distance based updates are desired (legacy behavior.)
	 * @param Float distance in meters
	 */
	public void setLocationMoveDistance(float meters)
	{
		locationMinMoveMeters = meters;
	}
	
	/**
	 * Optional.
	 * Get minimum distance (in meters) location must change for app to be notified of new location.
	 */
	public float getLocationMoveDistance()
	{
		return locationMinMoveMeters;
	}
	
	/**
	 * Optional.
	 * Set minimum time between location updates from the GPS system. 
	 * Use 0 (the default) if recurring time updates are not desired (legacy behavior.)
	 * @param Integer repeat time in milliseconds
	 */
	public void setLocationMinWait(int millis)
	{
		locationMinWaitMillis = millis;
	}
	
	/**
	 * Get minimum wait time between location updates from the GPS system. If 0, only one location update is performed.
	 * @return Int wait value, in milliseconds.
	 */
	public int getLocationMinWait()
	{
		return locationMinWaitMillis;
	}
	
	/**
	 * Turn location detection on or off. If enabled, the device GPS location capabilities will be used to obtain
	 * a position fix at least one. Ongoing location updates may continue depending on the minimum wait and minimum
	 * distance settings. If user-specified latitude and/or longitude values have been set, and location detection
	 * is enabled, the detected location will override preset values.
	 * @param detect If true, location detection is enabled.
	 */
	public void setLocationDetection(boolean detect)
	{
		if(detect)
		{
			final AutoDetectParameters autoDetectParameters = AutoDetectParameters.getInstance();
	
			if (locationMinWaitMillis == null)
				locationMinWaitMillis = 0;
			if (locationMinMoveMeters == null)
				locationMinMoveMeters = 0.0F;
			
			if((adserverRequest.getLatitude() == null) || (adserverRequest.getLongitude() == null)) {
				if((autoDetectParameters.getLatitude() == null) || (autoDetectParameters.getLongitude() == null)) {
			    	int isAccessFineLocation = _context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			    	
			    	if(isAccessFineLocation == PackageManager.PERMISSION_GRANTED) {
						locationManager = (LocationManager)_context.getSystemService(Context.LOCATION_SERVICE);
						boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
						if(isGpsEnabled) {
							listener = new LocListener(this.getContext(), locationMinWaitMillis.intValue(), locationMinMoveMeters.floatValue(), LocationManager.GPS_PROVIDER, Looper.getMainLooper(), adLog)
							{
								public void fail(String m)
								{
									// Nothing (legacy)
								}
								
								public void success(Location location)
								{
									try {
										double latitude = location.getLatitude();
										double longitude = location.getLongitude();
										
										adserverRequest.setLatitude(Double.toString(latitude));
										adserverRequest.setLongitude(Double.toString(longitude));
										autoDetectParameters.setLatitude(Double.toString(latitude));
										autoDetectParameters.setLongitude(Double.toString(longitude));
										adLog.log(MASTAdLog.LOG_LEVEL_3, MASTAdLog.LOG_TYPE_INFO, "GPSLocationChanged=", "("+autoDetectParameters.getLatitude()+";"+autoDetectParameters.getLongitude()+")");
										
						    		} catch (Exception e) {
						    			adLog.log(MASTAdLog.LOG_LEVEL_2,MASTAdLog.LOG_TYPE_ERROR,"GPSLocationChanged",e.getMessage());
						    		}									
								}
							};
							//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationMinWaitMillis.intValue(), locationMinMoveMeters.floatValue(), listener, Looper.getMainLooper());
							listener.start();
						}else
						{
							adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_WARNING, "AutoDetectParameters.Gps", "not avalable");
						}
			    	}else adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_WARNING, "AutoDetectParameters.Gps", "no permission ACCESS_FINE_LOCATION");
				} else {
					adserverRequest.setLatitude(autoDetectParameters.getLatitude());
					adserverRequest.setLongitude(autoDetectParameters.getLongitude());
					adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_WARNING, "AutoDetectParameters.Gps=", "("+autoDetectParameters.getLatitude()+";"+autoDetectParameters.getLongitude()+")");
				}
			}
		}
	}
		
	/**
	 * Get setting indicating use of device ID is allowed or not.
	 * @return True if device ID can be used, false otherwise.
	 */
	public boolean getUseSystemDeviceId()
	{
		return ContentManager.getInstance(this).getUseSystemDeviceId();
	}
	
	/**
	 * Set flag indicating if device ID can be used (sent to back-end) or not.
	 * @param value True if system device ID can be used, false otherwise.
	 */
	public void setUseSystemDeviceId(boolean value)
	{
		ContentManager.getInstance(this).setUseSystemDeviceId(value);
	}
	
	/**
	 * Set value to send to back-end as device ID (instead of detected system ID value.)
	 * @param value String to send to server.
	 */
	public void setDeviceId(String value)
	{
		ContentManager.getInstance(this).setDeviceId(value);
	}
	
	/**
	 * Return device ID value currently being used with ad requests.
	 * @return String value for device ID.
	 */
	public String getDeviceId()
	{
		return ContentManager.getInstance(this).getDeviceId();
	}
	
	
	/**
	 * Play video
	 * 
	 * @param url
	 *            - video URL
	 * @param audioMuted
	 *            - should audio be muted
	 * @param autoPlay
	 *            - should video play immediately
	 * @param controls
	 *            - should native player controls be visible
	 * @param loop
	 *            - should video start over again after finishing
	 * @param d
	 *            - inline area dimensions
	 * @param startStyle
	 *            - normal/fullscreen; full screen if video should play in full
	 *            screen
	 * @param stopStyle
	 *            - normal/exit; exit if video should exit after video stops
	 */
	public void playVideo(String url, boolean audioMuted, boolean autoPlay,
			boolean controls, boolean loop, Dimensions d, String startStyle,
			String stopStyle) {

		Message msg = handler.obtainMessage(MESSAGE_PLAY_VIDEO); // mHandler

		PlayerProperties properties = new PlayerProperties();

		properties.setProperties(audioMuted, autoPlay, controls, false,loop,
				startStyle, stopStyle);

		Bundle data = new Bundle();
		data.putString(EXPAND_URL, url);
		data.putString(ACTION_KEY, ACTION.PLAY_VIDEO.toString());		
		
		data.putParcelable(PLAYER_PROPERTIES, properties);
		
		if(d != null)
			data.putParcelable(DIMENSIONS, d);

		/*if (properties.isFullScreen()) {
			try {
				ormmaEvent("playvideo","fulscreen=true");
				Intent intent = new Intent();
				intent.setAction("ORMMA_ANCION_HANDLER");
				intent.putExtras(data);
				getContext().startActivity(intent);
			}
			catch(ActivityNotFoundException e){
				e.printStackTrace();
			}
		} else*/ {
			msg.setData(data);
			handler.sendMessage(msg); // mHandler
		}
	}
	
	
	private class SetupOrmmaPlayer implements Runnable {
		private WebView view;
		private Bundle data;
		
		public SetupOrmmaPlayer(WebView view,Bundle data) {
			this.view = view;
			this.data = data;
		}

		@Override
		public void run() {
			try {
				if (isShowMediaPlayerFrame == false) {
					if (mediaPlayerFrame != null) {
						((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(mediaPlayerFrame);
					}
					
					PlayerProperties properties = (PlayerProperties) data.getParcelable(PLAYER_PROPERTIES);
					String url = data.getString(EXPAND_URL);
	
					final OrmmaPlayer videoPlayer = getPlayer();
					videoPlayer.setPlayData(properties, url);
	
					int contentViewTop = ((Activity) getContext()).getWindow()
							.findViewById(Window.ID_ANDROID_CONTENT).getTop();
					mediaPlayerFrame = new RelativeLayout(getContext());
					RelativeLayout.LayoutParams mediaPlayerLayoutParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
					mediaPlayerFrame.setPadding(0, contentViewTop, 0, 0);
					mediaPlayerFrame.setBackgroundColor(Color.BLACK);
	
					final Runnable closeRunnable = new Runnable() {
						@Override
						public void run() {
							isShowMediaPlayerFrame = false;
							if (mediaPlayerFrame != null) {
								((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(mediaPlayerFrame);
							}
						}
					};
					videoPlayer.setOnCompletionRunnable(closeRunnable);
					videoPlayer.setOnErrorRunnable(closeRunnable);
					
					Button buttonClose = new Button(getContext());
					buttonClose.setBackgroundDrawable(Utils.GetSelector(getContext(),"b_close.png", "b_close.png", "b_close.png"));
					buttonClose.setLayoutParams(getLayoutParamsByDrawableSize("b_close.png"));
					buttonClose.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									isShowMediaPlayerFrame = false;
									videoPlayer.releasePlayer();
									if (mediaPlayerFrame != null) {
										((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(mediaPlayerFrame);
									}
								}
							});
						}
					});
					LinearLayout buttonCloseFrame = new LinearLayout(getContext());
					buttonCloseFrame.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					buttonCloseFrame.setGravity(Gravity.RIGHT);
					buttonCloseFrame.addView(buttonClose);
					
					videoPlayer.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

					LinearLayout videoFrame = new LinearLayout(getContext());
					videoFrame.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
					videoFrame.setGravity(Gravity.CENTER);
					videoFrame.addView(videoPlayer);
					mediaPlayerFrame.addView(videoFrame);
					mediaPlayerFrame.addView(buttonCloseFrame);
					
					((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
							.addView(mediaPlayerFrame, mediaPlayerLayoutParams);
					
					mediaPlayerFrame.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return true;
						}
					});
					
					videoPlayer.playVideo();
					isShowMediaPlayerFrame = true;
				}
			} catch (Exception e) {
			}
			/*try {
				PlayerProperties properties = (PlayerProperties) data.getParcelable(PLAYER_PROPERTIES);
				Dimensions d = (Dimensions) data.getParcelable(DIMENSIONS);
				String url = data.getString(EXPAND_URL);

				OrmmaPlayer videoPlayer = getPlayer();
				videoPlayer.setPlayData(properties, url);
				
				if (d != null) {
					FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams((int) (d.width), (int) (d.height));
					videoPlayer.setLayoutParams(fl);
					FrameLayout backGround = new FrameLayout(getContext());
					backGround.setId(BACKGROUND_ID);
					backGround.setPadding((int) (d.x), (int) (d.y), 0, 0);
					view.addView(backGround, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
					backGround.addView(videoPlayer);
				} else {
					videoPlayer.setLayoutParams(view.getLayoutParams());
					view.addView(videoPlayer);
				}
				videoPlayer.playVideo();
			} catch (Exception e){
			}*/
		}
	}
	
	private ViewGroup.LayoutParams getLayoutParamsByDrawableSize(String fileName) {
		int sizeDrawableWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
		int sizeDrawablehHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

		try {
			BitmapDrawable sizeDrawable = (BitmapDrawable)Utils.GetDrawable(getContext(), fileName);
			if (sizeDrawable != null) {
				sizeDrawableWidth = sizeDrawable.getBitmap().getWidth();
				sizeDrawablehHeight = sizeDrawable.getBitmap().getHeight();
			}
		} catch (Exception e) {
		}
		
		return new ViewGroup.LayoutParams(sizeDrawableWidth, sizeDrawablehHeight);
	}

	private OrmmaPlayer getPlayer() {
		if(player != null) player.releasePlayer();
		player = new OrmmaPlayer(getContext());	
		return player;
	}
	
	/**
	 * Play audio
	 * 
	 * @param url
	 *            - audio URL
	 * @param autoPlay
	 *            - should audio play immediately
	 * @param controls
	 *            - should native controls be visible
	 * @param loop
	 *            - should audio start over again after finishing
	 * @param position
	 *            - should audio be included with ad content
	 * @param startStyle
	 *            - normal/fullscreen; full screen if audio should play in full
	 *            screen
	 * @param stopStyle
	 *            - normal/exit; exit if audio should exit after audio stops
	 */
	public void playAudio(String url, boolean autoPlay, boolean controls,
			boolean loop, boolean position, String startStyle, String stopStyle) {

		PlayerProperties properties = new PlayerProperties();

		 properties.setProperties(false, autoPlay, controls, position,loop, startStyle, stopStyle);

		Bundle data = new Bundle();

		data.putString(ACTION_KEY, ACTION.PLAY_AUDIO.toString());
		data.putString(EXPAND_URL, url);
		data.putParcelable(PLAYER_PROPERTIES, properties);

		/*if (properties.isFullScreen()) {
			try {
				Intent intent = new Intent();
				intent.setAction("ORMMA_ANCION_HANDLER");
				intent.putExtras(data);
				getContext().startActivity(intent);
			}
			catch(ActivityNotFoundException e){
				e.printStackTrace();
			}
		} else*/ {
			Message msg = handler.obtainMessage(MESSAGE_PLAY_AUDIO); // mHandler
			msg.setData(data);
			handler.sendMessage(msg); // mHandler
		}
	}
	
	private class SetupOrmmaAudioPlayer implements Runnable {
		private Bundle data;
		
		public SetupOrmmaAudioPlayer(Bundle data) {
			this.data = data;
		}

		@Override
		public void run() {
			try {
				if (isShowMediaPlayerFrame == false) {
					if (mediaPlayerFrame != null) {
						((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(mediaPlayerFrame);
					}
					
					PlayerProperties properties = (PlayerProperties) data.getParcelable(PLAYER_PROPERTIES);
					String url = data.getString(EXPAND_URL);
	
					final OrmmaPlayer audioPlayer = getPlayer();
					audioPlayer.setPlayData(properties, url);
	
					int contentViewTop = ((Activity) getContext()).getWindow()
							.findViewById(Window.ID_ANDROID_CONTENT).getTop();
					mediaPlayerFrame = new RelativeLayout(getContext());
					RelativeLayout.LayoutParams mediaPlayerLayoutParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
					mediaPlayerFrame.setPadding(0, contentViewTop, 0, 0);
	
					final Runnable closeRunnable = new Runnable() {
						@Override
						public void run() {
							isShowMediaPlayerFrame = false;
							if (mediaPlayerFrame != null) {
								((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(mediaPlayerFrame);
							}
						}
					};
					audioPlayer.setOnCompletionRunnable(closeRunnable);
					audioPlayer.setOnErrorRunnable(closeRunnable);
					
					Button buttonClose = new Button(getContext());
					buttonClose.setBackgroundDrawable(Utils.GetSelector(getContext(),"b_close.png", "b_close.png", "b_close.png"));
					buttonClose.setLayoutParams(getLayoutParamsByDrawableSize("b_close.png"));
					buttonClose.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									isShowMediaPlayerFrame = false;
									audioPlayer.releasePlayer();
									if (mediaPlayerFrame != null) {
										((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(mediaPlayerFrame);
									}
								}
							});
						}
					});
					LinearLayout buttonCloseFrame = new LinearLayout(getContext());
					buttonCloseFrame.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					buttonCloseFrame.setGravity(Gravity.RIGHT);
					buttonCloseFrame.addView(buttonClose);
					
					ImageView audioPlayerLogo = new ImageView(getContext());
					audioPlayerLogo.setImageDrawable(Utils.GetDrawable(getContext(), "note.png"));
					audioPlayerLogo.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					LinearLayout audioPlayerLogoFrame = new LinearLayout(getContext());
					audioPlayerLogoFrame.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
					audioPlayerLogoFrame.setGravity(Gravity.CENTER);
					audioPlayerLogoFrame.addView(audioPlayerLogo);
					
					audioPlayer.setLayoutParams(new LinearLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
					mediaPlayerFrame.addView(audioPlayer);
					mediaPlayerFrame.addView(audioPlayerLogoFrame);
					mediaPlayerFrame.addView(buttonCloseFrame);
					
					((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
							.addView(mediaPlayerFrame, mediaPlayerLayoutParams);
					
					mediaPlayerFrame.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							return true;
						}
					});
					
					audioPlayer.playAudio();
					isShowMediaPlayerFrame = true;
				}
			} catch (Exception e) {
			}
			/*PlayerProperties properties = (PlayerProperties) data.getParcelable(PLAYER_PROPERTIES);
			String url = data.getString(EXPAND_URL);

			OrmmaPlayer audioPlayer = getPlayer();
			audioPlayer.setPlayData(properties, url);
			audioPlayer.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
			((ViewGroup) getParent()).addView(audioPlayer);
			audioPlayer.playAudio();*/
		}
	}
	
	/**
	 * Open Map
	 * 
	 * @param url
	 *            - map url
	 * @param fullscreen
	 *            - should map be shown in full screen
	 */
	public void openMap(String POI, boolean fullscreen) {
		POI = POI.trim();
		POI = OrmmaUtils.convert(POI);
		POI = "geo:0,0?q=" + POI; 

			try {
				// start google maps
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(POI));
				mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(mapIntent);
				
			} catch (ActivityNotFoundException e) {
				//e.printStackTrace();
				//Toast.makeText(getContext(), Constants.STR_ORMMA_ERROR_OPEN_MAP, Toast.LENGTH_LONG).show();
				adLog.log(MASTAdLog.LOG_LEVEL_2, MASTAdLog.LOG_TYPE_ERROR, "openMap", Constants.STR_ORMMA_ERROR_OPEN_MAP);
			}
	}
	
	protected static String readFile(String path)
    {
		StringBuffer result = new StringBuffer();
		
    	try
    	{
    		File ifile = new File(path);
    		FileInputStream fIn = new FileInputStream(ifile);
    		//System.out.println("copy: from " + input + ", size=" + ifile.length() + " to " + output);
    		BufferedReader buffer = new BufferedReader(new InputStreamReader(fIn));
    		String line;
			while ((line = buffer.readLine()) != null)
			{
				result.append(line);
			}
			buffer.close();
			fIn.close(); 
    	}
    	catch(Exception ex)
    	{
    		//DiagnosticsLog.addMessage("Exception copying file: " + input, "add_draft_media", DiagnosticsLog.logLevelError);
    		//DiagnosticsLog.addMessage("Message: " + ex.getMessage(), "add_draft_media", DiagnosticsLog.logLevelError);
    		//ex.printStackTrace();
    		System.out.println("Exception reading file: " + path);
    		return null;
    	}
 
    	System.out.println("readFile: " + result.toString());
    	return result.toString();
    }
}