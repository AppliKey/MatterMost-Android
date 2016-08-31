## General Documentation

* This project should be used as a base template for all new Android projects developed at [AppliKey Solutions](http://applikeysolutions.com/).
* This project contains 90% of needed dependencies and tools to develop high quality client-server android applications.
* There are few changes that developer should perform in order to use this template for his project:
    1. **Replace package name** with a needed one. In case if needed package name is unknown, it should be named *com.applikey.project_name*. To replace package name please use one of this [solutions](http://stackoverflow.com/a/19320896/3019157). Package name should be changed in following files: manifest.xml, build.gradle, Constants.java, and package directory structure.
    2. Check, if you need to use **Runtime Permissions**. If answer is yes, change **targetSdkVersion 22** to **targetSdkVersion 23** in build.gradle file. Preferred option is to use them.
    3. Check **examples** in MainActivity.java. They are showing preferred usage of project components, like Preferences, ImageLoader, Web-client, DataBase. Please follow them in future, and write similar code. Feel free to delete examples then.
    4. Check out other **TODO** comments from AS![Android Studio](https://cloud.githubusercontent.com/assets/5869863/13356797/dc56548c-dcaf-11e5-8f69-53249617a8c5.png)s
    5. Check **Utils** classes and methods. They have lots of helpful tools and utils. Please use them.
    6. Please note, that **ProGuard** is **enabled** by default for **release** builds. Please check your release builds every day, to prevent ProGuard-related crashes or issues. Please read official [Proguard documentation](http://developer.android.com/intl/ru/tools/help/proguard.html) to solve any related issues. Every time you add any dependency to the project, be sure that Proguard will obfuscate it properly. Proguard will remove unused methods and classes from final *apk, so don't care about unused classes or methods.
    7. Please note, that default **debug.keystore** is used here. It can be found at *android_skeleton_project/app/keys/debug.keystore*. This debug keystore will be used for every android app developed inside as a corporate one.


## Other rules
* Please follow IOC [(inversion of control)](https://ru.wikipedia.org/wiki/%D0%98%D0%BD%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_%D1%83%D0%BF%D1%80%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D1%8F) principles in your projects. Every dependency that you inject into your module should be hidden behind an abstraction (java interface).
* Please use android annotations in your java interfaces. For example:
	*  @Nullable for methods that return  null.
	*  @Nullable for parameters that may be null, and it's safe.
	*  @NotNull for methods that will never return null;
	*  @NotNull for parameters that should never be null;
	*  @WorkerThread for methods that should be performed called only from background thread (some heavy operations, like IO operations, Bitmap transforming, and others).
	*  @UIThread for methods that should be called only from UI thread. 
* Application **release** keystore should be stored in two places minimum. There are two options here:
	*  Keystore in git repository with a project, and in Redmine. Passwords in Redmine.
	*  Keystore in Redmine and corporate Dropbox. Passwords in build.gradle.
* Please follow load-content-error principle for every screen that performs a web request. 
	1. Display progress indicator while request is executing.  
	2. Update UI when request executed successfully.
	3. Display an error if request failed with an error. (Toast/Dialog/Snackbar).
	4. Always check internet connection before making web request. If there is no connection, just notify user about that, and skip web request. Use  *ConnectivityUtils.java* class for that.