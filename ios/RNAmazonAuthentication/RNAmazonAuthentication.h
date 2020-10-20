//
//  RNAmazonAuthentication.h
//  RNAmazonAuthentication
//
//  Created by MacBook06 on 18/10/20.
//  Copyright © 2020 None. All rights reserved.
//

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"

#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

#endif

#import <LoginWithAmazon/LoginWithAmazon.h>

#import <Foundation/Foundation.h>

@interface RNAmazonAuthentication : RCTEventEmitter <RCTBridgeModule>

@end
