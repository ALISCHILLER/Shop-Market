package com.varanegar.vaslibrary.model.targetbase;

import android.content.Context;

import com.varanegar.vaslibrary.R;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/19/2017.
 */

public class TargetBase {
    public static UUID Dealer = UUID.fromString("32CFB05C-0E08-46D3-BD12-50DB65689EA5");
    public static UUID Customer = UUID.fromString("FF9CE948-65F8-4BF6-969C-59F07DCA6FF0");
    public static String getName (Context context, UUID id) {
        if (id.equals(Dealer))
            return context.getString(R.string.dealer);
        else if (id.equals(Customer))
            return context.getString(R.string.customer);
        else
            return "";
    }
}
