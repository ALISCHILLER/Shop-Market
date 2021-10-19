package com.varanegar.vaslibrary.model.targettype;

import android.content.Context;

import com.varanegar.vaslibrary.R;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/19/2017.
 */

public class TargetType {
    public static UUID Total = UUID.fromString("44CA6CCB-0F5C-465E-BFB0-0E1B36F7353B");
    public static UUID Product = UUID.fromString("889D4795-770A-421B-AADB-2DF42A78B070");
    public static UUID ProductGroup = UUID.fromString("86201FC5-BFE0-47B5-A395-E89E9E592698");
    public static String getName(Context context, UUID id) {
        if (id.equals(Total))
            return context.getString(R.string.all);
        else if (id.equals(Product))
            return context.getString(R.string.product);
        else if (id.equals(ProductGroup))
            return context.getString(R.string.product_group);
        else
            return "";
    }
}
