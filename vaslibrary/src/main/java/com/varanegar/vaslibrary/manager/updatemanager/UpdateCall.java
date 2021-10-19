package com.varanegar.vaslibrary.manager.updatemanager;


import android.content.Context;

import com.varanegar.framework.util.Linq;

import java.util.List;

/**
 * Created by atp on 6/7/2017.
 */
public abstract class UpdateCall {

    protected void onFinish() {

    }

    protected void onSuccess() {

    }

    protected void onFailure(String error) {

    }

    protected void onError(String error) {

    }

    public final void success() {
        onFinish();
        onSuccess();
    }

    public final void failure(String error) {
        onFinish();
        onFailure(error);
    }

    public final void error(String error) {
        onFinish();
        onError(error);
    }

    public static String merge(List<String> errors) {
        String message = Linq.merge(errors, new Linq.Merge<String>() {
            @Override
            public String run(String item1, String item2) {
                return item1 + System.getProperty("line.separator") + item2;
            }
        });
        return message;
    }

}
