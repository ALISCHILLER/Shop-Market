package com.varanegar.vaslibrary.ui.dialog.new_dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.vaslibrary.R;

public class ImageUrlViewDialog extends CuteDialogWithToolbar {

    private String _urlImage;
    private ImageView imageView;

    public void setUrlImage(String urlImage){
        this._urlImage=urlImage;
    }
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.image_url_view_dialog,container,false);
        imageView=view.findViewById(R.id.image_view);
        Glide.with(getActivity())
                .load(_urlImage)
                .centerCrop()
                .placeholder(R.drawable.error_message_background)
                .into(imageView);
        return view;
    }
}
