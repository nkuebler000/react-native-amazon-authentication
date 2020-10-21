/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import LoginWithAmazon from 'react-native-amazon-authentication';

import React, {useState, useEffect} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  StatusBar,
  ActivityIndicator,
  Image,
  NativeEventEmitter
} from 'react-native';
import AmazonLoginImg from './assets/amazon_login.png';

const App: () => React$Node = () => {
  const [isLoggedIn, setLoginState] = useState(false);
  const [isLogInPressed, setLoginPressState] = useState(false);
  const [userProfileData, setUserProfileData] = useState({});
  const eventEmitter = new NativeEventEmitter(LoginWithAmazon);

  useEffect(
    () => {
      this.eventListener = eventEmitter.addListener('AmazonAuthEvent', (params) => {
        setLoginPressState(false)
        if(!params.error) {
          setLoginState(true)
          setUserProfileData(params)
        }
     });

      LoginWithAmazon.checkIsUserSignedIn();

      return () => {
        this.eventListener.remove(); 
      }
    },
  []);

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView style={styles.container}>
            {!isLoggedIn && <View style={styles.innerContainer}>
              <TouchableOpacity onPress={()=> {
                setLoginPressState(true)
                LoginWithAmazon.login()
              }}> 
                <Image source={AmazonLoginImg}/>
            </TouchableOpacity>
            {isLogInPressed && <ActivityIndicator color={'blue'} size="large" style={styles.topmargin}/>}
            </View>}
            {isLoggedIn && <View style={styles.innerContainer}>
            <Text style={styles.textLbl}>{userProfileData.name}</Text>
            <Text style={styles.textLbl}>{userProfileData.email}</Text>

              <TouchableOpacity style={styles.topmargin} onPress={()=> {
                LoginWithAmazon.logout((error) => {
                  if(!error) {
                    setLoginState(false)
                    setUserProfileData({})
                  }
                })
              }}> 
                <Text style={styles.textLbl}>Logout</Text>
            </TouchableOpacity>
            </View>}
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    height: 200
  },
  innerContainer: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  textLbl: {
    fontSize: 16,
    marginTop: 5,
    fontWeight: 'bold'
  },
  topmargin: {
    marginTop: 20
  }
});

export default App;
