
# react-native-amazon-authentication
This module demonstrates integration of login with amazon for React Native apps.


# Table of Contents
  * Installation
    * iOS
    * Android
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
React Native 0.60 and above run the following command

`npx pod-install`

OR

`cd ios`
`pod install`

### A. Get amazon authentication API key
If you don't have an amazon developer account yet then create one from here[https://developer.amazon.com/]

After creating and login your accound do the following steps:

1. You will first have to create a Security Profile to get your amazon iOS APIKey. Go here[https://developer.amazon.com/settings/console/securityprofile/overview.html] and create one.
2. Go inside your Security Profile and select `iOS Settings` tab.
3. Add an API key there. You will be asked for key name and your iOS app bundle identifier.
4. After adding an API key, you will get an amazon API key there. Copy it.

### B. Add amazon authentication API key to your app plist
Create a new key `APIKey` with type string in your iOS app plist file and paste the amazon api key there.

### C. Add a URL Scheme to your app plist
Go to your app info plist file in XCode. Click on + button under URL Types in plist file. You will be asked to enter your app bundle identifier and URL Schemes. The URL scheme must be declared as amzn-<bundleID> (for example, amzn-com.example.app).

If you have any doubt in the above steps then follow the document here[https://developer.amazon.com/docs/login-with-amazon/create-ios-project.html#add-api-key] for more details.

### D. Add amazon login framework(SDK) in your app
You can find amazon login framework in the github repo. Download it and keep it inside ios folder of your react native app. Go to `Build Phases` section in your XCode and check LoginWithAmazon.framework is showing there or not under `Link Binary With Libraries`. If not then add it. Also add `Security.framework` and `SafariServices.framework` there.

### E. Update your podfile
To add Framework Search Path under `RNAmazonAuthentication` pod you need to update your Podfile. Add the following code under section `post_install do |installer|` in your Podfile:

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

Now you have setup your iOS app with this node-module.


# Usage
In your login component or hook, copy the following code:

```javascript
import LoginWithAmazon from 'react-native-amazon-authentication';
```

1. For login:
```
LoginWithAmazon.login((error, accessToken, profileData) => {
  if(!error) {
    // ...  Navigate to home screen
  }
});
```

2. For checking if user already signed in:
```
LoginWithAmazon.checkIsUserSignedIn((error, accessToken, profileData) => {
  if(!error) {
    // ...  Navigate to home screen
  }
});
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

## iOS
1. Download it from github repo
2. Run `yarn install` from the root directory
3. Run `npx pod-install`
4. Change your amazon `APIKey` in the plist file
5. Run your iOS RN app from XCode or terminal


# License
MIT
