package com.varanegar.trackingviewer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.ui.dialog.ImportDialogFragment;

/**
 * Created by A.Torabi on 10/22/2018.
 */

public class ImportBackupFragment extends VaranegarFragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_backup_layout,container,false);
        view.findViewById(R.id.restore_backup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BackupManager.getList(getContext(),null).size() > 0) {
                    ImportDialogFragment importDialog = new ImportDialogFragment();
                    importDialog.show(getChildFragmentManager(), "ImportDialogFragment");
                } else {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.there_is_no_backup_file);
                    dialog.setIcon(Icon.Alert);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
            }
        });

        return view;
    }
}
