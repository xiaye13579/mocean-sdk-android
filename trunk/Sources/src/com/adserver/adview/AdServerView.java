/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview;

import java.util.Hashtable;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;

/**
 * Viewer of advertising.
 * Following parametres are defined automatically, if they are equal NULL:
 * latitude - Latitude. 
 * longitude - Longitude.
 * carrier - Carrier name.
 * country - Country of visitor (for example: US). 
 * ua - The browser user agent of the device making the request.
 */
public class AdServerView extends AdServerViewCore {
	private AutoDetectParametersThread autoDetectParametersThread;
	private LocationManager locationManager;
	private WhereamiLocationListener listener;
	
	/**
	 * @deprecated
	 * Creation of viewer of advertising.
	 * @param context - The reference to the context of Activity.
	 * @param minSizeX - The minimum width of advertising.
	 * @param minSizeY - The minimum height of advertising.
	 * @param sizeX - The maximum width of advertising.
	 * @param sizeY - The maximum height of advertising.
	 * @param isInternalBrowser - The flag which operates advertising opening. False - Ad opens in an external browser. True - Ad opening at the help of a adClickListener.
	 * @param adClickListener - The interface for advertising opening.
	 * @param defaultImage - The identifier of the resource, which will be shown during advertising loading.
	 * @param adReloadPeriod - The period of an automatic reload of advertising (in milliseconds).
	 * @param visibleMode - Mode of loading and refreshing of Ad (use VISIBLE_MODE_CASE1, VISIBLE_MODE_CASE2, VISIBLE_MODE_CASE3).
	 * @param appId - The id of the application.
	 * @param campaign - campaign.
	 * @param mode - Mode of viewer of advertising (use MODE_COUNTER_ONLY, MODE_ADS_ONLY, MODE_COUNTER_AND_ADS).
	 * @param site - The id of the publisher site.
	 * @param zone - The id of the zone of publisher site.
	 * @param ip - The IP address of the carrier gateway over which the device is connecting.
	 * @param keywords - Keywords to search ad delimited by commas.
	 * @param adstype - Type of advertisement (ADS_TYPE_TEXT_ONLY - text only, ADS_TYPE_IMAGES_ONLY - image only, ADS_TYPE_TEXT_AND_IMAGES - image and text, ADS_TYPE_SMS - SMS ad). SMS will be returned in XML.
	 * @param over18 - Filter by ad over 18 content (OVER_18_TYPE_DENY - deny over 18 content , OVER_18_TYPE_ONLY - only over 18 content, OVER_18_TYPE_ALL - allow all ads including over 18 content).
	 * @param latitude - Latitude. 
	 * @param longitude - Longitude.
	 * @param ua - The browser user agent of the device making the request.
	 * @param premium - Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). Can be used only by premium publishers.
	 * @param isTestModeEnabled - Setting is test mode where, if the ad code is true, the ad response is "Test MODE". 
	 * @param count - Quantity of ads, returned by a server. Maximum value is 5. 
	 * @param country - Country of visitor (for example: US). 
	 * @param region - Region of visitor (for example: NY). 
	 * @param isTextborderEnabled - Show borders around text ads (false - non-borders, true - show borders). 
	 * @param paramBorder - Borders color (for example: #000000).
	 * @param paramBG - Background color in borders (for example: #ffffff).
	 * @param paramLINK - Text color (for example: #ffffff).
	 * @param carrier - Carrier name.
	 * @param target - Target attribute for HTML link element (TARGET_BLANK - open the linked document in a new window, TARGET_SELF - open the linked document in the same frame, TARGET_PARENT - open the linked document in the parent frameset, TARGET_TOP - open the linked document in the full body of the window). 
	 * @param url - URL of site for which it is necessary to receive advertising. 
	 * @param customParameters - Custom parameters.
	 */
	public AdServerView(Context context, 
			Integer minSizeX, Integer minSizeY, Integer sizeX, Integer sizeY, 
			boolean isInternalBrowser, OnAdClickListener adClickListener,
			int defaultImage, Long adReloadPeriod, Integer visibleMode,  
			String appId, String campaign, Integer mode,
			String site, String zone, String ip, String keywords, Integer adstype, Integer over18, 
			String latitude, String longitude, String ua, Integer premium,  
			Boolean isTestModeEnabled, Integer count, String country, String region, 
			Boolean isTextborderEnabled, String paramBorder, String paramBG, String paramLINK, String carrier, 
			String target, String url, Hashtable<String, String> customParameters) {
		super(context, 
				minSizeX, minSizeY, sizeX, sizeY, 
				adClickListener, 
				defaultImage, adReloadPeriod, visibleMode, 
				site, zone, keywords,  
				latitude, longitude, ua, premium, isTestModeEnabled, 
				country, region, paramBG, paramLINK,
				carrier, customParameters);
		initialize(context);
	}

