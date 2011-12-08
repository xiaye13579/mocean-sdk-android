
package com.adserver.adview.ormma;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.adserver.adview.AdServerViewCore;
import com.adserver.adview.ormma.util.NavigationStringEnum;
import com.adserver.adview.ormma.util.TransitionStringEnum;

public class OrmmaController {

	protected AdServerViewCore mOrmmaView;

	private static final String STRING_TYPE = "class java.lang.String";
	private static final String INT_TYPE = "int";
	private static final String BOOLEAN_TYPE = "boolean";
	private static final String FLOAT_TYPE = "float";
	private static final String NAVIGATION_TYPE = "class com.ormma.NavigationStringEnum";
	private static final String TRANSITION_TYPE = "class com.ormma.TransitionStringEnum";

	public static final String EXIT = "exit";
	public static final String FULL_SCREEN = "fullscreen";
	public static final String STYLE_NORMAL = "normal";	

	protected Context mContext;

	public static class Dimensions extends ReflectedParcelable {

		public Dimensions() {
			x = -1;
			y = -1;
			width = -1;
			height = -1;
		};

		public static final Parcelable.Creator<Dimensions> CREATOR = new Parcelable.Creator<Dimensions>() {
			public Dimensions createFromParcel(Parcel in) {
				return new Dimensions(in);
			}

			public Dimensions[] newArray(int size) {
				return new Dimensions[size];
			}
		};

		protected Dimensions(Parcel in) {
			super(in);
			// TODO Auto-generated constructor stub
		}

		public int x, y, width, height;

	}

	public static class Properties extends ReflectedParcelable {
		protected Properties(Parcel in) {
			super(in);
		}

		public Properties() {
			transition = TransitionStringEnum.DEFAULT;
			navigation = NavigationStringEnum.NONE;
			use_background = false;
			background_color = 0;
			background_opacity = 0;
			is_modal = true;
			width=0;
			height=0;
			useCustomClose = false;
			lockOrientation = true;
		};

		public static final Parcelable.Creator<Properties> CREATOR = new Parcelable.Creator<Properties>() {
			public Properties createFromParcel(Parcel in) {
				return new Properties(in);
			}

			public Properties[] newArray(int size) {
				return new Properties[size];
			}
		};
		public TransitionStringEnum transition;
		public NavigationStringEnum navigation;
		public int width;
		public int height;
		public boolean useCustomClose;
		public boolean lockOrientation;
		public boolean use_background;
		public int background_color;
		public float background_opacity;
		public boolean is_modal;
	}

	public OrmmaController(AdServerViewCore adView, Context context) {
		mOrmmaView = adView;
		mContext = context;
	}

