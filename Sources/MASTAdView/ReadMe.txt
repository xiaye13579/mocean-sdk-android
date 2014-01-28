Welcome to the Mocean Mobile SDK package for Android, version 3.1
Copyright (C) 2011, 2012, 2013, 2014 Mocean Mobile. All Rights Reserved.

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