	/**
	 * Creation of viewer of advertising.
	 * @param context - The reference to the context of Activity.
	 * @param site - The id of the publisher site.
	 * @param zone - The id of the zone of publisher site.
	 */
	public AdServerView(Context context, String site, String zone) {
		super(context, site, zone);
		initialize(context);
	}
	
	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AdServerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 */
	public AdServerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 */
	public AdServerView(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(Context context) {
		autoDetectParametersThread = new AutoDetectParametersThread(context, this, adserverRequest);
	}
	
	@Override
	
	protected void onAttachedToWindow() {
		if((autoDetectParametersThread != null) 
				&& (autoDetectParametersThread.getState().equals(Thread.State.NEW))) { 
			autoDetectParametersThread.start();
		}
		
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		if((locationManager != null) && (listener != null)) {
			locationManager.removeUpdates(listener);
		}

		if(autoDetectParametersThread != null) {
			try {
				autoDetectParametersThread.interrupt();
			} catch (Exception e) {
			}
		}

		super.onDetachedFromWindow();
	}
	
	private class AutoDetectParametersThread extends Thread {
		private Context context;
		private AdServerViewCore adserverView;
		private AdserverRequest adserverRequest;

		public AutoDetectParametersThread(Context context,
				AdServerViewCore adserverView, AdserverRequest adserverRequest) {
			this.context = context;
			this.adserverView = adserverView;
			this.adserverRequest = adserverRequest;
		}

		@Override
		public void run() {
			if(adserverRequest != null) {
				AutoDetectParameters autoDetectParameters = AutoDetectParameters.getInstance();
				
				if(adserverRequest.getVersion() == null) {
					if(autoDetectParameters.getVersion() == null) {
						try {
							String version = Constants.SDK_VERSION;
							
							if((version != null) && (version.length() > 0)) {
								adserverRequest.setVersion(version);
								autoDetectParameters.setVersion(version);
							}
						} catch (Exception e) {
						}
					} else {
						adserverRequest.setVersion(autoDetectParameters.getVersion());
					}
				}
				
				if((adserverRequest.getLatitude() == null) || (adserverRequest.getLongitude() == null)) {
					if((autoDetectParameters.getLatitude() == null) || (autoDetectParameters.getLongitude() == null)) {
				    	int isAccessFineLocation = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
				    	
				    	if(isAccessFineLocation == PackageManager.PERMISSION_GRANTED) {
							locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
							boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
							if(isGpsEnabled) {
								listener = new WhereamiLocationListener(locationManager, autoDetectParameters); 
								locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener, Looper.getMainLooper());
							}
				    	}
					} else {
						adserverRequest.setLatitude(autoDetectParameters.getLatitude());
						adserverRequest.setLongitude(autoDetectParameters.getLongitude());
					}
				}

				if(adserverRequest.getCarrier() == null) {
					if(autoDetectParameters.getCarrier() == null) {
						TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
						String networkOperatorName = telephonyManager.getNetworkOperatorName();
					
						if((networkOperatorName != null) && (networkOperatorName.length() > 0)) {
							adserverRequest.setCarrier(networkOperatorName);
							autoDetectParameters.setCarrier(networkOperatorName);
						}
					} else {
						adserverRequest.setCarrier(autoDetectParameters.getCarrier());
					}
				}
				
				if(adserverRequest.getCountry() == null) {
					if(autoDetectParameters.getCountry() == null) {
						Locale defaultLocale = Locale.getDefault();
						String country = defaultLocale.getCountry();
	
						if((country != null) && (country.length() > 0)) {
							adserverRequest.setCountry(country);
							autoDetectParameters.setCountry(country);
						}
					} else {
						adserverRequest.setCountry(autoDetectParameters.getCountry());
					}
				}

				if(adserverRequest.getUa() == null) {
					if(autoDetectParameters.getUa() == null) {
						String userAgent = adserverView.getSettings().getUserAgentString();
	
						if((userAgent != null) && (userAgent.length() > 0)) {
							adserverRequest.setUa(userAgent);
							autoDetectParameters.setUa(userAgent);
						}
					} else {
						adserverRequest.setUa(autoDetectParameters.getUa());
					}
				}
				
				if(adserverRequest.getConnectionSpeed() == null) {
					if(autoDetectParameters.getConnectionSpeed() == null) {
						try {
							Integer connectionSpeed = null;
					    	ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
					    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					    	
							if(networkInfo != null) {
								int type = networkInfo.getType();
								int subtype = networkInfo.getSubtype();
								
								//0 - low (gprs, edge), 1 - fast (3g, wifi)
								if(type == ConnectivityManager.TYPE_WIFI) {
									connectionSpeed = 1;
								} else if(type == ConnectivityManager.TYPE_MOBILE) {
									if(subtype == TelephonyManager.NETWORK_TYPE_EDGE) {
										connectionSpeed = 0;
									} else if(subtype == TelephonyManager.NETWORK_TYPE_GPRS) {
										connectionSpeed = 0;
									} else if(subtype == TelephonyManager.NETWORK_TYPE_UMTS) {
										connectionSpeed = 1;
									}
								}
							}
							
							if(connectionSpeed != null) {
								adserverRequest.setConnectionSpeed(connectionSpeed);
								autoDetectParameters.setConnectionSpeed(connectionSpeed);
							}
						} catch (Exception e) {
						}
					} else {
						adserverRequest.setConnectionSpeed(autoDetectParameters.getConnectionSpeed());
					}
				}
			}
		}
	}
	
	private class WhereamiLocationListener implements LocationListener {
		private LocationManager locationManager;
		private AutoDetectParameters autoDetectParameters;
		
		public WhereamiLocationListener(LocationManager locationManager, 
				AutoDetectParameters autoDetectParameters) {
			this.locationManager = locationManager;
			this.autoDetectParameters = autoDetectParameters;
		}

		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(this);
			
			try {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				adserverRequest.setLatitude(Double.toString(latitude));
				adserverRequest.setLongitude(Double.toString(longitude));
				autoDetectParameters.setLatitude(Double.toString(latitude));
				autoDetectParameters.setLongitude(Double.toString(longitude));
    		} catch (Exception e) {
    		}
	    }

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

}
