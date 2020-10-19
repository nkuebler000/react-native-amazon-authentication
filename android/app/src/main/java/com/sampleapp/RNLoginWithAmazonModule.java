package com.sampleapp;

import android.app.Activity;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
    public void login(final Callback callback) {
        this.requestContext.registerListener(new AuthorizeListener() {

            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult result) {
                /* Your app is now authorized for the requested scopes */
                WritableMap userInfo = Arguments.createMap();
                userInfo.putString("email", result.getUser().getUserEmail());
                userInfo.putString("name", result.getUser().getUserName());
                userInfo.putString("user_id", result.getUser().getUserId());
                callback.invoke(null, result.getAccessToken(), userInfo);
            }

            /* There was an error during the attempt to authorize the
            application. */
            @Override
            public void onError(AuthError ae) {
                /* Inform the user of the error */
                callback.invoke(ae.toString());
            }

            /* Authorization was cancelled before it could be completed. */
            @Override
            public void onCancel(AuthCancellation cancellation) {
                /* Reset the UI to a ready-to-login state */
                callback.invoke("The user cancelled the operation.");
            }
        });

        AuthorizationManager.authorize(new AuthorizeRequest
                .Builder(requestContext)
                .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                .build()
        );
    }

    @ReactMethod
    public void checkIsUserSignedIn(final Callback callback) {
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
                            final String name = user.getUserName();
                            WritableMap userInfo = Arguments.createMap();
                            userInfo.putString("email", user.getUserEmail());
                            userInfo.putString("name", user.getUserName());
                            userInfo.putString("user_id", user.getUserId());
                            callback.invoke(null,result.getAccessToken(), userInfo);
                        }

                        /* There was an error during the attempt to get the profile. */
                        @Override
                        public void onError(AuthError ae) {
                            callback.invoke("User not signed in");
                        }
                    });
                } else {
                    callback.invoke("User not signed in");
                }
            }

            @Override
            public void onError(AuthError ae) {
                /* The user is not signed in */
                callback.invoke("User not signed in");
            }
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
}