package com.varanegar.framework.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.varanegar.framework.R;
import com.varanegar.framework.util.component.drawer.DrawerAdapter;

import java.util.List;

import timber.log.Timber;

/**
 * Created by atp on 1/14/2017.
 */
public class VaranegarFragment extends Fragment {

    private String fragmentName;

    @Nullable
    public MainVaranegarActivity getVaranegarActvity() {
        Activity activity = getActivity();
        if (activity == null)
            return null;
        return (MainVaranegarActivity) activity;
    }

    /**
     * set a very custom drawer layout
     *
     * @param layout
     */
    @Nullable
    public View setDrawerLayout(@LayoutRes int layout) {
        MainVaranegarActivity varanegarActivity = getVaranegarActvity();
        if (varanegarActivity != null)
            return varanegarActivity.setDrawerLayout(layout);
        else
            return null;
    }

    public void setDrawerAdapter(DrawerAdapter drawer) {
        MainVaranegarActivity varanegarActivity = getVaranegarActvity();
        if (varanegarActivity != null)
            varanegarActivity.setDrawerAdapter(drawer);
    }

    @Nullable
    public DrawerAdapter getDrawerAdapter() {
        MainVaranegarActivity varanegarActivity = getVaranegarActvity();
        if (varanegarActivity != null)
            return varanegarActivity.getDrawerAdapter();
        else
            return null;
    }

    public void remove(@NonNull String id) {
        VaranegarApplication.getInstance().remove(id);
    }

    public void addArgument(@NonNull String key, @NonNull String value) {
        if (getArguments() == null) {
            Bundle bundle = new Bundle();
            bundle.putString(key, value);
            setArguments(bundle);
        } else {
            getArguments().putString(key, value);
        }
    }

    public void addArgument(@NonNull String key, @NonNull List<String> values) {
        if (getArguments() == null) {
            Bundle bundle = new Bundle();
            bundle.putStringArray(key, values.toArray(new String[values.size()]));
            setArguments(bundle);
        } else {
            getArguments().putStringArray(key, values.toArray(new String[values.size()]));
        }
    }

    @Nullable
    protected String getStringArgument(String key) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String arg = bundle.getString(key);
            return arg;
        } else return null;
    }

    public void addArgument(@NonNull String key, boolean value) {
        if (getArguments() == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(key, value);
            setArguments(bundle);
        } else {
            getArguments().putBoolean(key, value);
        }
    }

    protected boolean isNewFragment() {
        try {
            boolean isNew = getArguments().getBoolean("097b873b-98b3-4733-b2c4-a95053dcdc1e", false);
            getArguments().remove("097b873b-98b3-4733-b2c4-a95053dcdc1e");
            return isNew;
        } catch (Exception ex) {
            return true;
        }
    }

    /*
    default value is false
     */
    protected boolean getBooleanArgument(String key) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean arg = bundle.getBoolean(key, false);
            return arg;
        } else return false;
    }

    public void onBackPressed() {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null)
            activity.popFragment();
    }

    private boolean multipan;

    protected boolean isTablet() {
        return multipan;
    }

    private boolean isLandscape;

    protected boolean isLandscape() {
        return isLandscape;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            fragmentName = this.getClass().getName();
            Timber.d("Fragment " + fragmentName + " created.");
        } catch (Exception ex) {
            Timber.e(ex);
        }
        multipan = getResources().getBoolean(R.bool.multipane);
        isLandscape = getResources().getBoolean(R.bool.is_landscape);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Timber.d("Fragment " + fragmentName + " resumed.");
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Timber.d("Fragment " + fragmentName + " paused.");
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Timber.d("Fragment " + fragmentName + " destroyed.");
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    protected void setSoftInputMode(int mode) {
        Activity activity = getActivity();
        if (activity != null)
            activity.getWindow().setSoftInputMode(mode);
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = getActivity().getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(getActivity());
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException ignored) {

        }
    }
}
