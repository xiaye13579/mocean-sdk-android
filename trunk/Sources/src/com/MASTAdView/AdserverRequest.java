package com.MASTAdView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Class encapsulating all of the data and parameters associated with an ad server request.
 */
public class AdserverRequest {
	
	/**
	 * Max sex X to send to server
	 */
	public int sizeX=-1;
	
	/**
	 * Max size X to send to server
	 */
	public int sizeY=-1;
	
	/**
	 * Server side timeout value
	 */
	public int timeout = Constants.DEFAULT_AD_SERVER_TIMEOUT;
	
	private Map<String, String> parameters = new HashMap<String, String>();
	private final String parameter_site = "site";
	private final String parameter_zone = "zone";
	private final String parameter_userAgent = "ua";
	private final String parameter_keywords = "keywords";
	private final String parameter_premium = "premium";
	private final String parameter_track = "track";
	private final String parameter_test = "test";
	private final String parameter_count = "count";
	private final String parameter_country = "country";
	private final String parameter_region = "region";
	private final String parameter_city = "city";
	private final String parameter_area = "area";
	private final String parameter_metro = "metro";
	private final String parameter_dma = "dma";
	private final String parameter_zip = "zip";
	//private final String parameter_adstype = "adstype";
	private final String parameter_latitude = "lat";
	private final String parameter_longitude = "long";
	private final String parameter_background = "paramBG";
	private final String parameter_link = "paramLINK";
	private final String parameter_carrier = "carrier";
	private final String parameter_min_size_x = "min_size_x";
	private final String parameter_min_size_y = "min_size_y";
	private final String parameter_size_x = "size_x";
	private final String parameter_size_y = "size_y";
	private final String parameter_excampaigns = "excampaigns";
	private final String parameter_version = "version";
	//private final String parameter_connection_speed = "connection_speed";
	private final String parameter_size_required = "size_required";
	//private final String parameter_mcc = "mcc";
	//private final String parameter_mnc = "mnc";
	private final String parameter_type = "type";
	//private final String parameter_debug = "debug";
	private final static String parameter_Ad_Call_Timeout = "timeout";
	
	/**
	 * Device ID paramter name
	 */
	public final static String parameter_device_id = "udid";
	
	private String adserverURL = "http://ads.mocean.mobi/ad"; 
	
	private Hashtable<String, String> customParameters;
	
	private MASTAdLog AdLog;
	private Context appContext;
	
	
	/**
	 * Construct an ad server request object
	 * @param AdLog Logging object for diagnostic information
	 * @param appContext Application context
	 */
	public AdserverRequest(MASTAdLog AdLog, Context appContext) {
		this.AdLog = AdLog;
		this.appContext = appContext;
		setPremium(MASTAdViewCore.PREMIUM_STATUS_BOTH);
	}

	/**
	 * Get URL of ad server.
	 * @return
	 */
	public synchronized String getAdserverURL() {
		return adserverURL;
	}

	/**
	 * Overrides the URL of ad server.
	 * @param adserverURL
	 */
	public synchronized void setAdserverURL(String adserverURL) {
		if((adserverURL != null) && (adserverURL.length() > 0)) {
			this.adserverURL = adserverURL;
		}// else AdLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_ERROR, "setAdserverURL", "adserverURL=null");
	}

