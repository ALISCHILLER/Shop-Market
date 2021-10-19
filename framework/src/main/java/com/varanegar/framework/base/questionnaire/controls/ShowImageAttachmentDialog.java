package com.varanegar.framework.base.questionnaire.controls;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.varanegar.framework.R;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;

import java.io.File;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/2/2017.
 */

public class ShowImageAttachmentDialog extends CuteDialogWithToolbar {
    private String imagePath;

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_attachment_layout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        imagePath = getArguments().getString("3a787ce6-885d-41f4-a5a5-a5cb045473fe");
        imageView.setImageResource(R.drawable.ic_zoom_in_green_900_24dp);
        Picasso.with(getContext()).load(new File(imagePath)).resize(400, 300).onlyScaleDown().memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        return view;
    }

    public void setImagePath(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("3a787ce6-885d-41f4-a5a5-a5cb045473fe", path);
        setArguments(bundle);
    }
}
