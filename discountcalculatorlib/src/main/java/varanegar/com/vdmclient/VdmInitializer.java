package varanegar.com.vdmclient;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import androidx.annotation.Nullable;

import varanegar.com.discountcalculatorlib.R;
import varanegar.com.vdmclient.handlers.InitHandlerResponse;
import varanegar.com.vdmclient.handlers.VdmHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by A.Torabi on 7/17/2018.
 */

public abstract class VdmInitializer {
    private InitCallback callback;

    void runCallBackError(String error) {
        if (callback != null)
            callback.onFailure(error);
    }

    void runCallBackSuccess(String sessionId) {
        if (callback != null)
            callback.onSuccess(sessionId);
    }

    public Context getContext() {
        return context;
    }

    private final Context context;

    public String getAppId() {
        return appId;
    }

    private final String appId;

    public VdmClient getClient() {
        return client;
    }

    private final VdmClient client;

    static class AttachHandler extends VdmHandler {
        private final VdmInitializer initializer;

        AttachHandler(VdmInitializer initializer, String appId, String sessionId) {
            super(103);
            bundle = new Bundle();
            bundle.putString("AppId", appId);
            bundle.putString("SessionId", sessionId);
            this.initializer = initializer;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == getId()) {
                String sessionId = msg.getData().getString("SessionId", null);
                if (msg.arg1 == 1)
                    initializer.runCallBackSuccess(sessionId);
                else
                    initializer.runCallBackError(initializer.context.getString(R.string.attaching_vdm_file_failed));
            }
        }
    }


    static class InitHandler extends VdmHandler {

        private final VdmInitializer initializer;
        private final VdmClient client;
        private final Context context;
        private final String appId;

        InitHandler(VdmInitializer initializer) {
            super(101);
            bundle = new Bundle();
            bundle.putString("AppId", initializer.appId);
            this.initializer = initializer;
            client = initializer.client;
            context = client.getContext();
            appId = client.getAppId();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == getId()) {
                String sessionId = msg.getData().getString("SessionId", null);
                String destinationPath = msg.getData().getString("DestinationPath", null);
                byte[] dbBytes = msg.getData().getByteArray("Database");
                if (sessionId == null || sessionId.isEmpty())
                    onFailure();
                else {
                    client.saveSessionId(sessionId);
                    InitHandlerResponse response = new InitHandlerResponse();
                    response.SessionId = sessionId;
                    response.Database = dbBytes;
                    response.DestinationPath = destinationPath;
                    onSuccess(response);
                }
            }
        }

        private void onFailure() {
            initializer.runCallBackError(context.getString(R.string.init_vdm_failed));
        }

        private void onSuccess(InitHandlerResponse response) {
            File file = context.getDatabasePath(response.SessionId);
            try {
                FileOutputStream stream = new FileOutputStream(file.getAbsolutePath());
                stream.write(response.Database);
                boolean result = initializer.attachDb(response.SessionId);
                if (!result) {
                    initializer.runCallBackError(context.getString(R.string.init_vdm_failed_data_is_not_compatible));
                    return;
                }
                File dest = new File(response.DestinationPath + "/" + response.SessionId);
                copy(file, dest);
                client.run(new AttachHandler(initializer, appId, response.SessionId));
            } catch (FileNotFoundException e) {
                initializer.runCallBackError(context.getString(R.string.opening_vdm_data_failed));
            } catch (RemoteException e) {
                initializer.runCallBackError(context.getString(R.string.vdm_service_is_not_available));
            } catch (IOException e) {
                initializer.runCallBackError(context.getString(R.string.error_saving_vdm_file));
            } catch (NoConnectionException e) {
                initializer.runCallBackError(context.getString(R.string.vdm_service_is_not_available));
            }
        }
    }

    public VdmInitializer(VdmClient client) {
        this.client = client;
        appId = client.getAppId();
        context = client.getContext();
    }

    public interface InitCallback {
        void onSuccess(String sessionId);

        void onFailure(String error);
    }

    public void init(@Nullable InitCallback callback) throws RemoteException, NoConnectionException {
        this.callback = callback;
        client.run(new InitHandler(this));
    }

    protected abstract boolean attachDb(String databaseFileName);

    private static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
