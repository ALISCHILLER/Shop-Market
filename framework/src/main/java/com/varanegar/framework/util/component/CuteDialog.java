package com.varanegar.framework.util.component;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;

/**
 * Created by atp on 12/31/2016.
 */
public abstract class CuteDialog extends DialogFragment {
    private SizingPolicy sizePolicy = SizingPolicy.Medium;

    public enum SizingPolicy {
        WrapContent,
        Medium,
        Maximum,
        MatchParent
    }

    public MainVaranegarActivity getVaranegarActvity() {
        return (MainVaranegarActivity) getActivity();
    }

    public void setSizingPolicy(SizingPolicy policy) {
        this.sizePolicy = policy;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       // getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_background);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        boolean multipane = getActivity().getResources().getBoolean(R.bool.multipane);
        if (sizePolicy == SizingPolicy.Medium) {
            if (multipane) {
                int height = size.y;
                double r = (double) width / (double) height;
                if (r > 1)
                    window.setLayout((int) (width * 0.9 / r), WindowManager.LayoutParams.WRAP_CONTENT);
                else
                    window.setLayout((int) (width * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
            } else
                window.setLayout((int) (width * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        } else if (sizePolicy == SizingPolicy.Maximum) {
            window.setLayout((int) (width * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        } else if (sizePolicy == SizingPolicy.MatchParent) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        window.setGravity(Gravity.CENTER);

    }
}
