package com.varanegar.vaslibrary.model.returnType;

import android.content.Context;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.R;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */

public class ReturnType {
    public static UUID WithRef = UUID.fromString("229A85AA-747A-4F8E-A196-6C061101FD50");
    public static UUID WithoutRef = UUID.fromString("E182C4C9-A3D6-4570-8243-C50F867A00FE");
    public static UUID WithRefReplacementRegistration = UUID.fromString("40D9E224-2EDE-4161-8EFA-CC114AF73078");
    public static UUID WithoutRefReplacementRegistration = UUID.fromString("2808F52E-8EB3-45CB-8E86-3A661DEC9CCC");
    public static UUID WithRefFromRequest = UUID.fromString("7d09a398-2118-4d21-aecd-7e8e17eb65ee");
    public static UUID WithoutRefFromRequest = UUID.fromString("66ce9160-f3f5-4cda-81ec-646eb032ccf5");
    public static UUID Well = UUID.fromString("CFD30F1C-69B6-47C3-8E01-8AB5768B6907");
    public static UUID Waste = UUID.fromString("73B8AE91-EA32-407E-B7C6-57AC185A3B8B");
    public static String getName(Context context, UUID id){
        if (id.equals(WithoutRef))
            return context.getString(R.string.without_reference);
        if (id.equals(WithRef))
            return context.getString(R.string.with_reference);
        if (id.equals(Well))
            return context.getString(R.string.well);
        if (id.equals(Waste))
            return context.getString(R.string.waste);
        if (id.equals(WithoutRefReplacementRegistration))
            return context.getString(R.string.without_ref_replacement_registration);
        if (id.equals(WithRefReplacementRegistration))
            return context.getString(R.string.with_ref_replacement_registration);
        return "";
    }

}
