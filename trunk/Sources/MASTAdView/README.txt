Welcome to the Mocean Mobile SDK package for Android, version 3.0.1
Copyright (C) 2011, 2012, 2013 Mocean Mobile. All Rights Reserved.

Release Notes:

3.0.1

- Handle no ad returned from server use case.
- Add missing user agent for tracking impression.
- Variety of bug fixes for rotation size handling, ad download callbacks, logging.
- Variety of enhancements to sample application.
- New removeContent method in developer API (see javadoc documentation for more.)

3.0.2

- Fixed edge cases of handling custom parameters and refresh.
- Fixed edge case with non-MRAID interstitial ads and close button.
- Updated MRAID JavaScript bridge layer.
- Corrected MRAID resize placement when ad view is located at the bottom.
- Removed target-densitiydpi viewport setting for rich media ad content.
- Relocated JAvaScript handlers for the web view to before enabling JavaScript as some
  OS varients may attempt to reference handlers on enablement.
- Null resize values are now interpreted as 0s to prevent possible null references.
- Updated MRAID resize implementation to enforce a minium size and guaranteed close area.
- Restart the update timer (if enabled) for third party ad content as well.
- Updated MRAID setOrientationProperties method to ensure ads can properly force orientation.
- Updated documentation to reflect recommended android:configChanges attribute.  Should contain:
  keyboardHidden|orientation|screenSize.  Also moved release notes to the README.txt.
 
