package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ConstraintViolation;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.Validator;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.target.TargetDetailModel;
import com.varanegar.vaslibrary.model.target.TargetMasterModel;
import com.varanegar.vaslibrary.model.target.TargetProductGroupModel;
import com.varanegar.vaslibrary.model.target.TargetProductModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.target.TargetApi;
import com.varanegar.vaslibrary.webapi.target.TargetDetailViewModel;
import com.varanegar.vaslibrary.webapi.target.TargetMasterViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

import static com.varanegar.framework.util.datetime.DateFormat.MicrosoftDateTime;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public class TargetManager {
    private Context context;

    public TargetManager(@NonNull Context context) {
        this.context = context;
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(context);
        final TargetApi targetApi = new TargetApi(context);
        Date date = updateManager.getLog(UpdateKey.Target);
        String dateAfter = DateHelper.toString(date, MicrosoftDateTime, Locale.US);
        targetApi.runWebRequest(targetApi.getTarget(dateAfter, UserManager.readFromFile(context).UniqueId.toString()), new WebCallBack<List<TargetMasterViewModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<TargetMasterViewModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    List<TargetMasterModel> targetMasterModels = new ArrayList<>();
                    List<TargetDetailViewModel> targetDetailViewModelList = new ArrayList<>();
                    final List<TargetDetailModel> targetDetailModels = new ArrayList<>();
                    final List<TargetProductGroupModel> targetProductGroupModels = new ArrayList<>();
                    final List<TargetProductModel> targetProductModels = new ArrayList<>();
                    for (TargetMasterViewModel targetMasterViewModel : result) {
                        targetMasterModels.add(targetMasterViewModel.convert());
                        if (targetMasterViewModel.TargetDetails != null) {
                            targetDetailViewModelList.addAll(targetMasterViewModel.TargetDetails);
                            for (TargetDetailViewModel targetDetailViewModel : targetDetailViewModelList) {
                                targetDetailModels.add(targetDetailViewModel.convert());
                                if (targetDetailViewModel.TargetProducts != null)
                                    targetProductModels.addAll(targetDetailViewModel.TargetProducts);
                                if (targetDetailViewModel.TargetProductGroups != null)
                                    targetProductGroupModels.addAll(targetDetailViewModel.TargetProductGroups);
                            }
                        }
                    }
                    if (targetMasterModels.size() > 0) {
                        TargetMasterManager targetMasterManager = new TargetMasterManager(context);
                        try {
                            targetMasterManager.sync(targetMasterModels);
                            Timber.i("List of target masters updated");
                            if (targetDetailModels.size() > 0) {
                                new TargetDetailManager(context).sync(targetDetailModels);
                                if (targetProductGroupModels.size() > 0) {
                                    new TargetProductGroupManager(context).sync(targetProductGroupModels);
                                    insetTargetProduct(targetProductModels);
                                } else {
                                    if (targetProductModels.size() > 0) {
                                        insetTargetProduct(targetProductModels);
                                    } else {
                                        new UpdateManager(context).addLog(UpdateKey.Target);
                                        Timber.i("List of TargetProduct was empty");
                                    }
                                }
                            } else {
                                new UpdateManager(context).addLog(UpdateKey.Target);
                                Timber.i("List of TargetDetail was empty");
                            }
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(context.getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(context.getString(R.string.data_error));
                        }
                    } else {
                        call.success();
                        new UpdateManager(context).addLog(UpdateKey.Target);
                        Timber.i("List of TargetMaster was empty");
                    }
                } else {
                    call.success();
                    new UpdateManager(context).addLog(UpdateKey.Target);
                    Timber.i("List of Target was empty");
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

    private void insetTargetProduct(List<TargetProductModel> targetProductModels) throws ValidationException, DbException {
        if (targetProductModels.size() > 0) {
            new TargetProductManager(context).sync(targetProductModels);
            new UpdateManager(context).addLog(UpdateKey.Target);
            Timber.i("List of target product updated");
        }
    }
}
