package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureDemandType;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetailModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeaderModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.picturesubject.PictureSubjectApi;
import com.varanegar.vaslibrary.webapi.picturesubject.PictureTemplateModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 10/16/2017.
 */

public class PictureTemplateManager {
    Context context;

    public PictureTemplateManager(@NonNull Context context) {
        this.context = context;
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(context);
        //Date date = updateManager.getLog(UpdateKey.PictureTemplate);
        Date date = UpdateManager.MIN_DATE;
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        PictureSubjectApi api = new PictureSubjectApi(context);
        api.runWebRequest(api.getPictureTemplates(dateString), new WebCallBack<List<PictureTemplateModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<PictureTemplateModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    List<PictureTemplateHeaderModel> pictureTemplateHeaderModels = new ArrayList<>();
                    final List<PictureTemplateDetailModel> pictureTemplateDetailModels = new ArrayList<>();
                    for (final PictureTemplateModel pictureTemplateModel : result) {
                        pictureTemplateHeaderModels.add(pictureTemplateModel.convert());
                        pictureTemplateDetailModels.addAll(Linq.map(pictureTemplateModel.PictureTemplateDetails, new Linq.Map<PictureTemplateDetailModel, PictureTemplateDetailModel>() {
                            @Override
                            public PictureTemplateDetailModel run(PictureTemplateDetailModel item) {
                                if (item.DemandTypeUniqueId.equals(PictureDemandTypeId.Mandatory) || item.DemandTypeUniqueId.equals(PictureDemandTypeId.JustOnce))
                                    item.DemandType = PictureDemandType.Mandatory;
                                else if (item.DemandTypeUniqueId.equals(PictureDemandTypeId.NoSaleMandatory))
                                    item.DemandType = PictureDemandType.NoSaleMandatory;
                                else
                                    item.DemandType = PictureDemandType.Optional;
                                return item;
                            }
                        }));
                    }

                    try {
                        new PictureTemplateHeaderManager(context).sync(pictureTemplateHeaderModels);
                        Timber.i("List of picture template headers updated");
                        new PictureTemplateDetailManager(context).sync(pictureTemplateDetailModels);
                        new UpdateManager(context).addLog(UpdateKey.PictureTemplate);
                        Timber.i("List of picture template details updated");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(context.getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(context.getString(R.string.data_error));
                    }
                } else {
                    Timber.i("List of picture templates was empty");
                    call.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(context.getString(R.string.network_error));
            }
        });
    }

    public void calculateCustomerPictures(final UUID customerId, @Nullable List<CustomerCallModel> customerCalls) throws ValidationException, DbException {
        // step 1: extract valid templates for the customer
        PictureTemplateHeaderManager headerManager = new PictureTemplateHeaderManager(context);
        List<PictureTemplateHeaderModel> headers = headerManager.getValidTemplatesForCustomer(customerId);
        if (headers.size() == 0) {
            Timber.i("No picture template has been calculated for customer id = " + customerId.toString());
            return;
        }
        // step 2: find corresponding subjects according to the picture templates that we found in the step 1
        PictureTemplateDetailManager detailManager = new PictureTemplateDetailManager(context);
        final List<PictureTemplateDetailModel> details = detailManager.getCorrespondingDetails(headers);
        if (details.size() == 0) {
            Timber.i(headers.size() + " picture templates has been calculated for customer id = " + customerId.toString() + " but no picture subject found for them");
            return;
        }
        // step 3: Now save picture subjects for the customer. In camera fragment we use the corresponding view manager to find these items
        PictureCustomerManager pictureCustomerManager = new PictureCustomerManager(context);
        pictureCustomerManager.savePictureTemplates(customerId, details, customerCalls);
    }
}
