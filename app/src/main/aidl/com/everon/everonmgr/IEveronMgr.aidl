// IEveronMgr.aidl
package com.everon.everonmgr;

import com.everon.everonmgr.IAsyncListener;

// Declare any non-default types here with import statements

interface IEveronMgr {
    /** Request the process ID of this service */
    int getPid();

    /** Count of received connection requests from clients */
    int getConnectionCount();

    /** Set displayed value of screen */
    void setDisplayedValue(String packageName, int pid, String data);

    // test
    String getMessage();
    void addListener(in IAsyncListener listener);
}