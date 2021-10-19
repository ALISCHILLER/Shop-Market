package com.varanegar.framework.base.account;

/**
 * Created by atp on 6/6/2017.
 */

public interface OnError {
    void onAuthenticationFailure(String error, String description);

    void onNetworkFailure(Throwable t);
}
