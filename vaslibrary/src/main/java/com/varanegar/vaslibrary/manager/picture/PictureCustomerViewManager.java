package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiException;
import com.varanegar.framework.network.listeners.InterruptedException;
import com.varanegar.framework.network.listeners.NetworkException;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerView;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModelRepository;
import com.varanegar.vaslibrary.model.picturesubject.PictureDemandType;
import com.varanegar.vaslibrary.model.picturesubject.PictureFileModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.imageapi.ImageApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/18/2017.
 */

public class PictureCustomerViewManager extends BaseManager<PictureCustomerViewModel> {
    public PictureCustomerViewManager(@NonNull Context context) {
        super(context, new PictureCustomerViewModelRepository());
    }


    /**
     * check db
     * عدم ویزیت عدم سفارش
     * Need Image
     *  کوئری  گرفتن
     *  لیست عدم ویزیت
     * لیست عدم سفارش
     * @param customerId
     * @param isLackOfOrder
     * @return
     */
    public static Query getPicturesQuery(UUID customerId, boolean isLackOfOrder, boolean isLackOfVisit) {
        if (isLackOfVisit || isLackOfOrder)
            return new Query().from(PictureCustomerView.PictureCustomerViewTbl)
                    .whereAnd(Criteria.equals(PictureCustomerView.CustomerId, customerId.toString()))
                    .whereAnd(Criteria.equals(PictureCustomerView.DemandTypeUniqueId,
                            PictureDemandTypeId.Mandatory.toString()));


        /*else if (isLackOfOrder)
            return new Query().from(PictureCustomerView.PictureCustomerViewTbl)
                    .whereAnd(Criteria.equals(PictureCustomerView.CustomerId, customerId.toString()))
                    .whereAnd(Criteria.equals(PictureCustomerView.DemandTypeUniqueId, PictureDemandTypeId.JustOnce.toString())
                            .and(Criteria.equals(PictureCustomerView.AlreadyTaken, false))
                            .or(Criteria.notEquals(PictureCustomerView.DemandTypeUniqueId, PictureDemandTypeId.JustOnce.toString())));*/
        else
            return new Query().from(PictureCustomerView.PictureCustomerViewTbl)
                    .whereAnd(Criteria.equals(PictureCustomerView.CustomerId, customerId.toString()))
                    .whereAnd(Criteria.equals(PictureCustomerView.DemandTypeUniqueId, PictureDemandTypeId.JustOnce.toString())
                            .and(Criteria.equals(PictureCustomerView.AlreadyTaken, false))
                            .or(Criteria.notEquals(PictureCustomerView.DemandTypeUniqueId, PictureDemandTypeId.JustOnce.toString())))
                    .whereAnd(Criteria.notEquals(PictureCustomerView.DemandTypeUniqueId,
                            PictureDemandTypeId.NoSaleMandatory.toString()));
    }


    public List<PictureCustomerViewModel> getPictures(UUID customerId, boolean isLackOfOrder, boolean isLackOfVisit) {
        return getItems(getPicturesQuery(customerId, isLackOfOrder, isLackOfVisit));
    }


