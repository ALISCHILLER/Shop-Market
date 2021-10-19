package varanegar.com.vdmclient.handlers;

import android.os.Message;

/**
 * Created by A.Torabi on 7/16/2018.
 */

public abstract class VersionHandler extends VdmHandler {
    public VersionHandler() {
        super(100);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == getId()) {
            String version = msg.getData().getString("version");
            if (version != null && !version.isEmpty())
                done(version);
            else
                failed();
        }
    }

    protected abstract void done(String version);

    protected abstract void failed();
}
