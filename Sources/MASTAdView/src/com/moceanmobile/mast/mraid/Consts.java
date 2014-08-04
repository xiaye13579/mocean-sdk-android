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

public interface Consts
{
    public enum State
    {
        Loading,
        Default,
        Expanded,
        Resized,
        Hidden,
    };
    
    public enum PlacementType
    {
        Inline,
        Interstitial,
    };

    public enum Feature
    {
        SMS,
        Tel,
        Calendar,
        StorePicture,
        InlineVideo,
    };

    public enum ForceOrientation
    {
        Portrait,
        Landscape,
        None,
    };

    public enum CustomClosePosition
    {
        TopLeft,
        TopCenter,
        TopRight,
        Center,
        BottomLeft,
        BottomCenter,
        BottomRight,
    };
    
	public static final String StateLoading = "loading";
    public static final String StateDefault = "default";
    public static final String StateExpanded = "expanded";
    public static final String StateResized = "resized";
    public static final String StateHidden = "hidden";

    public static final String PlacementTypeInline = "inline";
    public static final String PlacementTypeInterstitial = "interstitial";

    public static final String FeatureSMS = "sms";
    public static final String FeatureTel = "tel";
    public static final String FeatureCalendar = "calendar";
    public static final String FeatureStorePicture = "storePicture";
    public static final String FeatureInlineVideo = "inlineVideo";

    public static final String EventReady = "ready";

    public static final String True = "true";
    public static final String False = "false";

    public static final String Scheme = "mraid";
    public static final String CommandInit = "init";
    public static final String CommandClose = "close";
    public static final String CommandOpen = "open";
    public static final String CommandUpdateCurrentPosition = "updatecurrentposition";
    public static final String CommandExpand = "expand";
    public static final String CommandSetExpandProperties = "setexpandproperties";
    public static final String CommandResize = "resize";
    public static final String CommandSetResizeProperties = "setresizeproperties";
    public static final String CommandSetOrientationProperties = "setorientationproperties";
    public static final String CommandPlayVideo = "playvideo";
    public static final String CommandCreateCalendarEvent = "createcalendarevent";
    public static final String CommandStorePicture = "storepicture";

    public static final String CommandArgUrl = "url";
    public static final String CommandArgEvent = "event";

    public static final String PropertiesWidth = "width";
    public static final String PropertiesHeight = "height";
    
    public static final String ExpandPropertiesUseCustomClose = "useCustomClose";

    public static final String ResizePropertiesCustomClosePosition = "customClosePosition";
    public static final String ResizePropertiesOffsetX = "offsetX";
    public static final String ResizePropertiesOffsetY = "offsetY";
    public static final String ResizePropertiesAllowOffscreen = "allowOffscreen";

    public static final String ResizePropertiesCCPositionTopLeft = "top-left";
    public static final String ResizePropertiesCCPositionTopCenter = "top-center";
    public static final String ResizePropertiesCCPositionTopRight = "top-right";
    public static final String ResizePropertiesCCPositionCenter = "center";
    public static final String ResizePropertiesCCPositionBottomLeft = "bottom-left";
    public static final String ResizePropertiesCCPositionBottomCenter = "bottom-center";
    public static final String ResizePropertiesCCPositionBottomRight = "bottom-right";
    
    public static final String OrientationPpropertiesAllowOrientationChange = "allowOrientationChange";
    public static final String OrientationPpropertiesForceOrientation = "forceOrientation";
    public static final String OrientationPropertiesForceOrientationNone = "none";
    public static final String OrientationPropertiesForceOrientationPortrait = "portrait";
    public static final String OrientationPropertiesForceOrientationLandscape = "landscape";
}
