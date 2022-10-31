// IAsyncListener.aidl
package com.everon.everonmgr;

// Declare any non-default types here with import statements

interface IAsyncListener {
  oneway void onResponse(String str);
}