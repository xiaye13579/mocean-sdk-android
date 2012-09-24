//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.MASTAdView.MASTAdConstants;

public class AdParser
{
	// XML tags we key off of
	public static final String TAG_AD 			= "ad";
	public static final String TAG_URL 			= "url";
	public static final String TAG_TEXT 		= "text";
	public static final String TAG_IMG 			= "img";
	public static final String TAG_TRACK 		= "track";
	public static final String TAG_CONTENT 		= "content";
	//public static final String TAG_RESPONSE 	= "response";
	public static final String TAG_CAMPAIGN_TYPE = "type";
	public static final String TAG_CAMPAIGN_ID	= "campaign_id";
	public static final String TAG_PARAM		= "param";
	public static final String TAG_TRACK_URL	= "track_url";
	
	
	// XML attributes for various tags
	public static final String ATTRIBUTE_AD_TYPE = "type";
	public static final String ATTRIBUTE_AD_FEED = "feed";
	public static final String ATTRIBUTE_EXTERNAL_CAMPAIGN_VARIABLE_NAME = "name";

	
	// Constants for external third party campaign
	public static final String EXTERNAL_THIRD_PARTY_CAMPAIGN_SIGNAL = "client_side_external_campaign";
	public static final String EXTERNAL_THIRD_PARTY_CAMPAIGN_START	= "<external_campaign";
	public static final String EXTERNAL_THIRD_PARTY_CAMPAIGN_END	= "</external_campaign>";
	
	
	// Setup for parsing ad data from server
	public AdData parseAdData(String adContent)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
        try 
        {
            SAXParser parser = factory.newSAXParser();
            AdHandler handler = new AdHandler();
            System.out.println("Starting parse of ad XML data: " + adContent);
            //parser.parse(adContent, handler);
            parser.parse(new ByteArrayInputStream(adContent.getBytes()), handler);
            
            // For third party ads, apply extra logic for ad type handling:
            // if the content contains a "<script" tag, set it to rich media;
            // otherwise, if it has an image, set type to image;
            // otherwise, if it has text, set type to text;
            // otherwise, treat it as rich media.
            AdData ad = handler.getAd();
            if (ad.adType == MASTAdConstants.AD_TYPE_THIRDPARTY)
            {
            	if ((ad.richContent != null) && (ad.richContent.contains("<script")))
    			{
            		ad.adType = MASTAdConstants.AD_TYPE_RICHMEDIA;
    			}
            	else if ((ad.imageUrl != null) && (ad.imageUrl.length() > 0))
            	{
            		ad.adType = MASTAdConstants.AD_TYPE_IMAGE;
            	}
            	else if ((ad.text != null) && (ad.text.length() > 0))
            	{
            		ad.adType = MASTAdConstants.AD_TYPE_TEXT;
            	}
            	else
            	{
            		ad.adType = MASTAdConstants.AD_TYPE_RICHMEDIA;
            	}
            }
            
            return ad;
        }
        catch (Exception e)
        {
        	System.out.println("Exception parsing ad data: " + e.getMessage());
            //throw new RuntimeException(e);
        	AdData errorAd = new AdData();
        	errorAd.error = "Error parsing ad data: " + e.getMessage();
        	return errorAd;
        } 
	}
	
	
	// Setup for parsing external third party ad campaign
	public void parseExternalCampaignProperties(AdData ad, String campaignContent)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
        try 
        {
            SAXParser parser = factory.newSAXParser();
            ExternalCampaignHandler handler = new ExternalCampaignHandler(ad);
            System.out.println("Starting parse of external campaign XML data: " + campaignContent);
            //parser.parse(adContent, handler);
            parser.parse(new ByteArrayInputStream(campaignContent.getBytes()), handler);
        }
        catch (Exception e)
        {
        	System.out.println("Exception parsing external campaign data: " + e.getMessage());
            //throw new RuntimeException(e);
        	ad.error = "Error parsing external campaign data: " + e.getMessage();
        } 
	}
	
	
	// Parse ad XML data, which has various content depending on ad type, bus has the following general structure:
	// <?xml version="1.0" encoding="UTF-8" ?> 
	// <mojiva>
	// <ad type="text, image, richmedia or thirdparty" feed="..third party feed type, if any..">
	// <url>   <![CDATA[ ..click through url.. ]]> </url>
	// <text>  <![CDATA[ ..content for text ad, if any.. ]]> </text>
	// <img>   <![CDATA[ ..image url, if any.. ]]> </img>
	// <track> <![CDATA[ ..tracking url, if any.. ]]> </track>
	// <content> ..rich media content, if any.. </content> 
	// <response> ..orignal response from mediated server, etc.. </response> 
	// </ad>
	// </mojiva>
	private class AdHandler extends DefaultHandler
	{
		private AdData currentAd;
	    private StringBuilder sb;

	    public AdHandler()
	    {
	    	currentAd = new AdData();
	    	sb = new StringBuilder();
	    }
	    
	    public AdData getAd()
	    {
	    	System.out.println("AdParser.getAd()");
	    	/*
	    	System.out.println("ad type: " + currentAd.getAdTypeName());
	    	if (currentAd.adType == MASTAdConstants.AD_TYPE_THIRDPARTY)
	    	{
	    		if (currentAd.thirdPartyFeed != null)
	    		{
	    			System.out.println("third party feed: " + currentAd.thirdPartyFeed);
	    		}
	    	}
	    	if (currentAd.clickUrl != null)
	    	{
	    		System.out.println("click url: " + currentAd.clickUrl);
	    	}
	    	if (currentAd.text != null)
	    	{
	    		System.out.println("text: " + currentAd.text);
	    	}
	    	if (currentAd.imageUrl != null)
	    	{
	    		System.out.println("image url: " + currentAd.imageUrl);
	    	}
	    	if (currentAd.richContent != null)
	    	{
	    		System.out.println("content: " + currentAd.richContent);
	    	}
	    	if (currentAd.error != null)
	    	{
	    		System.out.println("error: " + currentAd.error);
	    	}
	    	*/
	    	
	    	return currentAd;
	    }
	    
	    @Override
	    public void characters(char[] ch, int start, int length) throws SAXException
	    {
	        super.characters(ch, start, length);
	        sb.append(ch, start, length);
	    }

	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	    {
	    	if (localName.equalsIgnoreCase(TAG_AD) &&
    			(attributes != null) && (attributes.getLength() > 0))
            {
	    		String value = attributes.getValue(uri, ATTRIBUTE_AD_TYPE);
	    		if (value != null)
	    		{
	    			currentAd.setAdTypeByName(value);
	    		}
        	
	    		value = attributes.getValue(uri, ATTRIBUTE_AD_FEED);
	    		if (value != null)
	    		{
	    			currentAd.thirdPartyFeed = value;
	    		}
            }
	    }
	    
	    @Override
	    public void endElement(String uri, String localName, String name) throws SAXException
	    {
	        super.endElement(uri, localName, name);

            if (localName.equalsIgnoreCase(TAG_URL))
            {
                currentAd.clickUrl = sb.toString();
            }
            else if (localName.equalsIgnoreCase(TAG_TRACK))
            {
                currentAd.trackUrl = sb.toString();
            }
            else if (localName.equalsIgnoreCase(TAG_TEXT))
            {
                currentAd.text = sb.toString();
            }
            else if (localName.equalsIgnoreCase(TAG_IMG))
            {
                currentAd.setImage(sb.toString());
            }
            else if (localName.equalsIgnoreCase(TAG_CONTENT))
            {
            	String externalCampaignStanza = extractExternalCampaignStanza(sb);
            	if (externalCampaignStanza != null)
            	{
            		parseExternalCampaignProperties(currentAd, externalCampaignStanza);
            		currentAd.setAdTypeByName(AdData.typeNameExternalThirdParty);
            	}
            	else
            	{
            		currentAd.richContent = sb.toString();
            	}
            }
            
            sb.setLength(0);    
	    }
	}
	

	// Parse external campaign XML data, such as the following example:
	// <external_campaign version="1.0"><campaign_id>127374</campaign_id><type>RichMediaLibrary</type><external_params><param name="variables">123456789</param><param name="long">-76.5836</param><param name="lat">36.728195</param></external_params><track_url></track_url></external_campaign>
	private class ExternalCampaignHandler extends DefaultHandler
	{
		private AdData currentAd;
	    private StringBuilder sb;
	    private String campaignParamName;
	    
	    public ExternalCampaignHandler(AdData ad)
	    {
	    	currentAd = ad;
	    	sb = new StringBuilder();
	    }

	    @Override
	    public void characters(char[] ch, int start, int length) throws SAXException
	    {
	        super.characters(ch, start, length);
	        sb.append(ch, start, length);
	    }

	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	    {
	    	if (localName.equalsIgnoreCase(TAG_PARAM) &&
    			(attributes != null) && (attributes.getLength() > 0))
            {
	    		String value = attributes.getValue(uri, ATTRIBUTE_EXTERNAL_CAMPAIGN_VARIABLE_NAME);
	    		if (value != null)
	    		{
	    			campaignParamName = value;
	    		}
            }
	    	else
	    	{
	    		campaignParamName = null;
	    	}
	    }
	    
	    @Override
	    public void endElement(String uri, String localName, String name) throws SAXException
	    {
	        super.endElement(uri, localName, name);

            if (localName.equalsIgnoreCase(TAG_CAMPAIGN_ID))
            {
                addExternalProperty(currentAd, TAG_CAMPAIGN_ID, sb.toString());
            }
            else if (localName.equalsIgnoreCase(TAG_CAMPAIGN_TYPE))
            {
            	addExternalProperty(currentAd, TAG_CAMPAIGN_TYPE, sb.toString());
            }
            else if (localName.equalsIgnoreCase(TAG_TRACK_URL))
            {
            	addExternalProperty(currentAd, TAG_TRACK_URL, sb.toString());
            }
            else if (localName.equalsIgnoreCase(TAG_PARAM))
            {
            	addExternalProperty(currentAd, campaignParamName, sb.toString());
            }
            
            sb.setLength(0);    
	    }
	}

	
	private void addExternalProperty(AdData ad, String name, String value)
	{
		if ((ad == null) || (name == null) || (value == null))
		{
			System.out.println("AdParser: add external property with null parameter, skipping...");
			return; // don't try to add with null data
		}
		
		if (ad.externalCampaignProperties == null)
		{
			ad.externalCampaignProperties = new ArrayList<NameValuePair>();
		}
		
		BasicNameValuePair nvp = new BasicNameValuePair(name, value);
		ad.externalCampaignProperties.add(nvp);
	}
	
	
	private String extractExternalCampaignStanza(StringBuilder sb)
	{
		if (sb.indexOf(EXTERNAL_THIRD_PARTY_CAMPAIGN_SIGNAL) > 0)
		{
			int start = sb.indexOf(EXTERNAL_THIRD_PARTY_CAMPAIGN_START);
			int end = sb.indexOf(EXTERNAL_THIRD_PARTY_CAMPAIGN_END) + EXTERNAL_THIRD_PARTY_CAMPAIGN_END.length() + 1; 
			String stanza = sb.substring(start,  end);
			return stanza;
		}
		
		return null;
	}
}
