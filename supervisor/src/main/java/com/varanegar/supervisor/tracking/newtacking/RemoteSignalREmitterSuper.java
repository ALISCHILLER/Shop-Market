package com.varanegar.supervisor.tracking.newtacking;

public interface RemoteSignalREmitterSuper {
    void onConnectToSignalR();
    void onErrorConnectToSignalR();
    void onGetPoint(String lat,String lng,String visitorId);
    void onReConnectToSignalR();

}
