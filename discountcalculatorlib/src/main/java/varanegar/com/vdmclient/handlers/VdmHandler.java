package varanegar.com.vdmclient.handlers;

import android.os.Bundle;
import android.os.Handler;

/**
 * Created by A.Torabi on 7/16/2018.
 */

public abstract class VdmHandler extends Handler {
    protected Bundle bundle = new Bundle();

    public int getId() {
        return id;
    }

    private final int id;

    public VdmHandler(int id){
        this.id = id;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
