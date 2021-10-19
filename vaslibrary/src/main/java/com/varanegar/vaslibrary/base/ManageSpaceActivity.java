package com.varanegar.vaslibrary.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.image.ImageManager;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/13/2018.
 */

public class ManageSpaceActivity extends Activity {
    private CheckBox clearAllBackupsCheckBox;
    private CheckBox clearAllPicturesCheckBox;
    private CheckBox clearAllDataAndSettingsCheckBox;
    private Button okBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_space_activity);
        clearAllBackupsCheckBox = findViewById(R.id.clear_all_backups_check_box);
        clearAllPicturesCheckBox = findViewById(R.id.clear_all_pictures_check_box);
        clearAllDataAndSettingsCheckBox = findViewById(R.id.clear_all_data_and_settings_check_box);
        clearAllDataAndSettingsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    clearAllBackupsCheckBox.setChecked(true);
                    clearAllBackupsCheckBox.setEnabled(false);
                    clearAllPicturesCheckBox.setChecked(true);
                    clearAllPicturesCheckBox.setEnabled(false);
                } else {
                    clearAllBackupsCheckBox.setEnabled(true);
                    clearAllPicturesCheckBox.setEnabled(true);
                }
            }
        });

        okBtn = findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clearAllDataAndSettingsCheckBox.isChecked() && !clearAllPicturesCheckBox.isChecked() && !clearAllBackupsCheckBox.isChecked()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(ManageSpaceActivity.this);
                    dialog.setTitle(R.string.error);
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.no_option_is_selected);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                    return;
                }

                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(ManageSpaceActivity.this);
                cuteMessageDialog.setIcon(Icon.Warning);
                cuteMessageDialog.setTitle(R.string.warning);
                cuteMessageDialog.setMessage(R.string.are_you_sure);
                cuteMessageDialog.setNegativeButton(R.string.no, null);
                cuteMessageDialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clearAllDataAndSettingsCheckBox.isChecked()) {
                            CuteMessageDialog dialog = new CuteMessageDialog(ManageSpaceActivity.this);
                            dialog.setIcon(Icon.Warning);
                            dialog.setTitle(R.string.are_you_sure);
                            dialog.setMessage(R.string.all_data_and_order_details_will_be_ereased);
                            dialog.setNegativeButton(R.string.cancel, null);
                            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    clearAppData();
                                    Timber.d("User cleared all data!");
                                }
                            });
                            dialog.show();

                        }

                        if (clearAllPicturesCheckBox.isChecked()) {
                            try {
                                ImageManager.deleteAllImages(ManageSpaceActivity.this);
                                Timber.d("User cleared all pictures!");
                            } catch (Exception ex) {
                                Timber.e(ex);
                            }
                        }

                        if (clearAllBackupsCheckBox.isChecked()) {
                            BackupManager.wipeAllBackups(ManageSpaceActivity.this);
                            Timber.d("User cleared all backups!");
                        }
                    }
                });
                cuteMessageDialog.show();
            }
        });
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ActivityManager activityManager = ((ActivityManager) getSystemService(ACTIVITY_SERVICE));
                if (activityManager != null)
                    activityManager.clearApplicationUserData();
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("pm clear " + packageName);
                } catch (IOException ex) {
                    Timber.e(ex);
                    CuteMessageDialog dialog = new CuteMessageDialog(ManageSpaceActivity.this);
                    dialog.setTitle(R.string.error);
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.failed_to_clear_data);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
            }

        } catch (Exception e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(ManageSpaceActivity.this);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(R.string.failed_to_clear_data);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
