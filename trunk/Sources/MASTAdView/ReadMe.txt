Welcome to the Mocean Mobile SDK package for Android, version 3.1
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

Release Notes:

3.1.0

- Updated code base with new interface.
- Animated GIF and images now render on a native ImageView view.

3.1.1

- Updating expand dialog to properly handle clicks when using full screen image or
  text (non-web based ad creatives) for interstitial ads.
- Updated onCloseButtonClick to allow developers to override click behavior.
- Updated tracking logic to cover proper cases of tracking (when view is made visible).


3.1.2

- Updating URL handling for non-HTTP/S links and the internal browser.  The browser
  will now only be invoked with HTTP/S URLs and internally open new HTTP/S URLs.  Any
  non-HTTP/S URL will be routed and opened with an Intent.  Corrected internal browser
  dialog load URL issue.  Added new sample for internal browser testing.
- Updating bridge loading for API 11 and higher for MRAID two-part creatives.  Other
  two-part creative fixes.
- Added layout parameters to WebView when added to expand dialog.  This corrected bug
  introduced with API 17 where the expanded ad view would not fill the expand dialog.
- Set flag to not use cache in WebView.
- Replacing touch handler with click handler for image and text ad rendering.
- Adding validation to third party ad descriptors that attempts to ensure image and
  text ads are rendered natively only if it appears the server's parsing was proper.
