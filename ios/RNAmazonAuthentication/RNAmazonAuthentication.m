//
//  RNAmazonAuthentication.m
//  RNAmazonAuthentication
//
//  Created by MacBook06 on 18/10/20.
//  Copyright © 2020 None. All rights reserved.
//

#import "RNAmazonAuthentication.h"
#import <React/RCTLog.h>
#import <LoginWithAmazon/LoginWithAmazon.h>

@implementation RNAmazonAuthentication

RCT_EXPORT_MODULE()


- (NSArray<NSString *> *)supportedEvents
{
  return @[@"AmazonAuthEvent"];
}

// Called when the user taps the Login with Amazon Button
// Redirects to the Amazon sign on page
RCT_EXPORT_METHOD(login)
{
    
    // Build an authorize request.
    AMZNAuthorizeRequest *request = [[AMZNAuthorizeRequest alloc] init];
    request.scopes = [NSArray arrayWithObjects:
                      [AMZNProfileScope profile],
                      [AMZNProfileScope postalCode], nil];
    
    // Make an Authorize call to the Login with Amazon SDK.
    [[AMZNAuthorizationManager sharedManager] authorize:request
                                            withHandler:^(AMZNAuthorizeResult *result, BOOL
                                                          userDidCancel, NSError *error) {
                                                if (error) {
                                                    [self sendEventWithName:@"AmazonAuthEvent" body:@{@"error": [error userInfo]}];
                                                } else if (userDidCancel) {
                                                    [self sendEventWithName:@"AmazonAuthEvent" body:@{@"error": @"User cancelled login with amazon"}];
                                                } else {
                                                    // Authentication was successful.
                                                    // Obtain the access token and user profile data.
                                                    NSString *accessToken = result.token;
                                                    AMZNUser *user = result.user;
                                                    
                                                     [self sendEventWithName:@"AmazonAuthEvent" body:
                                                          @{
                                                              @"email": user.profileData[@"email"],
                                                              @"name": user.profileData[@"name"],
                                                              @"user_id": user.profileData[@"userID"],
                                                              @"postalCode": user.profileData[@"postalCode"],
                                                              @"token": accessToken
                                                          }
                                                     ];
                                                }
                                            }];
}

RCT_EXPORT_METHOD(checkIsUserSignedIn) {
    // Make authorize call to SDK using AMZNInteractiveStrategyNever to detect whether there is an authenticated user. While making this call you can specify scopes for which user authorization is needed. If this call returns error, it means either there is no authenticated user, or at least of the requested scopes are not authorized. In both case you should show sign in page again.
    
    // Build an authorize request.
    AMZNAuthorizeRequest *request = [[AMZNAuthorizeRequest alloc] init];
    
    // Requesting 'profile' scopes for the current user.
    request.scopes = [NSArray arrayWithObject:[AMZNProfileScope profile]];
    
    // Set interactive strategy as 'AMZNInteractiveStrategyNever'.
    request.interactiveStrategy = AMZNInteractiveStrategyNever;
    
   // Make an Authorize call to the Login with Amazon SDK.
    [[AMZNAuthorizationManager sharedManager] authorize:request
                                            withHandler:^(AMZNAuthorizeResult *result, BOOL
                                                          userDidCancel, NSError *error) {
                                                if (error) {
                                                    [self sendEventWithName:@"AmazonAuthEvent" body:@{@"error": [error userInfo]}];
                                                } else if (userDidCancel) {
                                                    [self sendEventWithName:@"AmazonAuthEvent" body:@{@"error": @"User not signed in"}];
                                                } else {
                                                    // Authentication was successful.
                                                    // Obtain the access token and user profile data.
                                                    NSString *accessToken = result.token;
                                                    AMZNUser *user = result.user;
                                                    [self sendEventWithName:@"AmazonAuthEvent" body:
                                                         @{
                                                             @"email": user.profileData[@"email"],
                                                             @"name": user.profileData[@"name"],
                                                             @"user_id": user.profileData[@"userID"],
                                                             @"postalCode": user.profileData[@"postalCode"],
                                                             @"token": accessToken
                                                         }
                                                    ];
                                                }
                                            }];
}

// Call to fetch the user's account information as long as they are already logged in/authorized
RCT_EXPORT_METHOD(fetchUserData:(RCTResponseSenderBlock)callback)
{
    [AMZNUser fetch:^(AMZNUser *user, NSError *error) {
        if (error) {
            callback(@[[error userInfo]]);
        } else if (user) {
            callback(@[[NSNull null], user.profileData]);
        }
    }];
}

// Logs the user out
RCT_EXPORT_METHOD(logout:(RCTResponseSenderBlock)callback)
{
    [self logoutAmazon:callback];
    [self logoutAmazon:callback];
}

- (void)logoutAmazon:(RCTResponseSenderBlock)callback {
    [[AMZNAuthorizationManager sharedManager] signOut:^(NSError * _Nullable error) {
        if (error) {
            callback(@[[error userInfo]]);
        } else {
            callback(@[[NSNull null]]);
        }
    }];
}

@end
