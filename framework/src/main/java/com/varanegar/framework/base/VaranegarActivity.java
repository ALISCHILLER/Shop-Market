package com.varanegar.framework.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.varanegar.framework.R;
import com.varanegar.framework.base.logging.LogConfig;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Created by atp on 2/25/2017.
 */

public abstract class VaranegarActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    protected abstract boolean checkStoragePermission();

    protected abstract boolean checkCameraPermission();

    protected abstract boolean checkLocationPermission();

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int storagePermission = PackageManager.PERMISSION_GRANTED;
            int cameraPermission = PackageManager.PERMISSION_GRANTED;
            int locationPermission = PackageManager.PERMISSION_GRANTED;
            int bgLocationPermission = PackageManager.PERMISSION_GRANTED;
            if (checkStoragePermission())
                storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCameraPermission())
                cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (checkLocationPermission()) {
                locationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ||
                        (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q &&
                                locationPermission == PackageManager.PERMISSION_GRANTED))
                    bgLocationPermission = checkSelfPermission(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED
                    || cameraPermission != PackageManager.PERMISSION_GRANTED
                    || locationPermission != PackageManager.PERMISSION_GRANTED
                    || bgLocationPermission != PackageManager.PERMISSION_GRANTED) {

                List<String> permissions = new ArrayList<>();
                if (checkStoragePermission())
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checkCameraPermission())
                    permissions.add(Manifest.permission.CAMERA);
                if (checkLocationPermission()) {
                    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
                        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    else if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.Q &&
                            locationPermission == PackageManager.PERMISSION_GRANTED)
                        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
                String[] p = new String[permissions.size()];
                p = permissions.toArray(p);
                ActivityCompat.requestPermissions(this, p, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                boolean granted = true;
                for (int i = 0; i < grantResults.length; i++
                ) {
                    int grant = grantResults[i];
                    String p = permissions[i];
                    if (p.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkStoragePermission()) {
                        if (grant == PackageManager.PERMISSION_DENIED) {
                            granted = false;
                            Toast.makeText(VaranegarActivity.this,
                                    getString(R.string.storage_permission_denied),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    } else if (p.equals(Manifest.permission.CAMERA) && checkCameraPermission()) {
                        if (grant == PackageManager.PERMISSION_DENIED) {
                            granted = false;
                            Toast.makeText(VaranegarActivity.this,
                                    getString(R.string.camera_permission_denied),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    } else if (p.equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                            checkLocationPermission()) {
                        if (grant == PackageManager.PERMISSION_DENIED) {
                            granted = false;
                            Toast.makeText(VaranegarActivity.this,
                                    getString(R.string.location_permission_denied),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else
                            checkPermissions();
                    } else if (p.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                            checkLocationPermission()) {
                        if (grant == PackageManager.PERMISSION_DENIED) {
                            granted = false;
                            Timber.e(getString(R.string.background_location_permission_denied));
                        }
                    }
                }
                if (granted) {
                    createLogConfig();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected abstract LogConfig createLogConfig();

}