	/**
	 * Required.
	 * Set the id of the publisher site. 
	 * @param site
	 *            Id of the site assigned by Adserver
	 * @return
	 */
	public AdserverRequest setSite(Integer site) {
		if((site != null)&&(site>0)) {
			synchronized(parameters) {
				parameters.put(parameter_site, site.toString());
			}
		}else if((site != null)&&(site<1))
		{
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"site="+site.toString()+" (valid: int>0)");
		}
		return this;
	}

	/**
	 * Optional.
	 * Set the browser user agent of the device making the request.
	 * @param ua
	 * @return
	 */
	public AdserverRequest setUa(String ua) {
		if(ua != null) {
			synchronized(parameters) {
				parameters.put(parameter_userAgent, ua);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Keywords to search ad delimited by commas.
	 * @param keywords
	 * @return
	 */
	public AdserverRequest setKeywords(String keywords) {
		if(keywords != null) {
			synchronized(parameters) {
				parameters.put(parameter_keywords, keywords);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Filter by premium (PREMIUM_STATUS_NON_PREMIUM - non-premium, 
	 * PREMIUM_STATUS_PREMIUM - premium only, PREMIUM_STATUS_BOTH - both). 
	 * Can be used only by premium publishers.
	 * @param premium
	 * @return
	 */
	public AdserverRequest setPremium(Integer premium) {
		if(premium != null) {
			switch(premium)
			{
			case 0: case 1: case 2:
				synchronized(parameters) {
					parameters.put(parameter_premium, String.valueOf(premium));
				}
				break;
				default:
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"premium="+premium.toString()+"  (valid: 0 - non-premium, 1 - premium only, 2 - both)");
			};
			
			
		}
		return this;	
	}

	/**
	 * Required.
	 * Set the id of the zone of publisher site.
	 * @param zone
	 * @return
	 */
	public AdserverRequest setZone(Integer zone) {
		if((zone != null)&&(zone>0)) {
			synchronized(parameters) {
				parameters.put(parameter_zone, zone.toString());
			}
		}else if((zone != null)&&(zone<1))
		{
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"zone="+zone.toString()+" (valid: int>0)");
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Default setting is test mode where, if the ad code is properly installed, 
	 * the ad response is "Test MODE".
	 * @param enabled
	 * @return
	 */
	public AdserverRequest setTestModeEnabled(Boolean enabled) {
		if(enabled != null) {
			synchronized(parameters) {
				if(enabled) {
					parameters.put(parameter_test, "1");
				} else {
					parameters.remove(parameter_test);
				}
			}
		}
		return this;
	}
	
	public AdserverRequest setTrack(Boolean value) {
		synchronized(parameters) {
			if(value != null) {
			
				if(value) {
					parameters.put(parameter_track, "1");
				} else {
					parameters.put(parameter_track, "0");
				}			
			}else parameters.remove(parameter_track);
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Quantity of ads, returned by a server. Maximum value is 5.
	 * @param count
	 * @return
	 */
	public AdserverRequest setCount(Integer count) {
		if(count != null) {
			synchronized(parameters) {
				parameters.put(parameter_count, String.valueOf(count));
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Country of visitor. See codes here (http://www.mojiva.com/docs/iso3166.csv). 
	 * Will override country detected by IP. 
	 * @param country
	 * @return
	 */
	public AdserverRequest setCountry(String country) {
		if(country != null) {
			synchronized(parameters) {
				parameters.put(parameter_country, country);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Region of visitor. See codes for US and Canada here (http://www.mojiva.com/docs/iso3166_2.csv), 
	 * others - here (http://www.mojiva.com/docs/fips10_4.csv). 
	 * @param region
	 * @return
	 */
	public AdserverRequest setRegion(String region) {
		if(region != null) {
			synchronized(parameters) {
				parameters.put(parameter_region, region);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set City of the device user (with state). For US only. 
	 * @param city
	 * @return
	 */
	public AdserverRequest setCity(String city) {
		if(city != null) {
			synchronized(parameters) {
				parameters.put(parameter_city, city);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Area code of a user. For US only. 
	 * @param area
	 * @return
	 */
	public AdserverRequest setArea(String area) {
		if(area != null) {
			synchronized(parameters) {
				parameters.put(parameter_area, area);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Metro code of a user. For US only.
	 * NOTE: Deprecated, use setDma instead.
	 *  
	 * @param metro
	 * @return
	 */
	@Deprecated
	public AdserverRequest setMetro(String metro) {
		if(metro != null) {
			synchronized(parameters) {
				parameters.put(parameter_dma, metro);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Dma code of a user. For US only.
	 *  
	 * @param marketting zone name
	 * @return
	 */
	public AdserverRequest setDma(String dma) {
		if(dma != null) {
			synchronized(parameters) {
				parameters.put(parameter_dma, dma);
			}
		}
		return this;
	}
	
	
	/**
	 * Optional.
	 * Set Zip/Postal code of user. For US only. 
	 * @param zip
	 * @return
	 */
	public AdserverRequest setZip(String zip) {
		if(zip != null) {
			synchronized(parameters) {
				parameters.put(parameter_zip, zip);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Latitude.
	 * @param latitude
	 * @return
	 */
	public AdserverRequest setLatitude(String latitude) {
		if(latitude != null) {
			double lon = -1000;
			try
			{
				lon = Double.parseDouble(latitude);
			}catch(Exception e)
			{
				
			}
			
			if((lon>=-90)&&(lon<=90))
				synchronized(parameters) {
					parameters.put(parameter_latitude, latitude);
				}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"latitude="+latitude+"  (valid: -90<=double<=90)");	
		}
		/*if(latitude != null) {
			synchronized(parameters) {
				parameters.put(parameter_latitude, latitude);
			}
		}*/
		return this;
	}

	/**
	 * Optional.
	 * Set Longitude.
	 * @param longitude
	 * @return
	 */
	public AdserverRequest setLongitude(String longitude) {
		if(longitude != null) {
			double lon = -1000;
			try
			{
				lon = Double.parseDouble(longitude);
			}catch(Exception e)
			{
				
			}
			
			if((lon>=-180)&&(lon<=180))
				synchronized(parameters) {
					parameters.put(parameter_longitude, longitude);
				}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"longitude="+longitude+" (valid: -180<=double<=180)");	
		}

		return this;
	}
	
	
	
	/**
	 * Optional.
	 * Set Background color in borders.
	 * @param paramBG
	 * @return
	 */
	public AdserverRequest setParamBG(Integer paramBG) {
		if(paramBG != null) {
			synchronized(parameters) {
				paramBG= paramBG&0xFFFFFF;
				String str = Integer.toHexString(paramBG);
				while(str.length()<6) str="0"+str;
				parameters.put(parameter_background, str);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * Set Text color.
	 * @param paramLINK
	 * @return
	 */
	public AdserverRequest setParamLINK(Integer paramLINK) {
		if(paramLINK != null) {
			synchronized(parameters) {
				paramLINK= paramLINK&0xFFFFFF;
				String str = Integer.toHexString(paramLINK);				
				while(str.length()<6) str="0"+str;
				parameters.put(parameter_link, str);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set Carrier name.
	 * @param carrier
	 * @return
	 */
	public AdserverRequest setCarrier(String carrier) {
		if(carrier != null) {
			synchronized(parameters) {
				parameters.put(parameter_carrier, carrier);
			}
		}
		return this;
	}
	
	/**
	 * Optional.
	 * Set minimum width of advertising. 
	 * @param minSizeX
	 * @return
	 */
	public AdserverRequest setMinSizeX(Integer minSizeX) {
		if(minSizeX != null) {
			if(minSizeX>0)
			synchronized(parameters) {
				parameters.put(parameter_min_size_x, String.valueOf(minSizeX));
			}
			else
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"minSizeX="+minSizeX.toString()+" valid>0");			
		}
		else
		{
			parameters.remove(parameter_min_size_x);
		}
		return this;	
	}

	/**
	 * Optional.
	 * Set minimum height of advertising. 
	 * @param minSizeY
	 * @return
	 */
	public AdserverRequest setMinSizeY(Integer minSizeY) {
		if((minSizeY != null)) {
			if((minSizeY>0))
			synchronized(parameters) {
				parameters.put(parameter_min_size_y, String.valueOf(minSizeY));
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"minSizeY="+minSizeY.toString()+" valid>0");				
		}
		else
		{
			parameters.remove(parameter_min_size_y);
		}
		return this;	
	}

	/**
	 * Type of ads to be returned (1 - text, 2 - image, 4 - richmedia ad). 
	 * You can set different combinations with these values. 
	 * For example, 3 = 1 + 2 (text + image), 7 = 1 + 2 + 4 (text + image + richmedia)
	 * @param type Int ad type, default: 3 (text or image)
	 * @return Reference to update ad server request object
	 */
	public AdserverRequest setType(Integer type) {
		if(type != null) {
			if((type>0)&&(type<8))
			synchronized(parameters) {
				parameters.put(parameter_type, String.valueOf(type));
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"type="+type.toString()+" (valid: 1<=int<=7, 1 - text, 2 - image, 4 - richmedia ad, set combinations as sum of this values)");
		}
		return this;	
	}
	
	/**
	 * Optional.
	 * Set maximum width of advertising. 
	 * @param sizeX
	 * @return
	 */
	public AdserverRequest setSizeX(Integer sizeX) {
		if(sizeX != null) {
			if(sizeX>0)
			synchronized(parameters) {
				if (sizeX < getMinSizeX())
				{
					// 2012-03-11: Aron says don't allow max size < min, log and fix request
					parameters.put(parameter_size_x, parameters.get(parameter_min_size_x));
					 AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeX="+sizeX.toString()+" <minSizeX");
				}
				else
				{
					parameters.put(parameter_size_x, String.valueOf(sizeX));
				}
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeX="+sizeX.toString()+" valid>0");
		}
		return this;	
	}

	/**
	 * Optional.
	 * Set maximum height of advertising. 
	 * @param sizeY
	 * @return
	 */
	public AdserverRequest setSizeY(Integer sizeY) {
		if(sizeY != null) {
			if(sizeY>0)
			synchronized(parameters) {
				if (sizeY < getMinSizeY())
				{
					// 2012-03-11: Aron says don't allow max size < min, log and fix request
					parameters.put(parameter_size_y, parameters.get(parameter_min_size_y));
					 AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeY="+sizeY.toString()+" <minSizeY");
				}
				else
				{
					parameters.put(parameter_size_y, String.valueOf(sizeY));
				}
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, Constants.STR_INVALID_PARAM,"maxSizeY="+sizeY.toString()+" valid>0");
		}
		return this;	
	}

	/**
	 * Optional.
	 * Parameter excampaigns should allow excluding the list of campaigns from the result by ID. 
	 * @param excampaigns
	 * @return
	 */
	public AdserverRequest setExcampaigns(String excampaigns) {
		if((excampaigns != null) && (excampaigns.length() > 0)) {
			synchronized(parameters) {
				parameters.put(parameter_excampaigns, excampaigns);
			}
		}
		return this;	
	}
	
	/**
	 * Optional.
	 * Set SDK version. 
	 * @param version
	 * @return
	 */
	public AdserverRequest setVersion(String version) {
		if(version != null) {
			synchronized(parameters) {
				parameters.put(parameter_version, version);
			}
		}
		return this;
	}

	/**
	 * Optional.
	 * If set to 1, return image size (width and height) in html. 
	 * @param sizeRequired
	 * @return
	 */
	public AdserverRequest setSizeRequired(Integer sizeRequired) {
		if(sizeRequired != null) {
			synchronized(parameters) {
				parameters.put(parameter_size_required, String.valueOf(sizeRequired));
			}
		}
		return this;
	}

	public Integer getSite() {
		synchronized(parameters) {
			return getIntParameter(parameters.get(parameter_site),0);
		}
	}

	public String getUa() {
		synchronized(parameters) {
			return parameters.get(parameter_userAgent);
		}
	}
	
	public String getKeywords() {
		synchronized(parameters) {
			return parameters.get(parameter_keywords);
		}
	}

	public Integer getPremium() {
		synchronized(parameters) {
			String premium = parameters.get(parameter_premium);
			return getIntParameter(premium,MASTAdViewCore.PREMIUM_STATUS_BOTH);
		}
	}
	
	public Integer getTrack() {
		synchronized(parameters) {
			String track = parameters.get(parameter_track);
			return getIntParameter(track,null);
		}
	}

	public Integer getZone() {
		synchronized(parameters) {
			return getIntParameter(parameters.get(parameter_zone),0);
		}
	}
	
	public Boolean getTestModeEnabled() {
		synchronized(parameters) {
			String test = parameters.get(parameter_test);
			if(test != null) {
				if(test.equals("1")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	public Integer getCount() {
		synchronized(parameters) {
			String count = parameters.get(parameter_count);
			return getIntParameter(count,0);
		}
	}

	public String getCountry() {
		synchronized(parameters) {
			return parameters.get(parameter_country);
		}
	}
	
	public String getRegion() {
		synchronized(parameters) {
			return parameters.get(parameter_region);
		}
	}

	public String getCity() {
		synchronized(parameters) {
			return parameters.get(parameter_city);
		}
	}

	public String getArea() {
		synchronized(parameters) {
			return parameters.get(parameter_area);
		}
	}

	@Deprecated
	public String getMetro() {
		synchronized(parameters) {
			return parameters.get(parameter_dma);
		}
	}

	public String getDma() {
		synchronized(parameters) {
			return parameters.get(parameter_dma);
		}
	}
	
	public String getZip() {
		synchronized(parameters) {
			return parameters.get(parameter_zip);
		}
	}
	
	/*public Integer getAdstype() {
		if((parameters!=null) && (parameters.get(parameter_adstype)!=null))
		{
			synchronized(parameters) {
				String adstype = parameters.get(parameter_adstype);
				return getIntParameter(adstype,AdServerViewCore.ADS_TYPE_TEXT_AND_IMAGES);
			}
		}else return new Integer(AdServerViewCore.ADS_TYPE_TEXT_AND_IMAGES);
			
	}*/
	
	public String getLatitude() {
		synchronized(parameters) {
			return parameters.get(parameter_latitude);
		}
	}

	public String getLongitude() {
		synchronized(parameters) {
			return parameters.get(parameter_longitude);
		}
	}

	public int getParamBG() {
		synchronized(parameters) {
			if(parameters.get(parameter_background)==null)
				return	Constants.DEFAULT_COLOR;
			else
				return Integer.decode("#"+parameters.get(parameter_background));
		}
	}

	public Integer getParamLINK() {
		synchronized(parameters) {
			if(parameters.get(parameter_link)==null)
				return	Constants.DEFAULT_COLOR;
			else
			return Integer.decode("#"+parameters.get(parameter_link));
		}
	}
	
	public String getCarrier() {
		synchronized(parameters) {
			return parameters.get(parameter_carrier);
		}
	}

	public Integer getMinSizeX() {
		synchronized(parameters) {
			String minSizeX = parameters.get(parameter_min_size_x);
			return getIntParameter(minSizeX,0);
		}
	}

	public Integer getMinSizeY() {
		synchronized(parameters) {
			String minSizeY = parameters.get(parameter_min_size_y);
			return getIntParameter(minSizeY,0);
		}
	}
	
	public Integer getType() {
		synchronized(parameters) {
			String type = parameters.get(parameter_type);
			return getIntParameter(type, Constants.DEFAULT_AD_TYPE);
		}
	}

	public Integer getSizeX() {
		String sizeX = null;
		
		synchronized(parameters) {
			sizeX = parameters.get(parameter_size_x);
		}
		if (sizeX != null)
		{
			return getIntParameter(sizeX,0);
		}
	
		/*
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return new Integer(metrics.widthPixels);
		*/
		return new Integer(0);
	}

	public Integer getSizeY() {
		String sizeY;
		
		synchronized(parameters) {
			sizeY = parameters.get(parameter_size_y);
		}
		
		if (sizeY != null)
		{
			return getIntParameter(sizeY,0);
		}
		
		/*
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return new Integer(metrics.heightPixels);
		*/
		
		return new Integer(0);
	}
	
	public String getExcampaigns() {
		synchronized(parameters) {
			return parameters.get(parameter_excampaigns);
		}
	}
	
	public String getVersion() {
		synchronized(parameters) {
			return parameters.get(parameter_version);
		}
	}

	public Integer getSizeRequired() {
		synchronized(parameters) {
			String sizeRequired = parameters.get(parameter_size_required);
			return getIntParameter(sizeRequired,0);
		}
	}
	
	/**
	 * Optional.
	 * Set Custom parameters.
	 * @param customParameters
	 * @return
	 */
	public void setCustomParameters(Hashtable<String, String> customParameters) {
		this.customParameters = customParameters;
	}
	
	public Hashtable<String, String> getCustomParameters() {
		return customParameters;
	}

	private Integer getIntParameter(String stringValue, Integer defValue) {
		if(stringValue != null) {
			return Integer.parseInt(stringValue);
		} else {
			return defValue;
		}
	}

	/**
	 * Creates URL with given parameters.
	 * @return
	 * @throws IllegalStateException if all the required parameters are not present.
	 */
	public synchronized String createURL() throws IllegalStateException
	{
		return this.toString(); 
	}

	public String toString() {
		StringBuilder builderToString = new StringBuilder();
		synchronized(this)
		{
			builderToString.append(adserverURL);
		}
		builderToString.append("?key=1");
		builderToString.append(ContentManager.getInstance(null).getAutoDetectParameters());
		synchronized(parameters)
		{
			appendParameters(builderToString, parameters);
		}
		appendParameters(builderToString, customParameters);
		if ((getSizeX()<=0)&&(sizeX>-1))
			builderToString.append("&"+parameter_size_x+"="+String.valueOf(sizeX));
		if ((getSizeY()<=0)&&(sizeY>-1))
			builderToString.append("&"+parameter_size_y+"="+String.valueOf(sizeY));
		builderToString.append("&"+parameter_Ad_Call_Timeout+"="+String.valueOf(timeout));
		return  builderToString.toString();//builderToString.toString().equals(adserverURL) ?  this.adserverURL : builderToString.toString();
	}

	private void appendParameters(StringBuilder builderToString, Map<String, String> parameters) {
		if(parameters != null) {
			Set<String> keySet = parameters.keySet();
			
			for (Iterator<String> parameterNames = keySet.iterator(); parameterNames.hasNext();) {
				String param = parameterNames.next();
				String value = parameters.get(param);
	
				if(value != null) {
					builderToString.append("&" + URLEncoder.encode(param) + "=");// + URLEncoder.encode(value));
					if (param.equals(parameter_background)||param.equals(parameter_link))
						builderToString.append("%23"+  URLEncoder.encode(value.toUpperCase()));
					else
						builderToString.append(URLEncoder.encode(value));
							
				}
			}
		}
	}
}
