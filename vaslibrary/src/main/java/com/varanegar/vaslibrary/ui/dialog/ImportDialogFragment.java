package com.varanegar.vaslibrary.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupInfo;
import com.varanegar.vaslibrary.base.BackupInfoFile;
import com.varanegar.vaslibrary.base.BackupManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/3/2017.
 */

public class ImportDialogFragment extends CuteAlertDialog {
    private BaseSelectionRecyclerAdapter<BackupInfoFile> adapter;
    private BackupManager.BackupType backupType;

    public void setBackupType(BackupManager.BackupType backupType) {
        Bundle bundle = new Bundle();
        bundle.putString("BACKUP_TYPE", String.valueOf(backupType.ordinal()));
        setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String bt = bundle.getString("BACKUP_TYPE", null);
            if (bt != null) {
                if (bt.equals("0"))
                    backupType = BackupManager.BackupType.Full;
                else
                    backupType = BackupManager.BackupType.Partial;
            }
        }
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.import_data);
        setSizingPolicy(SizingPolicy.Maximum);
        final View view = inflater.inflate(R.layout.dialog_import_data, viewGroup, true);
        final List<File> files = BackupManager.getList(getContext(), backupType);
        List<BackupInfoFile> backupInfoList = new ArrayList<>();
        for (File file :
                files) {
            BackupInfo backupInfo = BackupManager.getBackupInfo(file.getAbsolutePath());
            BackupInfoFile backupInfoFile = new BackupInfoFile();
            backupInfoFile.backupInfo = backupInfo;
            backupInfoFile.file = file;
            backupInfoList.add(backupInfoFile);
        }
        BaseRecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new BaseSelectionRecyclerAdapter<BackupInfoFile>(getVaranegarActvity(), backupInfoList, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_backup_info, parent, false);
                return new BackupInfoViewHolder(view, this, getContext());
            }
        };

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void ok() {
        final BackupInfoFile backupInfo = adapter.getSelectedItem();
        if (backupInfo != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(R.string.please_wait);
            progressDialog.setMessage(getString(R.string.import_data));
            progressDialog.setCancelable(false);
            progressDialog.show();
            final Handler handler = new Handler();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Timber.d("Trying to import the backup file:");
                        Timber.d(VaranegarGsonBuilder.build().create().toJson(backupInfo.backupInfo));
                        BackupManager.importData(getContext(), backupInfo.file.getAbsolutePath());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setMessage(R.string.import_finished_successfully);
                                dialog.setTitle(R.string.import_data);
                                dialog.setIcon(Icon.Success);
                                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getVaranegarActvity().finish();
                                    }
                                });
                                dialog.show();
                            }
                        });
                    } catch (Exception e) {
                        Timber.e(e);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setMessage(R.string.import_failed);
                                dialog.setTitle(R.string.import_data);
                                dialog.setIcon(Icon.Error);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                        });
                    } finally {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });
            thread.start();

        } else {
            Toast.makeText(getContext(), R.string.please_select_backup_file, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void cancel() {

    }

    class BackupInfoViewHolder extends BaseViewHolder<BackupInfoFile> {

        private final TextView dateTextView;
        private final TextView dbVersionTextView;
        private final TextView packageNameTextview;
        private final TextView packageVersionTextView;
        private final TextView visitorNameTextView;
        private final TextView dataOwnerIdTextView;
        private final TextView dataCenterOwnerIdTextView;
        private final TextView packageVersionNameTextView;
        private final TextView typeTextView;

        public BackupInfoViewHolder(View itemView, BaseRecyclerAdapter<BackupInfoFile> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            dbVersionTextView = (TextView) itemView.findViewById(R.id.db_version_text_view);
            packageNameTextview = (TextView) itemView.findViewById(R.id.package_name_text_view);
            packageVersionTextView = (TextView) itemView.findViewById(R.id.package_version_text_view);
            packageVersionNameTextView = (TextView) itemView.findViewById(R.id.package_version_name_text_view);
            visitorNameTextView = (TextView) itemView.findViewById(R.id.visitor_name_text_view);
            dataOwnerIdTextView = (TextView) itemView.findViewById(R.id.data_owner_id_text_view);
            dataCenterOwnerIdTextView = (TextView) itemView.findViewById(R.id.data_center_owner_id_text_view);
            typeTextView = (TextView) itemView.findViewById(R.id.type_text_view);

        }

        @Override
        public void bindView(final int position) {
            try {
                final String packageName = getContext().getPackageName();
                final int appVersionCode = getContext().getApplicationContext().getPackageManager().getPackageInfo(packageName, 0).versionCode;
                final BackupInfoFile backupInfo = recyclerAdapter.get(position);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (packageName.equals(backupInfo.backupInfo.PackageName)) {
                            adapter.notifyItemClicked(getAdapterPosition());
                        } else {
                            Toast.makeText(getContext(), R.string.this_backup_is_not_for_this_application, Toast.LENGTH_SHORT).show();
                            adapter.notifyItemClicked(getAdapterPosition());
                        }
                    }
                });
                if (appVersionCode == backupInfo.backupInfo.AppVersionCode && packageName.equals(backupInfo.backupInfo.PackageName)) {
                    itemView.findViewById(R.id.ok_layout).setVisibility(View.VISIBLE);
                    itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
                } else {
                    itemView.findViewById(R.id.ok_layout).setVisibility(View.GONE);
                    itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light));
                }
                if (adapter.getSelectedPosition() == position) {
                    itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light));
                }
                dateTextView.setText(DateHelper.toString(backupInfo.backupInfo.Date, DateFormat.Complete, Locale.US));
                dbVersionTextView.setText(String.valueOf(backupInfo.backupInfo.DatabaseVersion));
                if (backupInfo.backupInfo.IsFullBackup == null)
                    typeTextView.setText(getString(R.string.normal));
                else if (backupInfo.backupInfo.IsFullBackup)
                    typeTextView.setText(getString(R.string.full_backup));
                else
                    typeTextView.setText(getString(R.string.partial_backup));
                packageNameTextview.setText(backupInfo.backupInfo.PackageName);
                packageVersionTextView.setText(" (" + String.valueOf(backupInfo.backupInfo.AppVersionCode) + ") ");
                packageVersionNameTextView.setText(String.valueOf(backupInfo.backupInfo.AppVersionName));
                visitorNameTextView.setText(backupInfo.backupInfo.UserName);
                if (backupInfo.backupInfo.OwnerKeys != null) {
                    dataOwnerIdTextView.setText(backupInfo.backupInfo.OwnerKeys.DataOwnerKey);
                    dataCenterOwnerIdTextView.setText(backupInfo.backupInfo.OwnerKeys.DataOwnerCenterKey);
                } else {
                    dataOwnerIdTextView.setText(getContext().getString(R.string.not_available));
                    dataCenterOwnerIdTextView.setText(getContext().getString(R.string.not_available));
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
