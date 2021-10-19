package com.varanegar.vaslibrary.model.amounttype;

import android.content.Context;

import com.varanegar.vaslibrary.R;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/19/2017.
 */

public class AmountType {
    public static UUID NetAmount = UUID.fromString("AAA7EC3C-A040-42B6-99F0-3EE81E60285E");
    public static UUID Amount = UUID.fromString("F9109FC2-195C-410C-B323-E1728923F2A6");
    public static UUID AmountAfterReduceTaxAndAdd = UUID.fromString("05B8760D-40BB-48DF-89B4-B5DF085534B0");
    public static UUID AmountAfterReduceReturn = UUID.fromString("2A866280-A22F-4B8E-A8E1-94D3F1095750");
    public static String getName(Context context, UUID id) {
        if (id.equals(NetAmount))
            return context.getString(R.string.net_amount);
        else if (id.equals(Amount))
            return context.getString(R.string.amount);
        else if (id.equals(AmountAfterReduceTaxAndAdd))
            return context.getString(R.string.amount_after_reduce_tax_and_add);
        else if (id.equals(AmountAfterReduceReturn))
            return context.getString(R.string.amount_after_reduce_return);
        else
            return "";
    }
}
