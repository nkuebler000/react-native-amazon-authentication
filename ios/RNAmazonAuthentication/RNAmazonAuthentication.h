//
//  RNAmazonAuthentication.h
//  RNAmazonAuthentication
//
//  Created by MacBook06 on 18/10/20.
//  Copyright © 2020 None. All rights reserved.
//

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#import <LoginWithAmazon/LoginWithAmazon.h>

#import <Foundation/Foundation.h>

@interface RNAmazonAuthentication : NSObject <RCTBridgeModule>

@end
