package com.varanegar.vaslibrary.ui.fragment;

import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.picture.PictureFileManager;
import com.varanegar.vaslibrary.model.picturesubject.PictureFileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/24/2017.
 */

public class ImagesPagerAdapter extends PagerAdapter {

    private List<UUID> fileIds = new ArrayList<>();
    private final Context context;
    private final PictureFileManager fileManager;

    public ImagesPagerAdapter(Context context, List<UUID> fileIds) {
        if (fileIds == null)
            this.fileIds = new ArrayList<>();
        else
            this.fileIds = fileIds;
        this.context = context;
        fileManager = new PictureFileManager(context);
    }

    @Nullable
    public UUID getItem(int position) {
        return position >= fileIds.size() || position < 0 ? null : fileIds.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_view_pager_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        PictureFileModel file = fileManager.getPictureFile(fileIds.get(position));
        String filename = fileManager.getPictureFileName(file);
        if (new File(filename).exists())
            Timber.d(filename + " exists");
        else
            Timber.d(filename + " not found");
        double ratio = file.getRatio();
        Point point = HelperMethods.getDisplayMetrics(context);
        int w = point.x;
        int h = (int) Math.floor(w / ratio);
        if (file.IsPortrait)
            Picasso.with(context).load(new File(filename)).memoryPolicy(MemoryPolicy.NO_CACHE).resize(h, w).onlyScaleDown().into(imageView);
        else
            Picasso.with(context).load(new File(filename)).memoryPolicy(MemoryPolicy.NO_CACHE).resize(w, h).onlyScaleDown().into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return fileIds.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
