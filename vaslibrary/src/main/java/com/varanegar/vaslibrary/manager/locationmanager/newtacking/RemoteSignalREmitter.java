package com.varanegar.vaslibrary.manager.locationmanager.newtacking;

public interface RemoteSignalREmitter {
    void onConnectToSignalR();
    void onErrorConnectToSignalR();
    void onReConnectToSignalR();

}
