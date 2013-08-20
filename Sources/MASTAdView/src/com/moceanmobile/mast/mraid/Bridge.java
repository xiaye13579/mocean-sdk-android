package com.moceanmobile.mast.mraid;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

public class Bridge 
{
	public final WebView webView;
	public final Handler handler;
	
	public Bridge(WebView webView, Handler handler)
	{
		if (webView == null)
			throw new IllegalArgumentException("webView null");
		
		if (handler == null)
			throw new IllegalArgumentException("handler null");
		
		this.webView = webView;
		this.handler = handler;
	}
	
    public void sendErrorMessage(String message, String action)
    {
        String script = "mraid.fireErrorEvent('" + message + "','" + action + "');";
        webView.injectJavascript(script);
    }
	
	private Consts.PlacementType placementType = Consts.PlacementType.Inline;
	/**
	 * @return Configured bridge placement type.
	 */
	public Consts.PlacementType getPlacementType()
	{
		return placementType;
	}
	
	public void setPlacementType(Consts.PlacementType placementType)
	{
		this.placementType = placementType;
		
		String placementTypeString = Consts.PlacementTypeInline;
		if (placementType == Consts.PlacementType.Interstitial)
			placementTypeString = Consts.PlacementTypeInterstitial;
		
		String script = "mraid.setPlacementType('" + placementTypeString + "');";
		webView.injectJavascript(script);
	}
	
	private Consts.State state = Consts.State.Loading;
	/**
	 * @return Configured bridge state.
	 */
	public Consts.State getState()
	{
		return state;
	}
	
	public void setState(Consts.State state)
	{
		this.state = state;
		
		String stateString = Consts.StateLoading;
		switch (state)
		{
		case Loading:
			stateString = Consts.StateLoading;
			break;
		case Default:
			stateString = Consts.StateDefault;
			break;
		case Hidden:
			stateString = Consts.StateHidden;
			break;
		case Resized:
			stateString = Consts.StateResized;
			break;
		case Expanded:
			stateString = Consts.StateExpanded;
			break;
		}
		
		String script = "mraid.setState('" + stateString + "');";
		webView.injectJavascript(script);
	}
	
    public void setSupportedFeature(Consts.Feature feature, boolean supported)
    {
        String supportedString = Consts.False;
        if (supported)
            supportedString = Consts.True;

        String featureString = null;
        switch (feature)
        {
            case SMS:
                featureString = Consts.FeatureSMS;
                break;
            case Tel:
                featureString = Consts.FeatureTel;
                break;
            case Calendar:
                featureString = Consts.FeatureCalendar;
                break;
            case StorePicture:
                featureString = Consts.FeatureStorePicture;
                break;
            case InlineVideo:
                featureString = Consts.FeatureInlineVideo;
                break;
        }

        if (featureString == null)
            return;

        String script = "mraid.setSupports('" + featureString + "', '" + supportedString + "');";
        webView.injectJavascript(script);
    }
	
	public void sendReady()
	{
		String script = "mraid.fireEvent('" + Consts.EventReady + "');";
		webView.injectJavascript(script);
	}
	
    public void setViewable(boolean viewable)
    {
        String viewableString = Consts.False;
        if (viewable)
            viewableString = Consts.True;

        String script = "mraid.setViewable('" + viewableString + "');";
        webView.injectJavascript(script);
    }
    
    public void setScreenSize(int width, int height)
    {
        String script = "mraid.setScreenSize({width:" + width + ",height:" + height + "});";
        webView.injectJavascript(script);
    }

    public void setMaxSize(int width, int height)
    {
    	String script = "mraid.setMaxSize({width:" + width + ",height:" + height + "});";
        webView.injectJavascript(script);
    }

    public void setCurrentPosition(int x, int y, int width, int height)
    {
    	String script = "mraid.setCurrentPosition({x:" + x + ",y:" + y + ",width:" + width + ",height:" + height + "});";
    	webView.injectJavascript(script);
    }

    public void setDefaultPosition(int x, int y, int width, int height)
    {
    	String script = "mraid.setDefaultPosition({x:" + x + ",y:" + y + ",width:" + width + ",height:" + height + "});";
    	webView.injectJavascript(script);
    }
	
	private ExpandProperties expandProperties = new ExpandProperties();
	/**
	 * To set properties call setExpandProperties() with the same or a new
	 * object as changing members of the returned object will NOT update the
	 * MRAID javascript bridge.
	 * 
	 * @return Configured expand properties.
	 */
	public ExpandProperties getExpandProperties()
	{
		return expandProperties;
	}
	
	// The class member will be set when the javascript bridge calls back a native invoke.
    public void setExpandProperties(ExpandProperties expandProperties)
    {
        String arg = expandProperties.toString();
        String script = "mraid.setExpandProperties(" + arg + ");";
        webView.injectJavascript(script);
    }
	
	private OrientationProperties orientationProperties = new OrientationProperties();
	/**
	 * To set properties call setOrientationProperties() with the same or a new
	 * object as changing members of the returned object will NOT update the
	 * MRAID javascript bridge.
	 * 
	 * @return Configured orientation properties.
	 */
	public OrientationProperties getOrientationProperties()
	{
		return orientationProperties;
	}
	
