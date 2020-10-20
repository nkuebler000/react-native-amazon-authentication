package com.amazonapp;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.amazon.identity.auth.device.api.authorization.*;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

public class RNLoginWithAmazonModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;
    private RequestContext requestContext;

    public RNLoginWithAmazonModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        this.requestContext = RequestContext.create(reactContext);
    }

    @Override
    public String getName() {
        return "RNAmazonAuthentication";
    }

    @Override
    public void onHostResume() {
        this.requestContext.onResume();
    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
    }

    @ReactMethod
    public void login() {
        this.requestContext.registerListener(new AuthorizeListener() {

            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult result) {
                /* Your app is now authorized for the requested scopes */
                WritableMap userInfo = Arguments.createMap();
                userInfo.putString("email", result.getUser().getUserEmail());
                userInfo.putString("name", result.getUser().getUserName());
                userInfo.putString("user_id", result.getUser().getUserId());
                userInfo.putString("postalCode", result.getUser().getUserPostalCode());
                userInfo.putString("token", result.getAccessToken());

                sendEvent(reactContext, "AmazonAuthEvent", userInfo);            }

            /* There was an error during the attempt to authorize the
            application. */
            @Override
            public void onError(AuthError ae) {
                /* Inform the user of the error */
                WritableMap error = Arguments.createMap();
                error.putString("error", ae.toString());
                sendEvent(reactContext, "AmazonAuthEvent", error);
            }

            /* Authorization was cancelled before it could be completed. */
            @Override
            public void onCancel(AuthCancellation cancellation) {
                /* Reset the UI to a ready-to-login state */
                WritableMap error = Arguments.createMap();
                error.putString("error", "The user cancelled the operation.");
                sendEvent(reactContext, "AmazonAuthEvent", error);
            }
        });

        AuthorizationManager.authorize(new AuthorizeRequest
                .Builder(requestContext)
                .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                .build()
        );
    }

    @ReactMethod
    public void checkIsUserSignedIn() {
        Scope[] scopes = {
                ProfileScope.profile(),
                ProfileScope.postalCode()
        };

        AuthorizationManager.getToken(this.reactContext, scopes, new Listener < AuthorizeResult, AuthError > () {

            @Override
            public void onSuccess(AuthorizeResult result) {
                if (result.getAccessToken() != null) {
                    /* The user is signed in */
                    User.fetch(reactContext, new Listener<User, AuthError>() {
                        /* fetch completed successfully. */
                        @Override
                        public void onSuccess(User user) {
                            WritableMap userInfo = Arguments.createMap();
                            userInfo.putString("email", user.getUserEmail());
                            userInfo.putString("name", user.getUserName());
                            userInfo.putString("user_id", user.getUserId());
                            userInfo.putString("postalCode", user.getUserPostalCode());
                            userInfo.putString("token", result.getAccessToken());

                            sendEvent(reactContext, "AmazonAuthEvent", userInfo);
                        }

                        /* There was an error during the attempt to get the profile. */
                        @Override
                        public void onError(AuthError ae) {

                            WritableMap error = Arguments.createMap();
                            error.putString("error", "User not signed in");
                            sendEvent(reactContext, "AmazonAuthEvent", error);
                        }
                    });
                } else {
                    WritableMap error = Arguments.createMap();
                    error.putString("error", "User not signed in");
                    sendEvent(reactContext, "AmazonAuthEvent", error);                }
            }

            @Override
            public void onError(AuthError ae) {
                /* The user is not signed in */
                WritableMap error = Arguments.createMap();
                error.putString("error", "User not signed in");
                sendEvent(reactContext, "AmazonAuthEvent", error);            }
        });
    }


    @ReactMethod
    public void logout(final Callback callback) {
        AuthorizationManager.signOut(getReactApplicationContext(), new Listener < Void, AuthError > () {
            @Override
            public void onSuccess(Void response) {
                // Set logged out state in UI
                callback.invoke();
            }
            @Override
            public void onError(AuthError authError) {
                // Log the error
                callback.invoke(authError.toString());
            }
        });
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}