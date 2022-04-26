package com.varanegar.vaslibrary.ui.dialog.new_dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeViewModel;

import java.util.List;

public class SingleChoiceDialog extends DialogFragment {
    int position = 0; //default selected position

    private Context _context;
    private String _title;
    private String[] _list;

    public SingleChoiceDialog(Context context, String title, String[] list){
        this._context=context;
        this._title=title;
        this._list=list;
    }

    public interface SingleChoiceListener {
        void onPositiveButtonClicked(String[] list, int position);

        void onNegativeButtonClicked();
    }

    SingleChoiceListener mListener;
    public void addItemClickListener(SingleChoiceListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);



        builder.setTitle(_title)
                .setSingleChoiceItems(_list, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        position = i;
                    }
                })
                .setPositiveButton("تاببد", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onPositiveButtonClicked(_list, position);
                    }
                })
                .setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onNegativeButtonClicked();
                    }
                });

        return builder.create();
    }
}
