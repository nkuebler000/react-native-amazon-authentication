
# react-native-amazon-authentication
This module demonstrates integration of login with amazon for React Native apps.


# Table of Contents
  * Installation
    * iOS
  * Usage
  * Example


# Installation
Run the following command from your root directory:

Using npm:
`npm install --save react-native-amazon-authentication`

OR

Using yarn:
`yarn add react-native-amazon-authentication`

## iOS
For React Native 0.60 and above, run the following command

`npx pod-install`

OR

`cd ios`

`pod install`

For React Native 0.59 and below

Run `react-native link react-native-amazon-authentication` to link the react-native-amazon-authentication library.


### A. Get amazon authentication API key
If you don't have an amazon developer account yet then create one from [here](https://developer.amazon.com/)

After creating and login your amazon developer account, do the following:

1. You will first have to create a Security Profile to get your amazon iOS APIKey. Go [here](https://developer.amazon.com/settings/console/securityprofile/overview.html) and create one.
2. Go inside your Security Profile and select `iOS Settings` tab.
3. Click on a button `Add an API key` there. You will be asked for key name and your iOS app bundle identifier.
4. After adding an API key, you will get your iOS amazon API key there. Copy it.

### B. Add amazon authentication API key to your app plist
Create a new key `APIKey` with type string in your iOS app plist file and paste the amazon api key there.

### C. Add a URL Scheme to your app plist
Go to your app info plist file in XCode. Click on + button under URL Types in plist file. You will be asked to enter your app bundle identifier and URL Schemes. The URL scheme must be declared as amzn-bundleID (for example, amzn-com.example.app).

If you have any doubt in the above steps then follow the document [here](https://developer.amazon.com/docs/login-with-amazon/create-ios-project.html#add-api-key) for more details.

### D. Add amazon login framework(SDK) in your app
You can find LoginWithAmazon.framework in the github repo. Download it and keep it inside ios folder of your react native app. Go to `Build Phases` section in your XCode and check LoginWithAmazon.framework is showing there or not under `Link Binary With Libraries`. If not then add it. Also add `Security.framework` and `SafariServices.framework` there.

### E. Update your podfile
To add Framework Search Path under `RNAmazonAuthentication` pod, you need to update your Podfile. Add the following code under section `post_install do |installer|` in your Podfile:

```
installer.pods_project.targets.each do |target|
      # find the right target
      if target.name == 'RNAmazonAuthentication'
        target.build_configurations.each do |config|
          # add library search paths
          config.build_settings['FRAMEWORK_SEARCH_PATHS'] = ["$(PROJECT_DIR)/.."]
        end
      end
    end
```

Run `npx pod-install` again.

### F. Add openURL method
In AppDelegate.m file of your iOS app, add the following import and method:

```
#import <LoginWithAmazon/LoginWithAmazon.h>


-(BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
  NSString *sourceApplication = options[UIApplicationOpenURLOptionsSourceApplicationKey];

  // Pass on the url to the SDK to parse authorization code from the url.
  BOOL isValidRedirectLogInURL = [AMZNAuthorizationManager handleOpenURL:url sourceApplication:sourceApplication];
  
  if(!isValidRedirectLogInURL)
      return NO;
  
  // App may also want to handle url
  return YES;
}
```

Now you have setup your iOS app with this node-module completely.


## Android
For React Native 0.60 and above, no need to link android module.

For React Native 0.59 and below

Run `react-native link react-native-amazon-authentication` to link the react-native-amazon-authentication library. Skip this step if you have already done it for iOS. 

### A. Get amazon authentication API key
If you have already done the steps under iOS section above to get api key then you don't need to create amazon developer account and security profile again as you can use the same.

Now, do the following:

1. Go inside your Security Profile and select `Android/Kindle Settings` tab.
2. Click on a button `Add an API key` there. You will be asked to enter API key name, Package name, MD5 Signature, and SHA256 Signature.
3. You will have to generate MD5 and SHA256 Signatures for debug and release builds.

  a. Generate Debug Signature
    Inside the android folder in your react native app you will find an app folder there. Go inside the app folder and then you will find the `debug.keystore` file. Run the following command on your terminal:
    `keytool -list -v -keystore <your debug.keystore path> -alias androiddebugkey -storepass android -keypass android`
    Now you will get the MD5 and SHA256 signatures.

  b. Generate Release Signature
    When you will create a sined apk then you will also create a jks file. To generate release signature, this jks file would be required. Run the following command on your terminal:
    `keytool -list -v -keystore <your .jks file path> -alias <your jks file alias name>`
    Now you will get the MD5 and SHA256 signatures.

4. After adding an API key, you will get your android amazon API key there. Copy it.

### B. Add amazon authentication API key in a txt file for your android app
  a. Create an `assets` folder inside `main` folder:
    rn-app-root-folder -> android -> app -> src -> main

  b. Create a txt file with name `api_key.txt` inside:
    rn-app-root-folder -> android -> app -> src -> main -> assets
  
  c. Paste your android API Key inside file `api_key.txt`

### C. Add WorkflowActivity inside AndroidManifest.xml file
Under <Application > tag, paste the following code:

```
<activity
    android:name="com.amazon.identity.auth.device.workflow.WorkflowActivity"
    android:allowTaskReparenting="true"
    android:launchMode="singleTask"
    android:theme="@android:style/Theme.NoDisplay">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>

        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <!-- android:host must use the full package name found in Manifest General Attributes -->
        <data
            android:host="${applicationId}"
            android:scheme="amzn"/>
    </intent-filter>
</activity>
```
Now you have setup your Android app with this node-module completely.


# Usage
In your login component or hook, copy the following code:

```javascript
import LoginWithAmazon from 'react-native-amazon-authentication';
import {
  NativeEventEmitter
} from 'react-native';

const eventEmitter = new NativeEventEmitter(LoginWithAmazon);

```
Add Listener in your constructor() or componentDidMount() method if you are using Component class OR if you are using hook then add the following code in useEffect() method:

```
this.eventListener = eventEmitter.addListener('AmazonAuthEvent', (params) => {
  if(!params.error) {
    //  Navigate to home screen
  }
});

```

Remove Listener in your componentWillUnmount() method if you are using Component class OR if your are using hook then return the following code from useEffect() method:

```
this.eventListener.remove(); 

```

1. For login:
```
LoginWithAmazon.login();
```

2. For checking if user already signed in:
```
LoginWithAmazon.checkIsUserSignedIn();
```

3. For Logout:
```
LoginWithAmazon.logout((error) => {
  if(!error) {
    // ...  Navigate to login screen
  }
});
```


# Example
This module has an example react native project in github. Just do the following:

### iOS
1. Download it from github repo
2. Run `yarn install` from the root directory
3. Run `npx pod-install`
4. Change your amazon `APIKey` in the plist file
5. Run your iOS RN app from XCode or terminal


# License
MIT