    public String checkMandatoryPicture(final UUID customerId, @Nullable List<CustomerCallModel> customerCalls) {
        if (customerCalls == null)
            customerCalls = new ArrayList<>();
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        boolean lakOfVisit = callManager.isLackOfVisit(customerCalls);
        boolean isLackOfOrderAndNeedImage = false;

        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            isLackOfOrderAndNeedImage = callManager.isLackOfOrderAndNeedImage(customerCalls);
           // isLackOfVisitAndNeedImage = callManager.isLackOfVisitAndNeedImage(customerCalls);
        }
        if (lakOfVisit && !isLackOfOrderAndNeedImage)
            return null;
        PictureSubjectZarManager pictureTemplateManager = new PictureSubjectZarManager(getContext());
        try {

            if (isLackOfOrderAndNeedImage)
                pictureTemplateManager.calculateCustomerPictures2(customerId, customerCalls);
            else
                 pictureTemplateManager.calculateCustomerPictures(customerId, customerCalls);
            PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(getContext());
            List<PictureCustomerViewModel> subjects = null;

               subjects = pictureCustomerViewManager.getPictures(customerId, isLackOfOrderAndNeedImage, false);

            if (subjects.size() == 0)
                return null;
            PictureCustomerViewModel force = Linq.findFirst(subjects, new Linq.Criteria<PictureCustomerViewModel>() {
                @Override
                public boolean run(PictureCustomerViewModel item) {
                    return item.DemandType == PictureDemandType.Mandatory && item.FileCount == 0 && item.NoPictureReason == null;
                }
            });
            if (force == null)
                return null;
            else
                return getContext().getString(R.string.taking_picture_of) + " " + force.Title + " " + getContext().getString(R.string.is_mandatory);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    private void uploadPictures(List<PictureCustomerViewModel> pictureCustomerViewModels, final UpdateCall call) {
        final List<ImageViewModel> imageViewModels = new ArrayList<>();
        PictureFileManager pictureFileManager = new PictureFileManager(getContext());
        if (pictureCustomerViewModels.size() > 0) {
            for (PictureCustomerViewModel pictureCustomerViewModel :
                    pictureCustomerViewModels) {
                CustomerCallManager callManager = new CustomerCallManager(getContext());
                List<CustomerCallModel> calls = callManager.loadCalls(pictureCustomerViewModel.CustomerId);
                if (!callManager.isDataSent(calls, null) && pictureCustomerViewModel.FileCount > 0) {
                    List<UUID> fileIds = pictureCustomerViewModel.getFileIds();
                    for (UUID fileId :
                            fileIds) {
                        PictureFileModel pictureFileModel = pictureFileManager.getPictureFile(fileId);
                        String fileName = pictureFileManager.getPictureFileName(pictureFileModel);
                        ImageViewModel imageViewModel = new ImageViewModel();
                        imageViewModel.imageId = pictureFileModel.FileId.toString();
                        imageViewModel.token = pictureCustomerViewModel.UniqueId.toString();
                        imageViewModel.isDefault = false;
                        imageViewModel.imageType = ImageType.CustomerCallPicture;
                        File file = new File(fileName);
                        if (file.exists()) {
                            imageViewModel.file = file;
                            if (file.exists())
                                imageViewModels.add(imageViewModel);
                        }
                    }
                }
            }
            if (imageViewModels.size() > 0) {
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int sentItems = 0;
                        ImageApi imageApi = new ImageApi(getContext());
                        for (ImageViewModel imageViewModel :
                                imageViewModels) {
                            try {
                                imageApi.runWebRequest(imageApi.postImage(imageViewModel));
                                Timber.d("image " + imageViewModel.imageId + " was sent");
                                imageViewModel.file.delete();
                                sentItems++;
                                if (sentItems == imageViewModels.size()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            call.success();
                                        }
                                    });
                                }
                            } catch (ApiException e) {
                                final String err = WebApiErrorBody.log(e.getApiError(), getContext());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        call.failure(err);
                                    }
                                });
                                return;
                            } catch (NetworkException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        call.failure(getContext().getString(R.string.connection_to_server_failed));
                                    }
                                });
                                return;
                            } catch (InterruptedException e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        call.failure(getContext().getString(R.string.sending_images_canceled));
                                    }
                                });
                                return;
                            }
                        }
                    }
                }).start();
            } else {
                call.success();
            }
        } else {
            call.success();
        }

    }

    public void uploadCustomerPictures(final UpdateCall call) {
        List<PictureCustomerViewModel> pictureCustomerViewModels = getItems(new Query().from(PictureCustomerView.PictureCustomerViewTbl));
        uploadPictures(pictureCustomerViewModels, call);
    }

    public void uploadCustomerPictures(UUID customerId, final UpdateCall call) {
        List<PictureCustomerViewModel> pictureCustomerViewModels = getItems(
                new Query().from(PictureCustomerView.PictureCustomerViewTbl)
                        .whereAnd(Criteria.equals(PictureCustomerView.CustomerId, customerId.toString()))
        );
        uploadPictures(pictureCustomerViewModels, call);
    }

    public void uploadCustomerPictures(List<UUID> customerIds, final UpdateCall call) {
        List<PictureCustomerViewModel> pictureCustomerViewModels = getItems(
                new Query().from(PictureCustomerView.PictureCustomerViewTbl)
                        .whereAnd(Criteria.in(PictureCustomerView.CustomerId, customerIds))
        );
        uploadPictures(pictureCustomerViewModels, call);
    }

    public void uploadNationalCard(UUID customerId, File file, UpdateCall call){
        ImageViewModel imageViewModel = new ImageViewModel();
        imageViewModel.file = file;
        imageViewModel.imageId = customerId.toString();
        imageViewModel.token = customerId.toString();
        imageViewModel.isDefault = false;
        imageViewModel.imageType = ImageType.NationalCard;
        ImageApi imageApi = new ImageApi(getContext());
        final Handler handler = new Handler();

        new Thread(() -> {

            try {
                imageApi.runWebRequest(imageApi.postImage(imageViewModel));
                handler.post(call::success);
            } catch (ApiException e) {
                final String err = WebApiErrorBody.log(e.getApiError(), getContext());
                handler.post(() -> call.failure(err));
            } catch (NetworkException e) {
                handler.post(() -> call.failure(getContext()
                        .getString(R.string.connection_to_server_failed)));
            } catch (InterruptedException e) {
                handler.post(() -> call.failure(getContext()
                        .getString(R.string.sending_images_canceled)));
            }
        }).start();
    }
}
