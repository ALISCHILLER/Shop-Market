package com.varanegar.vaslibrary.ui.fragment.picturecustomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Size;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.picture.PictureFileManager;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModelRepository;
import com.varanegar.vaslibrary.model.picturesubject.PictureDemandType;
import com.varanegar.vaslibrary.ui.fragment.ImageGalleryFragment;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/4/2017.
 */

public class CameraFragment extends VisitFragment {
    private ImageView previewImageView;
    private View imagePreviewLayout;
    private UUID customerId;
    private PictureCustomerViewModel selectedSubject;
    private View takePicture;
    BaseSelectionRecyclerAdapter<PictureCustomerViewModel> adapter;
    private CameraView cameraView;
    Bitmap mBitmap;
    private TextView subjectTextView;
    private int mWidth;
    private int mHeight;
    private boolean mPortrait;
    private View loadingLayout;
    private View mainLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View drawerView = setDrawerLayout(R.layout.drawer_camera_layout);
        BaseRecyclerView subjectRecyclerView = (BaseRecyclerView) drawerView.findViewById(R.id.subject_recycler_view);
        subjectRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.color.grey_light, 1));
        subjectRecyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerId = UUID.fromString(getArguments().getString("97a0e5da-48c8-44de-8aed-7fb2be8678aa"));
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.unConfirmAllCalls(customerId);
            final View view = inflater.inflate(R.layout.fragment_camera_layout, container, false);
            loadingLayout = view.findViewById(R.id.loading_layout);
            mainLayout = view.findViewById(R.id.main_layout);
            cameraView = (CameraView) view.findViewById(R.id.camera_view);
            cameraView.addCameraListener(new CameraListener() {
                @Override
                public void onCameraOpened(CameraOptions options) {
                    super.onCameraOpened(options);
                    dismissLoading();
                }

                @Override
                public void onOrientationChanged(int orientation) {
                    mPortrait = orientation == 90;
                }

                @Override
                public void onPictureTaken(byte[] picture) {
                    CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
                        @Override
                        public void onBitmapReady(Bitmap bitmap) {
                            dismissLoading();
                            Size captureSize = cameraView.getCaptureSize();
                            if (captureSize != null) {
                                mWidth = captureSize.getWidth();
                                mHeight = captureSize.getHeight();
                                double ratio = (double) mWidth / (double) mHeight;
                                imagePreviewLayout.setVisibility(View.VISIBLE);
                                mBitmap = bitmap;
                                Point point = HelperMethods.getDisplayMetrics(getContext());
                                int w = point.x;
                                int h = (int) Math.floor(w / ratio);
                                Bitmap bitmap2;
                                if (mPortrait)
                                    bitmap2 = Bitmap.createScaledBitmap(bitmap, h, w, true);
                                else
                                    bitmap2 = Bitmap.createScaledBitmap(bitmap, w, h, true);
                                previewImageView.setImageBitmap(bitmap2);
                            }
                        }
                    });
                }
            });
            previewImageView = (ImageView) view.findViewById(R.id.preview_image_view);
            subjectTextView = (TextView) view.findViewById(R.id.subject_text_view);
            imagePreviewLayout = view.findViewById(R.id.image_preview_layout);
            takePicture = view.findViewById(R.id.take_picture);
            view.findViewById(R.id.choose_subject_fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVaranegarActvity().toggleDrawer();
                }
            });
            view.findViewById(R.id.close_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagePreviewLayout.setVisibility(View.GONE);
                }
            });
            view.findViewById(R.id.done_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.findViewById(R.id.take_picture).setVisibility(View.VISIBLE);
                    save();
                }
            });

            view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trySave();
                }
            });
            takePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedSubject == null) {
                        getVaranegarActvity().showSnackBar(R.string.please_first_select_a_subject, MainVaranegarActivity.Duration.Short);
                        return;
                    }
                    showLoading();
                    cameraView.capturePicture();
                }
            });
            adapter = new BaseSelectionRecyclerAdapter<PictureCustomerViewModel>(
                    getVaranegarActvity(),
                    new PictureCustomerViewModelRepository(),
                    PictureCustomerViewManager.getPicturesQuery(customerId, isLackOfOrder()),
                    false) {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject_picture, parent, false);
                    return new PictureSubjectViewHolder(itemView, this, getContext());
                }
            };
            adapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position, boolean selected) {
                    selectedSubject = adapter.get(position);
                    subjectTextView.setText(selectedSubject.Title);
                }
            });
            adapter.refreshAsync();
            return view;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private boolean isLackOfOrder() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            return callManager.isLackOfOrder(callManager.loadCalls(customerId));
        else
            return false;
    }


    private void dismissLoading() {
        loadingLayout.setVisibility(View.GONE);
        mainLayout.setClickable(true);
    }

    private void showLoading() {
        mainLayout.setClickable(false);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void save() {
        imagePreviewLayout.setVisibility(View.GONE);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.saving_picture));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PictureFileManager pictureFileManager = new PictureFileManager(getContext());
                try {
                    pictureFileManager.save(selectedSubject, mBitmap, mWidth, mHeight, mPortrait);
                    getVaranegarActvity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            adapter.refreshAsync();
                        }
                    });
                } catch (IOException e) {
                    Timber.e(e);
                    getVaranegarActvity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(e.getMessage());
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.setIcon(Icon.Error);
                            dialog.show();
                        }
                    });
                } catch (Exception e) {
                    Timber.e(e);
                    getVaranegarActvity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(R.string.error_saving_request);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.setIcon(Icon.Error);
                            dialog.show();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
        getVaranegarActvity().openDrawer(500);
        showLoading();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    @Override
    public void onBackPressed() {
        if (getVaranegarActvity().isDrawerOpen())
            getVaranegarActvity().closeDrawer();
        else
            trySave();
    }

    private void trySave() {
        if (imagePreviewLayout.getVisibility() == View.VISIBLE) {
            imagePreviewLayout.setVisibility(View.GONE);
            takePicture.setVisibility(View.VISIBLE);
        } else {
            // we get all pictures of this customer
            PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(getContext());
            List<PictureCustomerViewModel> pictureCustomerViewModelList = pictureCustomerViewManager.getPictures(customerId, isLackOfOrder());
            // we find all picture subjects that are mandatory and user has not taken any pictures for them
            // if there is any we find if user has confirmed that he or she was not able to take picture
            final List<PictureCustomerViewModel> mandatoryList = Linq.findAll(pictureCustomerViewModelList, new Linq.Criteria<PictureCustomerViewModel>() {
                @Override
                public boolean run(PictureCustomerViewModel item) {
                    return item.FileCount == 0 && item.DemandType == PictureDemandType.Mandatory && item.NoPictureReason == null;
                }
            });
            if (mandatoryList.size() == 0) {
                // if there is no mandatory picture save call and exit
                saveCameraCallAndExit();
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(getString(R.string.taking_picture_of) + " " + mandatoryList.get(0).Title + " " + getString(R.string.is_mandatory));
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }


        }
    }

    public boolean hasTakenPhoto() {
        PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(getContext());
        List<PictureCustomerViewModel> pictureCustomerViewModelList = pictureCustomerViewManager.getPictures(customerId, isLackOfOrder());
        return Linq.exists(pictureCustomerViewModelList, new Linq.Criteria<PictureCustomerViewModel>() {
            @Override
            public boolean run(PictureCustomerViewModel item) {
                return item.FileCount > 0;
            }
        });
    }

    private void saveCameraCallAndExit() {
        // if any picture has been taken save camera call
        if (hasTakenPhoto()) {
            try {
                new CustomerCallManager(getActivity()).saveCameraCall(customerId);
                getVaranegarActvity().popFragment();
            } catch (Exception ex) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setMessage(R.string.error_saving_request);
                dialog.setTitle(R.string.error);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        } else
            getVaranegarActvity().popFragment();
    }

    public void setCustomerId(UUID selectedId) {
        addArgument("97a0e5da-48c8-44de-8aed-7fb2be8678aa", selectedId.toString());
    }

    private class PictureSubjectViewHolder extends BaseViewHolder<PictureCustomerViewModel> {
        private final TextView subjectTextView;
        private final ImageView pinImageView;
        private final View shadowView;
        private final ImageView imageGalleryImageView;

        public PictureSubjectViewHolder(View itemView, BaseRecyclerAdapter<PictureCustomerViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            subjectTextView = (TextView) itemView.findViewById(R.id.subject_text_view);
            pinImageView = (ImageView) itemView.findViewById(R.id.pin_image_view);
            shadowView = itemView.findViewById(R.id.white_shadow_view);
            imageGalleryImageView = (ImageView) itemView.findViewById(R.id.image_gallery_image_view);

        }

        @Override
        public void bindView(final int position) {
            final BaseSelectionRecyclerAdapter adapter = ((BaseSelectionRecyclerAdapter) recyclerAdapter);
            int p = adapter.getSelectedPosition();
            if (p == position) {
                shadowView.setVisibility(View.GONE);
            } else {
                shadowView.setVisibility(View.VISIBLE);
            }
            final PictureCustomerViewModel pictureCustomerViewModel = recyclerAdapter.get(position);
            subjectTextView.setText(pictureCustomerViewModel.Title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.notifyItemClicked(position);
                    getVaranegarActvity().toggleDrawer();
                }
            });
            if (pictureCustomerViewModel.FileCount == 0) {
                imageGalleryImageView.setImageResource(R.drawable.ic_no_image_grey_24dp);
                imageGalleryImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoPictureReasonDialog dialog = new NoPictureReasonDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("cc550edb-f5e1-45d8-8010-1eef6f9a9bfc", pictureCustomerViewModel.UniqueId.toString());
                        dialog.setArguments(bundle);
                        dialog.onReasonSelected = new NoPictureReasonDialog.OnReasonSelected() {
                            @Override
                            public void onDone() {
                                adapter.refreshAsync();
                            }
                        };
                        dialog.show(getChildFragmentManager(), "7b0b529b-3f9b-4399-a2c7-2a85bcf57c1c");
                    }
                });
            } else {
                if (pictureCustomerViewModel.FileCount == 1)
                    imageGalleryImageView.setImageResource(R.drawable.ic_image_grey_24dp);
                else
                    imageGalleryImageView.setImageResource(R.drawable.ic_multiple_image_grey_24dp);
                imageGalleryImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageGalleryFragment fragment = new ImageGalleryFragment();
                        fragment.setCustomerPictureSubjectId(pictureCustomerViewModel.UniqueId);
                        fragment.onDataSetChanged = new ImageGalleryFragment.OnDataSetChanged() {
                            @Override
                            public void onChanged() {
                                adapter.refreshAsync();
                                if (!hasTakenPhoto()) {
                                    try {
                                        new CustomerCallManager(getContext()).removeCameraCall(customerId);
                                    } catch (DbException e) {
                                        Timber.e(e);
                                    }
                                }
                            }
                        };
                        fragment.show(getChildFragmentManager(), "ImageGalleryFragment");
                    }
                });
            }

            if (pictureCustomerViewModel.DemandType == PictureDemandType.Mandatory)
                pinImageView.setVisibility(View.VISIBLE);
            else
                pinImageView.setVisibility(View.INVISIBLE);
        }
    }


}
