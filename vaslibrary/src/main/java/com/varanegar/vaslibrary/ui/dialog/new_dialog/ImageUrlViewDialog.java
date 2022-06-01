package com.varanegar.vaslibrary.ui.dialog.new_dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.util.rotateimage.MyTransformation;

public class ImageUrlViewDialog extends CuteDialogWithToolbar {

    private String _urlImage;
    private ImageView imageView;
    private Button ok_btn;

    public void setUrlImage(String urlImage){
        this._urlImage=urlImage;
    }
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {

        Glide.get(getActivity()).clearMemory();


        String url=getBaseUrl()+_urlImage;
        Log.e("ImageUrlViewDialog", "onCreateDialogView:"+url );
        View view=inflater.inflate(R.layout.image_url_view_dialog,container,false);
        imageView=view.findViewById(R.id.image_view);
        ok_btn=view.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResult.done();
                dismiss();
            }
        });
        Glide.with(getActivity())
                .load(url)
                .override(1000, 600)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.product_no_image)
//                .placeholder(R.drawable.product_no_image)
                .into(imageView);
        return view;
    }



    public void setOnResult(ImageUrlViewDialog.OnResult onResult){
        this.onResult = onResult;
    }

    private ImageUrlViewDialog.OnResult onResult;

    public interface OnResult {
        void done() ;
        void failed(String error);
    }

    protected String getBaseUrl() {
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel serverAddress =
                sysConfigManager.read(ConfigKey.BaseAddress, SysConfigManager.local);
        if (serverAddress == null)
            return "http://localhost";
        if (serverAddress.Value.endsWith("/"))
            serverAddress.Value = HelperMethods.removeLastChar(serverAddress.Value);
        return serverAddress.Value;
    }


}
