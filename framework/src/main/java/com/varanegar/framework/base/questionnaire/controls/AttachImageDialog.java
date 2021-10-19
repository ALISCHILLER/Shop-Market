package com.varanegar.framework.base.questionnaire.controls;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.varanegar.framework.R;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/1/2017.
 */

public class AttachImageDialog extends CuteDialogWithToolbar {
    public Bitmap getBitmap() {
        return mBitmap;
    }

    private Bitmap mBitmap;
    private CameraView cameraView;


    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attche_image_layout, container, false);
        setTitle(R.string.picture_attachment);
        cameraView = (CameraView) view.findViewById(R.id.camera_view);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        mBitmap = bitmap;
                        if (onAttachment != null)
                            onAttachment.onDone();
                    }
                });
            }
        });
        view.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.capturePicture();
            }
        });
        return view;
    }



    public OnAttachment onAttachment;

    public interface OnAttachment {
        void onDone();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }


}
