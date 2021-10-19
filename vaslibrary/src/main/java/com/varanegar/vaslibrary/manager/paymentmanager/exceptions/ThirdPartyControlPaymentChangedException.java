package com.varanegar.vaslibrary.manager.paymentmanager.exceptions;

import androidx.annotation.Nullable;

import com.varanegar.vaslibrary.enums.ThirdPartyPaymentTypes;

public class ThirdPartyControlPaymentChangedException extends Exception {
    private final @Nullable ThirdPartyPaymentTypes ThirdPartyPaymentType;
    private final @Nullable String pinCode;

    public ThirdPartyControlPaymentChangedException(String msg , @Nullable ThirdPartyPaymentTypes paymentType, @Nullable String pinCode) {
        super(msg);
        this.ThirdPartyPaymentType = paymentType;
        this.pinCode = pinCode;
    }

    public @Nullable ThirdPartyPaymentTypes getThirdPartyPaymentType() {
        return ThirdPartyPaymentType;
    }

    public @Nullable String getPinCode() {
        return pinCode;
    }
}
