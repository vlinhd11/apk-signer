If you are writing an Android project for a far far away client, so this tool is useful.

It just has a problem that the client must download and install JDK on their machine. And nothing else is required.

So help your client download and install JDK, then give them this tool. If they're using Windows, guide them to point the tool to **JDK's `/bin`** folder. After that, everything is easy: enter required fields, then create key, or sign the APK files...

### Notes ###

Make sure to tell your client to align the APK files after signing them. Refer to Android tool [zipalign](http://developer.android.com/tools/help/zipalign.html) for further details. The module _APK Alignment_ is ported from [zipalign source](https://android.googlesource.com/platform/build/+/master/tools/zipalign/).