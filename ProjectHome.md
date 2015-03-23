&lt;wiki:gadget url="https://android-lockpattern.googlecode.com/hg/resources/gadgets/main\_header.xml" height="70" width="50%" border="1" /&gt;

**apk-signer** is a convenient tool to sign APK files, it tends to serve end-users, not developers.

**[Latest stable: v1.8.5 (#45)](Downloads.md)** (February 8th, 2014). _From version `2.0 beta (#46)`, this project follows [Semantic Versioning](https://en.wikipedia.org/wiki/Semantic_versioning#Semantic_versioning)._

### Features ###

  * Generate Keystore files.
  * Sign APK/JAR/ZIP files.
  * Align APK files; verify their alignment.
  * Extract fingerprints from keystore files.
  * Auto-update.
  * Languages supported: English, Vietnamese.
  * Support: All operating systems which supports Java Virtual Machines. Except for Windows: this app only supports Windows < 7.

### Notes ###

  * This is an Eclipse [WindowBuilder](https://developers.google.com/java-dev-tools/wbpro/?hl=pl-PL) based project.
  * The app requires:
    * JRE 1.6+ to run.
    * JDK 1.6+ to generate keystores and sign files.
    * Screen resolution from 1024x768.
  * Please don't try to open keyfile created by this app in Eclipse _with Android Development Tools (ADT) < v18_. Eclipse will **_crash_** if you do that. We don't know the reason. However:
    * This app can sign files with keyfiles created by Eclipse.
    * We tested with ADT v18.0.0.v201203301601-306762, it works fine.
  * Make sure to align the APK files _after_ you signed them. Refer to Android tool [zipalign](http://developer.android.com/tools/help/zipalign.html) for further details. The module _APK Alignment_ is ported from [zipalign source](https://android.googlesource.com/platform/build/+/master/tools/zipalign/).

Here is GettingStarted.

### Donation ###

Thank you for your consideration, please have a look at our [Android app](https://play.google.com/store/apps/details?id=com.haibison.apksigner) on Google Play.