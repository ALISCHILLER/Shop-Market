package com.varanegar.vaslibrary.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Torabi on 12/25/2018.
 */

public class BackupConfigAlertDialog extends CuteAlertDialog {
    private CheckBox customerPictureCheckBox;
    private CheckBox questionnaireCheckBox;
    private CheckBox productAndCatalogue;

    public OnBackupConfig onBackupConfig;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.backup_alert_dialog, viewGroup, true);
        customerPictureCheckBox = view.findViewById(R.id.customer_picture_check_box);
        questionnaireCheckBox = view.findViewById(R.id.questionaire_attachment_check_box);
        productAndCatalogue = view.findViewById(R.id.product_and_catalogue_check_box);
        setTitle(R.string.backup_config);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageManager imageManager = new ImageManager(getContext());
                final int[] counts = imageManager.getFileCount(ImageType.CustomerCallPicture, ImageType.QuestionnaireAttachments, ImageType.CatalogLarge, ImageType.CatalogSmall, ImageType.Product, ImageType.ProductGroup);
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            customerPictureCheckBox.setText(customerPictureCheckBox.getText() + " (" + getString(R.string.count) + " = " + String.valueOf(counts[0]) + ") ");
                            questionnaireCheckBox.setText(questionnaireCheckBox.getText() + " (" + getString(R.string.count) + " = " + String.valueOf(counts[1]) + ") ");
                            productAndCatalogue.setText(productAndCatalogue.getText() + " (" + getString(R.string.count) + " = " + String.valueOf(counts[2] + counts[3] + counts[4] + counts[5]) + ") ");
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void ok() {
        List<ImageType> imageTypes = new ArrayList<>();
        if (customerPictureCheckBox.isChecked())
            imageTypes.add(ImageType.CustomerCallPicture);
        if (questionnaireCheckBox.isChecked())
            imageTypes.add(ImageType.QuestionnaireAttachments);
        if (productAndCatalogue.isChecked()) {
            imageTypes.add(ImageType.CatalogLarge);
            imageTypes.add(ImageType.CatalogSmall);
            imageTypes.add(ImageType.Product);
            imageTypes.add(ImageType.ProductGroup);
        }
        if (onBackupConfig != null)
            onBackupConfig.done(imageTypes);
    }

    @Override
    public void cancel() {

    }

    public interface OnBackupConfig {
        public void done(List<ImageType> imageTypes);
    }
}
