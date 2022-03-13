package com.varanegar.framework.base.account;

/**
 * Created by atp on 6/6/2017.
 */

public class Account {
    public Account(String username, String password,String deviceId,String token,String vpnUser){
        this.username = username;
        this.password = password;
        this.deviceId = deviceId;
        this.token = token;
        this.vpnUser=vpnUser;
    }
    public String username;
    public String password;
    public String deviceId ;
    public String token ;
    public String vpnUser;
}
