package com.adserver.adview;

import java.security.SignedObject;
import java.util.Hashtable;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.UserDictionary.Words;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
	//private AutoDetectParametersThread autoDetectParametersThread;
	
		/**
	 * Creation of viewer of advertising.
	 * @param context - The reference to the context of Activity.
	 * @param site - The id of the publisher site.
	 * @param zone - The id of the zone of publisher site.
	 */
	public AdServerView(Context context, Integer site, Integer zone) {
		super(context, site, zone);
		//DetectParameters(context);
		//initialize(context);		
	}
	
	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AdServerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//DetectParameters(context);
		//initialize(context);
	}

	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 * @param attrs
	 */
	public AdServerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		AutoDetectParameters(context);
		//initialize(context);
	}

	/**
	 * Creation of advanced viewer of advertising. It is used for element creation in a XML template.
	 * @param context
	 */
	public AdServerView(Context context) {
		super(context);
		//DetectParameters(context);
		//initialize(context);
	}
	
	/*private void initialize(Context context) {
		autoDetectParametersThread = new AutoDetectParametersThread(context, this, adserverRequest);
	}*/
	
	@Override	
	protected void onAttachedToWindow() {
		adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "AttachedToWindow", "");
		/*if((autoDetectParametersThread != null) 
				&& (autoDetectParametersThread.getState().equals(Thread.State.NEW))) { 
			autoDetectParametersThread.start();
		}*/
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "DetachedFromWindow", "");
		

		/*if(autoDetectParametersThread != null) {
			try {
				autoDetectParametersThread.interrupt();
			} catch (Exception e) {
			}
		}*/

		super.onDetachedFromWindow();
	}
	
	/*private class AutoDetectParametersThread extends Thread {
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
		}
	}*/
	
	@Override
	void AutoDetectParameters(Context context) {
		super.AutoDetectParameters(context);
		AdServerViewCore adserverView = this;
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
						adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "AutoDetectParameters.SDK_VERSION", version);
					} catch (Exception e) {
						adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "SDK_VERSION", e.getMessage());
					}
				} else {
					adserverRequest.setVersion(autoDetectParameters.getVersion());
					adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "AutoDetectParameters.SDK_VERSION", autoDetectParameters.getVersion());
				}
			}
			
			/*if((adserverRequest.getLatitude() == null) || (adserverRequest.getLongitude() == null)) {
				if((autoDetectParameters.getLatitude() == null) || (autoDetectParameters.getLongitude() == null)) {
			    	int isAccessFineLocation = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			    	
			    	if(isAccessFineLocation == PackageManager.PERMISSION_GRANTED) {
						locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
						boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
						if(isGpsEnabled) {
							listener = new WhereamiLocationListener(locationManager, autoDetectParameters); 
							locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener, Looper.getMainLooper());
						}else
						{
							adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_WARNING, "AutoDetectParameters.Gps", "not avalable");
						}
			    	}else adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_WARNING, "AutoDetectParameters.Gps", "no permission ACCESS_FINE_LOCATION");
				} else {
					adserverRequest.setLatitude(autoDetectParameters.getLatitude());
					adserverRequest.setLongitude(autoDetectParameters.getLongitude());
					adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_WARNING, "AutoDetectParameters.Gps=", "("+autoDetectParameters.getLatitude()+";"+autoDetectParameters.getLongitude()+")");
				}
			}
*/
			if(adserverRequest.getPremium() == null) {
				adserverRequest.setPremium(2);					
			}
			/*if(adserverRequest.getCarrier() == null) {
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
			}*/

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
	
	/*private class WhereamiLocationListener implements LocationListener {
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
				adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "GPSLocationChanged=", "("+autoDetectParameters.getLatitude()+";"+autoDetectParameters.getLongitude()+")");
				
    		} catch (Exception e) {
    			adLog.log(AdLog.LOG_LEVEL_2,AdLog.LOG_TYPE_ERROR,"GPSLocationChanged",e.getMessage());
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
	}*/

	Bitmap image;
	Canvas c;
	Paint paint;
	Matrix matrix;
	Paint clear;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		if((getBackgroundColor()==0) &&(image!=null) &&
				(ev.getX()>=0) && (ev.getX()<image.getWidth()) &&
				(ev.getY()>=0) && (ev.getY()<image.getHeight())) 
		{
			int  color = image.getPixel((int)ev.getX(), (int)ev.getY());
			if (Color.alpha(color)>0)
			{
				return super.onTouchEvent(ev);
			} return false;
		}else return super.onTouchEvent(ev);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if((getBackgroundColor()==0)&&(getWidth()>0)&&(getHeight()>0))
		{
			if((image==null)||(image.getWidth() != getWidth())||(image.getHeight() != getHeight())) 
			{
				image = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
				c = new Canvas(image);
				paint = new Paint();						
				matrix = new Matrix();
				
				clear = new Paint();
		        clear.setColor(Color.TRANSPARENT);
		        clear.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			}
			
	        c.drawPaint(clear);

			super.onDraw(c);
			canvas.drawBitmap(image, matrix, paint);
		} else super.onDraw(canvas);		
	}
}
