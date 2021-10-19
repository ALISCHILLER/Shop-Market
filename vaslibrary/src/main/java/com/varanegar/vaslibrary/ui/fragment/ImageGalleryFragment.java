package com.varanegar.vaslibrary.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.picture.PictureFileManager;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModel;

import java.util.UUID;

/**
 * Created by A.Torabi on 10/24/2017.
 */

public class ImageGalleryFragment extends CuteDialog {

    private UUID customerPictureSubjectId;
    private ViewPager imagesViewPager;
    private PictureCustomerViewModel pictureCustomerViewModel;
    private ImagesPagerAdapter adapter;

    public interface OnDataSetChanged {
        void onChanged();
    }

    public OnDataSetChanged onDataSetChanged;

    public void setCustomerPictureSubjectId(@NonNull UUID customerPictureSubjectId) {
        Bundle bundle = new Bundle();
        bundle.putString("fd515683-a607-4a78-a1c5-0e4240c3e409", customerPictureSubjectId.toString());
        setArguments(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerPictureSubjectId = UUID.fromString(getArguments().getString("fd515683-a607-4a78-a1c5-0e4240c3e409"));
        final PictureCustomerViewManager manager = new PictureCustomerViewManager(getContext());
        pictureCustomerViewModel = manager.getItem(customerPictureSubjectId);
        if (pictureCustomerViewModel == null)
            throw new NullPointerException("Customer Picture Subject id not found");
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        imagesViewPager = (ViewPager) view.findViewById(R.id.images_view_pager);
        adapter = new ImagesPagerAdapter(getContext(), pictureCustomerViewModel.getFileIds());
        imagesViewPager.setAdapter(adapter);
        TextView subjectTextView = (TextView) view.findViewById(R.id.subject_text_view);
        subjectTextView.setText(pictureCustomerViewModel.Title);
        ImageView closeImageView = (ImageView) view.findViewById(R.id.close_image_view);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ImageView deleteImageView = (ImageView) view.findViewById(R.id.delete_image_view);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Warning);
                dialog.setTitle(R.string.warning);
                dialog.setMessage(R.string.are_you_sure);
                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PictureFileManager pictureFileManager = new PictureFileManager(getContext());
                        UUID currentFileId = adapter.getItem(imagesViewPager.getCurrentItem());
                        if (currentFileId != null) {
                            try {
                                pictureFileManager.deleteFile(currentFileId);
                                pictureCustomerViewModel = manager.getItem(customerPictureSubjectId);
                                if (pictureCustomerViewModel.FileCount == 0)
                                    ImageGalleryFragment.this.dismiss();
                                else {
                                    adapter = new ImagesPagerAdapter(getContext(), pictureCustomerViewModel.getFileIds());
                                    imagesViewPager.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                                if (onDataSetChanged != null)
                                    onDataSetChanged.onChanged();
                            } catch (Exception ex) {
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setIcon(Icon.Error);
                                dialog.setTitle(R.string.error);
                                dialog.setMessage(R.string.error_deleting_picture);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                        }
                    }
                });
                dialog.setNegativeButton(R.string.no, null);
                dialog.show();

            }
        });
        return view;
    }


}
