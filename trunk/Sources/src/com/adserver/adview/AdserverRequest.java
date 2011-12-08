package com.adserver.adview;

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

public class AdserverRequest {
	
	public static String INVALID_PARAM_TITLE = "invalid param";
	
	
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
	private final String parameter_connection_speed = "connection_speed";
	private final String parameter_size_required = "size_required";
	private final String parameter_mcc = "mcc";
	private final String parameter_mnc = "mnc";
	private final String parameter_type = "type";
	//private final String parameter_debug = "debug";
	public final static String parameter_device_id = "udid";
	
	private String adserverURL = "http://ads.mocean.mobi/ad"; 
	
	private Hashtable<String, String> customParameters;
	
	AdLog AdLog;
	
	public AdserverRequest(AdLog AdLog) {
		this.AdLog = AdLog;
		setPremium(AdServerViewCore.PREMIUM_STATUS_BOTH);
	}

	/*public AdserverRequest(Integer site, Integer zone) {
		setSite(site);
		setZone(zone);		
	}*/
	
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";
    public synchronized static String id(Context context) {
    	if (sID == null) {
    		File installation = new File(context.getFilesDir(), INSTALLATION);
    		try {                
    			if (!installation.exists())
    				writeInstallationFile(installation);
    			sID = readInstallationFile(installation);
    			} catch (Exception e) {
    				//throw new RuntimeException(e);
    				sID="1234567890";
    				}
    			}
    	return sID;
    }
    private static String readInstallationFile(File installation) throws IOException {
    	RandomAccessFile f = new RandomAccessFile(installation, "r");
    	byte[] bytes = new byte[(int) f.length()];
    	f.readFully(bytes);
    	f.close();
    	return new String(bytes);
    }
    private static void writeInstallationFile(File installation) throws IOException {
    	FileOutputStream out = new FileOutputStream(installation);
    	String id = UUID.randomUUID().toString();
    	out.write(id.getBytes());
    	out.close();
    }
	
	void InitDefaultParameters(Context context)
	{
		String deviceId;
		
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String temp = tm.getDeviceId();
		if (null !=  temp) deviceId = temp;
		else {
			temp = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID); ;
			if (null != temp) deviceId = temp;
			else deviceId = "";
		}
		
		if(deviceId==null)
		{
			AdLog.log(AdLog.LOG_LEVEL_2,AdLog.LOG_TYPE_WARNING,"getDeviceId","not avalable");
			deviceId = id(context);
		}
		String deviceIdMD5 = Utils.md5(deviceId);
		AdLog.log(AdLog.LOG_LEVEL_2,AdLog.LOG_TYPE_INFO,"deviceIdMD5",deviceIdMD5);
		
		if((deviceIdMD5 != null) && (deviceIdMD5.length() > 0)) {
			parameters.put(parameter_device_id, deviceIdMD5);			
		}
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
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"site="+site.toString()+" (valid: int>0)");
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
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"premium="+premium.toString()+"  (valid: 0 - non-premium, 1 - premium only, 2 - both)");
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
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"zone="+zone.toString()+" (valid: int>0)");
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
	 * @param metro
	 * @return
	 */
	public AdserverRequest setMetro(String metro) {
		if(metro != null) {
			synchronized(parameters) {
				parameters.put(parameter_metro, metro);
			}
		}
		return this;
	}
	
	public AdserverRequest setMCC(String mcc) {
		if(mcc != null) {
			synchronized(parameters) {
				parameters.put(parameter_mcc, mcc);
			}
		}
		return this;
	}
	
	public AdserverRequest setMNC(String mnc) {
		if(mnc != null) {
			synchronized(parameters) {
				parameters.put(parameter_mnc, mnc);
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
	 * Set Type of advertisement (ADS_TYPE_TEXT_ONLY - text only, 
	 * ADS_TYPE_IMAGES_ONLY - image only, ADS_TYPE_TEXT_AND_IMAGES - image and text, 
	 * ADS_TYPE_SMS - SMS ad). SMS will be returned in XML.
	 * @param adstype
	 * @return
	 */
	/*public AdserverRequest setAdstype(Integer adstype) {
		if(adstype != null) {
			switch(adstype)
			{
			case 1:case 2:case 3:case 6:
			synchronized(parameters) {
				parameters.put(parameter_adstype, String.valueOf(adstype));
			}
			break;
			default:
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, INVALID_PARAM_TITLE,"adstype="+adstype.toString()+" (valid: 1;2;3;6)");
			}
		}
		return this;
	}*/
	
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
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"latitude="+latitude+"  (valid: -90<=double<=90)");	
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
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"longitude="+longitude+" (valid: -180<=double<=180)");	
		}
		/*if((minSizeY != null)) {
			if((minSizeY>0))
			synchronized(parameters) {
				parameters.put(parameter_min_size_y, String.valueOf(minSizeY));
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, INVALID_PARAM_TITLE,"minSizeY="+minSizeY.toString()+" valid>0");			
			
		}*/
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
			AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"minSizeX="+minSizeX.toString()+" valid>0");			
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
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"minSizeY="+minSizeY.toString()+" valid>0");			
			
		}
		return this;	
	}

	public AdserverRequest setType(Integer type) {
		if(type != null) {
			if((type>0)&&(type<8))
			synchronized(parameters) {
				parameters.put(parameter_type, String.valueOf(type));
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"type="+type.toString()+" (valid: 1<=int<=7, 1 - text, 2 - image, 4 - richmedia ad, set combinations as sum of this values)");
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
				parameters.put(parameter_size_x, String.valueOf(sizeX));
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"maxSizeX="+sizeX.toString()+" valid>0");
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
				parameters.put(parameter_size_y, String.valueOf(sizeY));
			}
			else
				AdLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, INVALID_PARAM_TITLE,"maxSizeY="+sizeY.toString()+" valid>0");
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
	 * Set connection speed. 0 - low (gprs, edge), 1 - fast (3g, wifi). 
	 * @param connectionSpeed
	 * @return
	 */
	public AdserverRequest setConnectionSpeed(Integer connectionSpeed) {
		if(connectionSpeed != null) {
			synchronized(parameters) {
				parameters.put(parameter_connection_speed, String.valueOf(connectionSpeed));
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
			return getIntParameter(premium,AdServerViewCore.PREMIUM_STATUS_BOTH);
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

	public String getMetro() {
		synchronized(parameters) {
			return parameters.get(parameter_metro);
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
			return getIntParameter(type,3);
		}
	}

	public Integer getSizeX() {
		synchronized(parameters) {
			String sizeX = parameters.get(parameter_size_x);
			return getIntParameter(sizeX,0);
		}
	}

	public Integer getSizeY() {
		synchronized(parameters) {
			String sizeY = parameters.get(parameter_size_y);
			return getIntParameter(sizeY,0);
		}
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

	public Integer getConnectionSpeed() {
		synchronized(parameters) {
			String connectionSpeed = parameters.get(parameter_connection_speed);
			return getIntParameter(connectionSpeed,null);
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

	public synchronized String toString() {
		StringBuilder builderToString = new StringBuilder();
		String adserverURL = this.adserverURL+"?key=1";		
		builderToString.append(adserverURL);
		appendParameters(builderToString, parameters);
		appendParameters(builderToString, customParameters);
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