	// The class member will be set when the javascript bridge calls back a native invoke.
    public void setOrientationProperties(OrientationProperties orientationProperties)
    {
        String arg = orientationProperties.toString();
        String script = "mraid.setOrientationProperties(" + arg + ");";
        webView.injectJavascript(script);
    }
	
	private ResizeProperties resizeProperties = new ResizeProperties();
	/**
	 * To set properties call setResizeProperties() with the same or a new
	 * object as changing members of the returned object will NOT update the
	 * MRAID javascript bridge.
	 * 
	 * @return Configured resize properties.
	 */
	public ResizeProperties getResizeProperties()
	{
		return resizeProperties;
	}
	
	// The class member will be set when the javascript bridge calls back a native invoke.
    public void setResizeProperties(ResizeProperties resizeProperties)
    {
        String arg = resizeProperties.toString();
        String script = "mraid.setResizeProperties(" + arg + ");";
        webView.injectJavascript(script);
    }
    
    public void sendPictureAdded(boolean success)
    {
        String successString = Consts.False;
        if (success)
            successString = Consts.True;

        String script = "mraid.firePictureAddedEvent('" + successString + "');";
        webView.injectJavascript(script);
    }
	
	/**
	 * Called from a WebView implementation.
	 * Not expected to be called from the SDK or application.
	 * 
	 * @param invoke URI from the MRAID Javascript with an encoded command and arguments.
	 */
	@JavascriptInterface
	public void nativeInvoke(String invoke)
	{
		if (TextUtils.isEmpty(invoke))
			return;
		
		if (invoke.startsWith("console"))
		{
			//TODO: Log javascript console messages.
			return;
		}
		
		URI uri = null;
		try
		{
			uri = new URI(invoke);
		} 
		catch (URISyntaxException e)
		{
			// TODO: Log error as this shouldn't happen with 'mraid' invokes.
			return;
		}
		
		String scheme = uri.getScheme().toLowerCase(Locale.US);
		
		if (Consts.Scheme.equals(scheme))
		{
			String command = uri.getHost().toLowerCase(Locale.US);
			String query = uri.getRawQuery();
			
			Map<String, String> args = new HashMap<String, String>(10);
			
			if (query != null)
			{
				try
				{
					String[] queryItems = query.split("\\&");
					for (String queryItem : queryItems)
					{
						String[] queryItemParts = queryItem.split("\\=");
						if (queryItemParts.length == 2)
						{
							String key = URLDecoder.decode(queryItemParts[0], "UTF-8");
							String value = URLDecoder.decode(queryItemParts[1], "UTF-8");
							args.put(key, value);
						}
					}
				}
				catch (Exception ex) {}
			}
			
			if (Consts.CommandClose.equals(command))
			{
				handler.mraidClose(this);
			}
			else if (Consts.CommandOpen.equals(command))
			{
				String url = args.get(Consts.CommandArgUrl);
				
				handler.mraidOpen(this, url);
			}
			else if (Consts.CommandUpdateCurrentPosition.equals(command))
			{
				handler.mraidUpdateCurrentPosition(this);
			}
			else if (Consts.CommandExpand.equals(command))
			{
				String url = args.get(Consts.CommandArgUrl);

				handler.mraidExpand(this, url);
			}
			
			else if (Consts.CommandSetExpandProperties.equals(command))
			{
                ExpandProperties expandProperties = ExpandProperties.propertiesFromArgs(args);
                this.expandProperties = expandProperties;
                
                handler.mraidUpdatedExpandProperties(this);
			}
			else if (Consts.CommandSetOrientationProperties.equals(command))
			{
                OrientationProperties orientationProperties = OrientationProperties.propertiesFromArgs(args);
                this.orientationProperties = orientationProperties;
                
                handler.mraidUpdatedOrientationProperties(this);
			}
			else if (Consts.CommandResize.equals(command))
			{
				handler.mraidResize(this);
			}
			else if (Consts.CommandSetResizeProperties.equals(command))
			{
                ResizeProperties resizeProperties = ResizeProperties.propertiesFromArgs(args);
                this.resizeProperties = resizeProperties;
                
                handler.mraidUpdatedResizeProperties(this);
			}
			else if (Consts.CommandPlayVideo.equals(command))
			{
				String url = args.get(Consts.CommandArgUrl);
				
				handler.mraidPlayVideo(this, url);
			}
			else if (Consts.CommandCreateCalendarEvent.equals(command))
			{
				handler.mraidCreateCalendarEvent(this, args.get(Consts.CommandArgEvent));
			}
			else if (Consts.CommandStorePicture.equals(command))
			{
				String url = args.get(Consts.CommandArgUrl);
				
				handler.mraidStorePicture(this, url);
			}
		}
	}
	
	public interface Handler
	{
        public void mraidClose(Bridge bridge);
        public void mraidOpen(Bridge bridge, String url);
        public void mraidUpdateCurrentPosition(Bridge bridge);
        public void mraidUpdatedExpandProperties(Bridge bridge);
        public void mraidExpand(Bridge bridge, String url);
        public void mraidUpdatedOrientationProperties(Bridge bridge);
        public void mraidUpdatedResizeProperties(Bridge bridge);
        public void mraidResize(Bridge bridge);
        public void mraidPlayVideo(Bridge bridge, String url);
        public void mraidCreateCalendarEvent(Bridge bridge, String calendarEvent);
        public void mraidStorePicture(Bridge bridge, String url);
	}
}
