/*
 * PubMatic Inc. (“PubMatic”) CONFIDENTIAL
 * Unpublished Copyright (c) 2006-2014 PubMatic, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of PubMatic. The intellectual and technical concepts contained
 * herein are proprietary to PubMatic and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from PubMatic.  Access to the source code contained herein is hereby forbidden to anyone except current PubMatic employees, managers or contractors who have executed 
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  
 * information that is confidential and/or proprietary, and is a trade secret, of  PubMatic.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, 
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF PubMatic IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE 
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS  
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.                
 */

package com.moceanmobile.mast.mraid;

import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class OrientationProperties
{
	public static OrientationProperties propertiesFromArgs(Map<String, String> args)
	{
		OrientationProperties properties = new OrientationProperties();
		
		try
		{
			properties.setAllowOrientationChange(Consts.False.equals(args.get(Consts.OrientationPpropertiesAllowOrientationChange)) == false);
			
			String forceOrientationString = args.get(Consts.OrientationPpropertiesForceOrientation);
			if (Consts.OrientationPropertiesForceOrientationNone.equals(forceOrientationString))
			{
				properties.setForceOrientation(Consts.ForceOrientation.None);
			}
			else if (Consts.OrientationPropertiesForceOrientationPortrait.equals(forceOrientationString))
			{
				properties.setForceOrientation(Consts.ForceOrientation.Portrait);
			}
			else if (Consts.OrientationPropertiesForceOrientationLandscape.equals(forceOrientationString))
			{
				properties.setForceOrientation(Consts.ForceOrientation.Landscape);
			}
		}
		catch (Exception ex)
		{
			// TODO: Error handling?
			// Possibly let the error bubble up so whomever is trying to parse can deal with it.
		}
		
		return properties;
	}

	private boolean allowOrientationChange = true;
	private Consts.ForceOrientation forceOrientation = Consts.ForceOrientation.None;
	
	public OrientationProperties()
	{
		
	}

	public boolean getAllowOrientationChange()
	{
		return allowOrientationChange;
	}
	
	public void setAllowOrientationChange(boolean allowOrientationChange)
	{
		this.allowOrientationChange = allowOrientationChange;
	}
	
	public Consts.ForceOrientation getForceOrientation()
	{
		return forceOrientation;
	}
	
	public void setForceOrientation(Consts.ForceOrientation forceOrientation)
	{
		this.forceOrientation = forceOrientation;
	}
	
	@Override
	public String toString()
	{
		String allowOrientationChangeString = Consts.False;
		if (allowOrientationChange)
			allowOrientationChangeString = Consts.True;
		
		String forceOrientationString = Consts.OrientationPropertiesForceOrientationNone;
		switch (forceOrientation)
		{
			case None:
				forceOrientationString = Consts.OrientationPropertiesForceOrientationNone;
				break;
			case Portrait:
				forceOrientationString = Consts.OrientationPropertiesForceOrientationPortrait;
				break;
			case Landscape:
				forceOrientationString = Consts.OrientationPropertiesForceOrientationLandscape;
				break;
		}
		
		String format = "{allowOrientationChange:%s,forceOrientation:'%s'}";
		Formatter formatter = new Formatter(Locale.US);
		formatter.format(format, allowOrientationChangeString, forceOrientationString);
		String toString = formatter.toString();
		formatter.close();
		
		return toString;
	}

}
