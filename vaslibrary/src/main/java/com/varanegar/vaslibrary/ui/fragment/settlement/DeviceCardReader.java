package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910CardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.I9000SCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.N910CardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.RahyabCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.SamanKishCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.SepehrCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.TejaratElectronicParsianCardReader;

/**
 * Created by A.Torabi on 11/11/2018.
 */

public abstract class DeviceCardReader {
    private final Context context;

    public DeviceCardReader(Context context) {
        this.context = context;
    }

    public static DeviceCardReader getDeviceCardReader(Activity context) {
        if (Build.MODEL.equals("KS8223"))
            return new RahyabCardReader(context);
        if (Build.MODEL.equals("i9000S"))
            return new I9000SCardReader((VasActivity) context);
        if (Build.MODEL.equals("A930"))
            return new SamanKishCardReader((VasActivity) context);
        if (Build.MODEL.equals("A910"))
            return new A910CardReader((VasActivity) context);
        if (Build.MODEL.equals("P1000"))
            return new TejaratElectronicParsianCardReader((VasActivity) context);
        if (Build.MODEL.equalsIgnoreCase("Sepehr A1"))
            return new SepehrCardReader((VasActivity) context);
        if (Build.MODEL.equalsIgnoreCase("p3"))
            return new SepehrCardReader((VasActivity) context);
        if (Build.MODEL.equals("N910"))
            return new N910CardReader((VasActivity) context);
        return null;
    }

    public abstract void runTransaction(@NonNull TransactionData td, @Nullable ICardReaderResult result);

    public Context getContext() {
        return context;
    }

    public abstract void dispose();
}
