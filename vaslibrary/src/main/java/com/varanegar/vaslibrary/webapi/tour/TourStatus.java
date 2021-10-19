package com.varanegar.vaslibrary.webapi.tour;

import android.content.Context;

import com.varanegar.vaslibrary.R;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class TourStatus {
    public static UUID TourStatusTypeId = UUID.fromString("69450E43-B6D0-46C2-929D-3D16561AB99C");
    public static UUID ReadyToSend = UUID.fromString("4F02D89E-12EB-4E2D-B2F3-2C3D4FE68EE1");
    public static UUID Sent = UUID.fromString("767F183D-B6BF-4BF8-A236-AC689AABC06A");
    public static UUID InProgress = UUID.fromString("07D74949-1930-4F8D-AA56-7D4E0A3D5855");
    public static UUID Received = UUID.fromString("0DE50435-EE0E-4658-8547-4BE751814407");
    public static UUID Finished = UUID.fromString("ACCA759C-B61A-49EA-BF17-60ED940AE2F0");
    public static UUID Canceled = UUID.fromString("0F9F22E9-205C-428A-9629-E1D2E6F3284B");
    public static UUID Deactivated = UUID.fromString("92D208E6-ED9A-4D8A-8B4E-C0AB5386E7B5");

    public static String getStatusName(Context context, UUID status) {
        if (status == null)
            return context.getString(R.string.unKnown);
        else if (ReadyToSend.equals(status))
            return context.getString(R.string.ready_ro_send);
        else if (Sent.equals(status))
            return context.getString(R.string.sent);
        else if (InProgress.equals(status))
            return context.getString(R.string.tour_in_progress);
        else if (Received.equals(status))
            return context.getString(R.string.received);
        else if (Finished.equals(status))
            return context.getString(R.string.finished);
        else if (Canceled.equals(status))
            return context.getString(R.string.canceled);
        else if (Deactivated.equals(status))
            return context.getString(R.string.deactivated);
        else
            return context.getString(R.string.unKnown);
    }
}
