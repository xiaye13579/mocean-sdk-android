
package com.adserver.adview.ormma;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.adserver.adview.MASTAdServerViewCore;

public class OrmmaUtilityController extends OrmmaController {

	static HashMap<String, Boolean> mFeatureMap = null;

	public OrmmaUtilityController(MASTAdServerViewCore adView, Context context) {
		super(adView, context);
		setFeatureMap();
	}

	private synchronized void setFeatureMap() {
		if(mFeatureMap == null) {
			mFeatureMap = new HashMap<String, Boolean>();

			mFeatureMap.put("level-1", true);
			mFeatureMap.put("level-2", true);
			mFeatureMap.put("level-3", true);
			
			mFeatureMap.put("network", true);
			mFeatureMap.put("orientation", true);
			mFeatureMap.put("screen", true);

			boolean p = (mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
					|| (mContext.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
			mFeatureMap.put("location", p);

			boolean hasMagneticField = OrmmaSensorController.hasMagneticField(mContext);
			boolean hasAccelerometer = OrmmaSensorController.hasAccelerometer(mContext);
			mFeatureMap.put("heading", (hasMagneticField && hasAccelerometer));
			mFeatureMap.put("shake", hasAccelerometer);
			mFeatureMap.put("tilt", hasAccelerometer);

			p = mContext.checkCallingOrSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
			mFeatureMap.put("sms", p);

			p = mContext.checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
			mFeatureMap.put("phone", p);

			p = ((mContext.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) &&
				(mContext.checkCallingOrSelfPermission(android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED));
			mFeatureMap.put("calendar", p);

			mFeatureMap.put("email", true);

			p = mContext.checkCallingOrSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
			mFeatureMap.put("camera", p);
			
			//additional features : playVideo, playAudio, openMap
			mFeatureMap.put("video", true);
			mFeatureMap.put("audio", true);
			mFeatureMap.put("map", 	 true);
		}
	}

	public void eventAdded(String event)
	{
		mOrmmaView.ormmaEvent("service", "name="+event+";action=add");
	}
	
	public void eventRemoved(String event)
	{
		mOrmmaView.ormmaEvent("service", "name="+event+";action=remove");
	}
	
	public boolean supports(String feature) {
		return (mFeatureMap.containsKey(feature))? mFeatureMap.get(feature): false;
	}

	public void sendSMS(String recipient, String body) {
		if(supports("sms")) {
			mOrmmaView.ormmaEvent("sms", "recipient="+recipient+";body="+body);
			try {
	            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
	            if(sdkVersion < Build.VERSION_CODES.DONUT) {
	            	Method getDef = Class.forName("android.telephony.gsm.SmsManager").getMethod(
	            			"getDefault", (Class[])null);
	            	Object sm = getDef.invoke(null, (Object[])null);

	            	Method sendSMS = Class.forName("android.telephony.gsm.SmsManager").getMethod(
	            			"sendTextMessage", new Class[]{String.class, String.class, 
	            					String.class, PendingIntent.class, PendingIntent.class});
	            	sendSMS.invoke(sm, new Object[]{recipient, null, body, null, null});
	            } else {
	            	Method getDef = Class.forName("android.telephony.SmsManager").getMethod(
	            			"getDefault", (Class[])null);
	            	Object sm = getDef.invoke(null, (Object[])null);

	            	Method sendSMS = Class.forName("android.telephony.SmsManager").getMethod(
	            			"sendTextMessage", new Class[]{String.class, String.class, 
	            					String.class, PendingIntent.class, PendingIntent.class});
	            	sendSMS.invoke(sm, new Object[]{recipient, null, body, null, null});
	            }
			} catch (Exception e) {
				mOrmmaView.injectJavaScript("Ormma.fireError(\"sendSMS\",\"Internal error\")");
			}
		} else {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"sendSMS\",\"SMS not available\")");
		}
	}

	public boolean getKeyboardState()
	{
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean flag = imm.isAcceptingText();
		return flag ;
	}
	
	public void sendMail(String recipient, String subject, String body) {
		if(supports("email")) {
			try {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("plain/text");
				i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ recipient});
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
				i.putExtra(android.content.Intent.EXTRA_TEXT, body);
				
				mOrmmaView.ormmaEvent("sendMail","recipient="+recipient+";subject="+subject+";body="+body);
				
				mContext.startActivity(i);
			} catch (ActivityNotFoundException e) {
				mOrmmaView.injectJavaScript("Ormma.fireError(\"sendMail\",\"Email client not available\")");
			} catch (Exception e2) {
				mOrmmaView.injectJavaScript("Ormma.fireError(\"sendMail\",\"Internal error\")");
			}
		} else {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"sendMail\",\"Email not available\")");
		}
	}

	private String createTelUrl(String number) {
		if(TextUtils.isEmpty(number)) {
			return null;
		}

		StringBuilder buf = new StringBuilder("tel:");
		buf.append(number);
		return buf.toString();
	}

	public void makeCall(String number) {
		if(supports("phone")) {
			try {
				mOrmmaView.ormmaEvent("makeCall","number="+number);
				String url = createTelUrl(number);
				if(url == null) {
					mOrmmaView.injectJavaScript("Ormma.fireError(\"makeCall\",\"Bad Phone Number\")");
				}
				Intent i = new Intent(Intent.ACTION_CALL, Uri.parse(url.toString()));
				mContext.startActivity(i);
			} catch (Exception e) {
				mOrmmaView.injectJavaScript("Ormma.fireError(\"makeCall\",\"Internal error\")");
			}
		} else {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"makeCall\",\"CALLS not available\")");
		}
	}

	public void createEvent(String date, String title, String body) {
		if(supports("calendar")) {
			try {
				mOrmmaView.ormmaEvent("calendar", "date="+date+";title="+title+";body="+body);
				String[] projection = new String[] { "_id", "name" };
	            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);

				Uri calendars = Uri.parse("content://calendar/calendars");
	            if(sdkVersion >= Build.VERSION_CODES.FROYO) {
					calendars = Uri.parse("content://com.android.calendar/calendars");
	            }
				     
				Cursor managedCursor = ((Activity)mContext).managedQuery(
						calendars, projection, "selected=1", null, null);
				String calId; 
	
				if(managedCursor!= null && managedCursor.moveToFirst()) {
					int idColumn = managedCursor.getColumnIndex("_id");
				    calId = managedCursor.getString(idColumn);
				} else {
					mOrmmaView.injectJavaScript("Ormma.fireError(\"createEvent\",\"Could not find a local calendar\")");
					return;
				}
				
				ContentValues event = new ContentValues();
				event.put("calendar_id", calId);
				event.put("title", title);
				event.put("description", body);
				Long ldate = Long.parseLong(date);
				event.put("dtstart", ldate);
				
				Uri eventsUri = Uri.parse("content://calendar/events");
	            if(sdkVersion >= Build.VERSION_CODES.FROYO) {
					eventsUri = Uri.parse("content://com.android.calendar/events");
	            }
	            
			    mContext.getContentResolver().insert(eventsUri, event);
			} catch (Exception e) {
				mOrmmaView.injectJavaScript("Ormma.fireError(\"createEvent\",\"Internal error\")");
			}
		} else {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"createEvent\",\"Calendar not available\")");
		}
	}
	
}
