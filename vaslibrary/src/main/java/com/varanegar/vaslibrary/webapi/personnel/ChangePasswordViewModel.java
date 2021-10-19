package com.varanegar.vaslibrary.webapi.personnel;

import java.util.UUID;

/**
 * Created by A.Torabi on 6/17/2018.
 */

public class ChangePasswordViewModel {
    public UUID UserId;
    public String OldPassword;
    public String NewPassword;
    public String ConfirmPassword;
}