	protected static Object getFromJSON(JSONObject json, Class<?> c) throws IllegalAccessException,
			InstantiationException, NumberFormatException, NullPointerException {
		Field[] fields = null;
		fields = c.getFields();
		Object obj = c.newInstance();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			String name = f.getName();
			String JSONName = name.replace('_', '-');
			Type type = f.getType();
			String typeStr = type.toString();
			try {
				if (typeStr.equals(INT_TYPE)) {
					String value;
					value = json.getString(JSONName);
					int iVal;
					if (value.startsWith("#")) {
						iVal = Integer.parseInt(value.substring(1), 16);
					} else
						iVal = Integer.parseInt(value);

					f.set(obj, iVal);
				} else if (typeStr.equals(STRING_TYPE)) {
					String value = json.getString(JSONName);
					f.set(obj, value);
				} else if (typeStr.equals(BOOLEAN_TYPE)) {
					boolean value = json.getBoolean(JSONName);
					f.set(obj, value);
				} else if (typeStr.equals(FLOAT_TYPE)) {
					float value = Float.parseFloat(json.getString(JSONName));
					f.set(obj, value);
				} else if (typeStr.equals(NAVIGATION_TYPE)) {
					NavigationStringEnum value = NavigationStringEnum.fromString(json.getString(JSONName));
					f.set(obj, value);
				} else if (typeStr.equals(TRANSITION_TYPE)) {
					TransitionStringEnum value = TransitionStringEnum.fromString(json.getString(JSONName));
					f.set(obj, value);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

		}
		return obj;
	}

	/**
	 * The Class ReflectedParcelable.
	 */
	public static class ReflectedParcelable implements Parcelable {

		/**
		 * Instantiates a new reflected parcelable.
		 */
		public ReflectedParcelable() {

		}

		/* (non-Javadoc)
		 * @see android.os.Parcelable#describeContents()
		 */
		@Override
		public int describeContents() {
			return 0;
		}

		/**
		 * Instantiates a new reflected parcelable.
		 *
		 * @param in the in
		 */
		protected ReflectedParcelable(Parcel in) {
			Field[] fields = null;
			Class<?> c = this.getClass();
			fields = c.getDeclaredFields();
			try {
				//Object obj = c.newInstance();
				Object obj = this;
				for (int i = 0; i < fields.length; i++) {
					Field f = fields[i];
					
					Class<?> type = f.getType();
					
					if (type.isEnum()) {
						String typeStr = type.toString();
						if (typeStr.equals(NAVIGATION_TYPE)) {
							f.set(obj, NavigationStringEnum.fromString(in.readString()));
						} else if (typeStr.equals(TRANSITION_TYPE)) {
							f.set(obj, TransitionStringEnum.fromString(in.readString()));
						}
					} else {
						Object dt = f.get(this);
						if( !(dt instanceof Parcelable.Creator<?>)) {
							f.set(obj, in.readValue(null));							
						}
					}
				}
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/* (non-Javadoc)
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel out, int flags1) {
			Field[] fields = null;
			Class<?> c = this.getClass();
			fields = c.getDeclaredFields();
			try {
				for (int i = 0; i < fields.length; i++) {
					Field f = fields[i];
					Class<?> type = f.getType();
										
					if (type.isEnum()) {
						String typeStr = type.toString();
						if (typeStr.equals(NAVIGATION_TYPE)) {
							out.writeString(((NavigationStringEnum) f.get(this)).getText());
						} else if (typeStr.equals(TRANSITION_TYPE)) {
							out.writeString(((TransitionStringEnum) f.get(this)).getText());
						}
					} else {
						Object dt = f.get(this);
						if( !(dt instanceof Parcelable.Creator<?>)) 
								out.writeValue(dt);				
						
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	/**
	 * 
	 * Contains audio and video properties
	 *
	 */
	public static class PlayerProperties extends ReflectedParcelable {
			
		
		public PlayerProperties() {
			autoPlay = showControl = true;
			doLoop = audioMuted = false;
			startStyle = stopStyle = STYLE_NORMAL;
			inline = false;			
		}
		
		/**
		 * The Constant CREATOR.
		 */
		public static final Parcelable.Creator<PlayerProperties> CREATOR = new Parcelable.Creator<PlayerProperties>() {
			public PlayerProperties createFromParcel(Parcel in) {
				return new PlayerProperties(in);
			}

			public PlayerProperties[] newArray(int size) {
				return new PlayerProperties[size];
			}
		};		
		
		public PlayerProperties(Parcel in) {
			super(in);
		}

		/**
		 * Set stop style
		 * @param style - stop style (normal/full screen)
		 */
		public void setStopStyle(String style){
			stopStyle = style;
		}
		

		/**
		 * Set Player properties
		 * @param autoPlay - true if player should start immediately
		 * @param controls - true if player should show controls
		 * @param loop - true if player should start again after finishing
		 */
		public void setProperties(boolean audioMuted, boolean autoPlay, boolean controls, boolean inline,boolean loop, String startStyle, String stopStyle){
			this.autoPlay = autoPlay;
			this.showControl = controls;
			this.doLoop = loop;
			this.audioMuted = audioMuted;
			this.startStyle = startStyle;
			this.stopStyle = stopStyle;
			this.inline = inline;			

		}
		
		/**
		 * Mute Audio
		 */
		public void muteAudio(){
			audioMuted = true;
		}
		
		/**
		 * Get autoPlay
		 * 
		 */
		public boolean isAutoPlay(){
			return (autoPlay == true);
		}
		
		/**
		 * Get show control
		 */
		public boolean showControl(){
			return showControl;
		}
		
		/**
		 * 
		 * Get looping option
		 */
		public boolean doLoop(){
			return doLoop;
		}
		
		/**
		 * Get mute status
		 */
		public boolean doMute(){
			return audioMuted;
		}
		
		/**
		 * 
		 * Get stop style
		 */
		public boolean exitOnComplete(){
			return stopStyle.equalsIgnoreCase(EXIT);
		}
		
		/**
		 * 
		 * Get start style
		 */
		public boolean isFullScreen(){
			return startStyle.equalsIgnoreCase(FULL_SCREEN);
		}		
		
		public boolean autoPlay, showControl, doLoop, audioMuted,inline;
		public String stopStyle, startStyle;
	}
}
