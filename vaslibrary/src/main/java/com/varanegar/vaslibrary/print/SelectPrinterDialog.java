package com.varanegar.vaslibrary.print;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;

import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 3/15/2018.
 */

public class SelectPrinterDialog extends CuteDialogWithToolbar {
    private BaseRecyclerView recyclerView;
    private SelectionRecyclerAdapter<PrinterModel> adapter;
    private CallBack callBack;
    private List<PrinterModel> printerModelList;
    private PrinterManager printerManager;
    private CheckBox checkBox;

    public interface CallBack {
        void onSelected(PrinterModel printerModel);

        void onCancel();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (callBack != null)
            callBack.onCancel();
        printerManager.unRegisterCallBack();
        dismissAllowingStateLoss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printerManager = new PrinterManager(getContext());
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_printer_dialog, container, false);
        setTitle(R.string.printer_selection);
        recyclerView = (BaseRecyclerView) view.findViewById(R.id.printers_recycler_view);
        checkBox = (CheckBox) view.findViewById(R.id.check_box);
        refreshDialog();

        printerManager.registerCallBack(new PrinterManager.ScanCallBack() {
            @Override
            public void onFinish() {
                view.findViewById(R.id.scan_progress_bar).setVisibility(View.GONE);
                view.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                refreshDialog();
            }

            @Override
            public void onStart() {
                view.findViewById(R.id.scan_progress_bar).setVisibility(View.VISIBLE);
                view.findViewById(R.id.main_layout).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.print_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getSelectedItem() == null)
                    Toast.makeText(getContext(), R.string.please_select_a_printer, Toast.LENGTH_SHORT).show();
                else {
                    if (checkBox.isChecked()) {
                        try {
                            printerManager.setDefaultPrinter(adapter.getSelectedItem());
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }

                    if (callBack != null) {
                        callBack.onSelected(adapter.getSelectedItem());
                        callBack = null;
                    }
                    dismiss();
                }
            }
        });
        view.findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                printerManager.startScanner();
            }
        });


        if (adapter.getItemCount() > 0)
            checkBox.setVisibility(View.VISIBLE);
        else
            checkBox.setVisibility(View.INVISIBLE);

        return view;
    }

    private void refreshDialog() {
        printerModelList = printerManager.listAvailablePrinters();
        adapter = new SelectionRecyclerAdapter<PrinterModel>(getVaranegarActvity(), printerModelList, false);
        int isDefault = Linq.findFirstIndex(printerModelList, new Linq.Criteria<PrinterModel>() {
            @Override
            public boolean run(PrinterModel item) {
                return item.IsDefault;
            }
        });
        adapter.select(isDefault);
        recyclerView.setAdapter(adapter);
    }
}
