package com.varanegar.framework.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.varanegar.framework.R;
import com.varanegar.framework.util.component.drawer.DrawerAdapter;

/**
 * Created by atp on 1/7/2017.
 */
public abstract class MainVaranegarActivity extends VaranegarActivity {

    public boolean isVisible() {
        return isVisible;
    }

    private boolean isVisible;

    public DrawerAdapter getDrawerAdapter() {
        return drawerAdapter;
    }


    DrawerAdapter drawerAdapter;
    boolean finish = false;

    protected void setToolbarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            findViewById(R.id.fake_status_bar).setVisibility(View.VISIBLE);
        }
    }

    public void setEnabled(boolean enabled) {
        findViewById(R.id.content_frame).setClickable(enabled);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varanegar_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen())
            closeDrawer();
        else if (getCurrentFragment() instanceof VaranegarFragment)
            ((VaranegarFragment) getCurrentFragment()).onBackPressed();
        else
            popFragment();
    }

    /**
     * provides a custom view for drawerAdapter menu. This method can be used if you want to create a very custom layout for drawerAdapter menu
     *
     * @param layout the custom layout that you have created
     * @return returns the view from the layout.
     */
    public View setDrawerLayout(@LayoutRes int layout) {
        LinearLayout drawerView = (LinearLayout) findViewById(R.id.drawer_view);
        drawerView.removeAllViews();
        View view = getLayoutInflater().inflate(layout, drawerView);
        drawerView.invalidate();
        return view;
    }

    public void removeDrawer() {
        LinearLayout drawerView = (LinearLayout) findViewById(R.id.drawer_view);
        drawerView.removeAllViews();
        drawerView.invalidate();
        this.drawerAdapter = null;
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * Provides an adapter for drawerAdapter menu
     *
     * @param drawerAdapter
     */
    public void setDrawerAdapter(DrawerAdapter drawerAdapter) {
        LinearLayout drawerView = (LinearLayout) findViewById(R.id.drawer_view);
        this.drawerAdapter = drawerAdapter;
        drawerView.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.drawer_layout, drawerView);
        ListView drawerListView = (ListView) view.findViewById(R.id.drawer_list_view);
        drawerListView.setAdapter(drawerAdapter);
        drawerView.invalidate();
    }

    /**
     * چک کردن باز بودن یا بسته بودنDrawer و باز کردن یا بستن  Drawer
     */
    public void toggleDrawer() {
        if (isDrawerOpen())
            closeDrawer();
        else
            openDrawer();
    }

    public void closeDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout drawerView = (LinearLayout) findViewById(R.id.drawer_view);
        drawerLayout.closeDrawer(drawerView);
    }

    public void openDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout drawerView = (LinearLayout) findViewById(R.id.drawer_view);
        drawerLayout.openDrawer(drawerView);
    }

    /**
     * opens drawer after a delay. Use this method if you need to open the drawer automatically after fragment started
     *
     * @param delay milliseconds
     */
    public void openDrawer(final int delay) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(delay);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openDrawer();
                    }
                });
            }
        });
        th.start();
    }

    public void setDrawerLockMode(int lock) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(lock);
    }

    public boolean isDrawerOpen() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout drawerView = (LinearLayout) findViewById(R.id.drawer_view);
        return drawerLayout.isDrawerOpen(drawerView);
    }

    public void putFragment(VaranegarFragment fragment) {
        putFragment(fragment, false);
    }

    public void pushFragment(VaranegarFragment fragment) {
        pushFragment(fragment, false);
    }

    public void putFragment(VaranegarFragment fragment, boolean force) {
        if (fragment == null) {
            throw new NullPointerException();
        }
        Fragment curr = getCurrentFragment();
        if (curr != null) {
            if ((fragment.getClass().getName().equals(curr.getClass().getName())) && !force) {
                return;
            }
        }

        int c = fragmentCount();
        if (c > 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.remove(getCurrentFragment());
            transaction.commit();
            getSupportFragmentManager().popBackStack();
        }
        pushFragment(fragment, force);
    }

    public void pushFragment(VaranegarFragment fragment, boolean force) {
        if (fragment == null) {
            throw new NullPointerException();
        }
        Fragment curr = getCurrentFragment();
        if (curr != null) {
            if ((fragment.getClass().getName().equals(curr.getClass().getName())) && !force) {
                return;
            }
            ((VaranegarFragment) curr).addArgument("097b873b-98b3-4733-b2c4-a95053dcdc1e",
                    false);
        }
        fragment.addArgument("097b873b-98b3-4733-b2c4-a95053dcdc1e", true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment, fragment.getClass().getName());
        transaction.addToBackStack(null);
        transaction.commit();
        finish = false;
    }

    public int fragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    public void popFragment() {
        int c = fragmentCount();
        if (c > 1) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(getCurrentFragment());
            transaction.commit();
            getSupportFragmentManager().popBackStack();
        } else {
            if (fragmentCount() == 1) {
                if (finish) {
                    finish();
                } else {
                    finish = true;
                    Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    public void showSnackBar(@StringRes int string, Duration duration) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.content_frame), string, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        if (duration == Duration.Short)
            snackbar.setDuration(Snackbar.LENGTH_SHORT);
        else
            snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showSnackBar(String string, Duration duration) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.content_frame), string, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        if (duration == Duration.Short)
            snackbar.setDuration(Snackbar.LENGTH_SHORT);
        else
            snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public enum Duration {
        Short,
        Long
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }
}
