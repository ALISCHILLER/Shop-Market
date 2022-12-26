package com.varanegar.supervisor.tracking.newtacking;

import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.UUID;


/**
 * Created by m-latifi on 11/11/2022.
 */

public class SignalRListener {

    private static SignalRListener instance;
    private final HubConnection hubConnection;
    private final RemoteSignalREmitterSuper remoteSignalREmitter;
    private Thread thread;

    //---------------------------------------------------------------------------------------------- SignalRListener
    public SignalRListener(RemoteSignalREmitterSuper remoteSignalREmitter, String token) {
        this.remoteSignalREmitter = remoteSignalREmitter;

        String url = "http://5.160.125.98:1364/realtimenotification?access_token=" + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIzZmE4NWY2NC01NzE3LTQ1NjItYjNmYy0yYzk2M2Y2NmFmYTYiLCJWaXNpdG9ySWQiOiIzZmE4NWY2NC01NzE3LTQ1NjItYjNmYy0yYzk2M2Y2NmFmYTYiLCJTdXBlcnZpc29ySWQiOiIzZmE4NWY2NC01NzE3LTQ1NjItYjNmYy0yYzk2M2Y2NmFmYTYiLCJUb3VySWQiOiIzZmE4NWY2NC01NzE3LTQ1NjItYjNmYy0yYzk2M2Y2NmFmYTYiLCJJc0Rpc3QiOiIxIiwibmJmIjoxNjcxOTUyNzUwLCJleHAiOjE2NzIxMjU1NTAsImlhdCI6MTY3MTk1Mjc1MH0.J7FlmW47ArxD9c6KLLNBvCHHfshXhUoOqvAJLlbioqc";
        hubConnection = HubConnectionBuilder
                .create(url)
                .build();

        hubConnection.on(
                "ReceiveVisitorLocation",
                (tourId, visitorId, lat, lng) ->this.remoteSignalREmitter.onGetPoint(lat,lng),
                String.class, String.class, String.class, String.class);
    }
    //---------------------------------------------------------------------------------------------- SignalRListener


    //---------------------------------------------------------------------------------------------- getInstance
    public static SignalRListener getInstance(RemoteSignalREmitterSuper remote, String token) {
        if (instance == null)
            instance = new SignalRListener(remote, token);
        return instance;
    }
    //---------------------------------------------------------------------------------------------- getInstance


    //---------------------------------------------------------------------------------------------- startConnection
    public void startConnection() {
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                        hubConnection
                                .start()
                                .doOnError(throwable -> remoteSignalREmitter.onErrorConnectToSignalR())
                                .doOnComplete(remoteSignalREmitter::onConnectToSignalR)
                                .blockingAwait();

                    hubConnection.onClosed(exception -> {
                        remoteSignalREmitter.onReConnectToSignalR();
                        interruptThread();
                    });
                } catch (Exception ignored) {
                    remoteSignalREmitter.onReConnectToSignalR();
                    interruptThread();
                }
            }
        };
        thread.start();
    }
    //---------------------------------------------------------------------------------------------- startConnection
    public void DistJoinGroup() {
        Log.e("DistJoinGroup","DistJoinGroup");
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("DistJoinGroup");
    }
    public void sendVisitorLocation(UUID torId, String lat, String lon) {
        Log.e("sendVisitorLocation",hubConnection.getConnectionState()+"asdasd");
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("sendVisitorLocation", torId, lat, lon);
    }
    //---------------------------------------------------------------------------------------------- stopConnection
    public void stopConnection() {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.stop();
        interruptThread();
    }
    //---------------------------------------------------------------------------------------------- stopConnection


    //---------------------------------------------------------------------------------------------- isConnection
    public boolean isConnection() {
        return hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
    }
    //---------------------------------------------------------------------------------------------- isConnection


    //---------------------------------------------------------------------------------------------- sendToServer
    public void sendToServer(Integer tripId,Integer driverId, String lat, String lon) {
        String arg = "trip" + tripId;
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("TrackDriver", arg, driverId, lat, lon);
    }
    //---------------------------------------------------------------------------------------------- sendToServer



    //---------------------------------------------------------------------------------------------- NotificationToServer
    public void NotificationToServer(Integer tripId, Integer stationId) {
        String arg = "trip" + tripId + "station" + stationId;
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("InformNextStation", arg);
    }
    //---------------------------------------------------------------------------------------------- NotificationToServer



    //---------------------------------------------------------------------------------------------- interruptThread
    public void interruptThread() {
        if (thread != null)
            thread.interrupt();
    }
    //---------------------------------------------------------------------------------------------- interruptThread

    /*        hubConnection.on("ReceiveNewPosition", (receive) -> {
            this.remoteSignalREmitter.onReceiveSignalR("receive : " + receive);
        }, Boolean.class);*/

}
