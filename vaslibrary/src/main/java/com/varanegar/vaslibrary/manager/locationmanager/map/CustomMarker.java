package com.varanegar.vaslibrary.manager.locationmanager.map;

import android.app.Activity;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import java.util.UUID;

/**
 * Created by A.Torabi on 6/9/2018.
 */

public abstract class CustomMarker {
    public Activity getActivity() {
        return activity;
    }

    private final Activity activity;

    public UUID getUniqueId() {
        return UniqueId;
    }

    private final UUID UniqueId;

    public CustomMarker(@NonNull Activity activity) {
        this.activity = activity;
        this.UniqueId = UUID.randomUUID();
    }

    public abstract View onCreateView(@NonNull LayoutInflater inflater);

    public View onCreateInfoView(@NonNull LayoutInflater inflater) {
        return null;
    }

    public Bitmap createBitMap() {
        View view = onCreateView(activity.getLayoutInflater());
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    public abstract float zIndex();

}
