package com.varanegar.vaslibrary.manager.tourmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;

/**
 * Created by A.Torabi on 10/24/2018.
 */

public class SendTourService extends Service {
    private Handler handler;
    private TourManager tourManager;
    private Handler handlerCallBack;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    ServiceBinder serviceBinder = new ServiceBinder();

    class SendThread extends Thread {
        private final Context context;
        private final TourManager.TourCallBack callBack;

        SendThread(Context context, TourManager.TourCallBack callBack) {
            this.context = context;
            this.callBack = callBack;
        }


        @Override
        public void run() {
            tourManager = new TourManager(context);
            tourManager.populatedAndSendTour(new TourManager.TourCallBack() {
                @Override
                public void onSuccess() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess();
                        }
                    });
                }

                @Override
                public void onFailure(final String error) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure(error);
                        }
                    });
                }

                @Override
                public void onProgressChanged(final String progress) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onProgressChanged(progress);
                        }
                    });
                }
            });
        }
    }

    public void sendTour(final TourManager.TourCallBack callBack) {
        handler = new Handler();
        SendThread sendThread = new SendThread(this, callBack);
        sendThread.setName("SendTourThread");
        sendThread.start();
        HandlerThread ht = new HandlerThread("SendTourThread");
        ht.start();
        handlerCallBack = new Handler(ht.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == 321)
                    tourManager.stopSendingTour();
                return true;
            }
        });

    }

    public void stopSendingTour() {
        handlerCallBack.sendEmptyMessage(321);
    }


    public class ServiceBinder extends Binder {
        public SendTourService getService() {
            return SendTourService.this;
        }
    }
}
