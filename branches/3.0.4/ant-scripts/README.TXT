Build Notes
-----------

In order to build this, you need to do the following:

1) Install/setup the Android SDK, Eclipse and the Android plug-in for Eclipse (ADT) as
   described here: http://developer.android.com/sdk/installing.html. 
   After installing the basic SDK, you need the 2.2 (API level 8) platform for building
   the SDK library.

NOTE: for reference purposes, we are using the following versions of various parts
of this system:

  a) Java JDK version 1.6.0.32
  b) Android SDK tools, revision 21.0.1 / platform tools, revision 16.0.1
  c) Android 2.2 (API level 8) platform, revision 3 (and others for testing, but this
     is what we compile with)
  d) Eclipse Juno (4.2) SR1 
  e) ADT plug-in for Eclipse 21.1.0.v201301150543-557718

  Once you have all the Android parts installed, you should be able to compile and install
  one of the Android sample apps on a simulator and a device. You should make sure this works
  before proceeding.

2) Build the MASTAdView (SDK Library jar file) using Eclipse. 

3) Update the version / date information in the build-sdk-package.xml file to match the
   information for this release. The version should match that in the SDK Constants class.
   
4) Use a command-shell in the ant-script folder to execute the command:
   “ant –f build-sdk-package.xml“ to packge the files for distribution.

  NOTE: The compiled file created by ant for the any brand will be located under the
  \TEMP\ folder.
