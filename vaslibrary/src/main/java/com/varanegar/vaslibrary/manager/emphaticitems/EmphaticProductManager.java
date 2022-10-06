package com.varanegar.vaslibrary.manager.emphaticitems;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ConstraintViolation;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.Validator;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.emphaticproduct.EmphaticProduct;
import com.varanegar.vaslibrary.model.emphaticproduct.EmphaticProductModel;
import com.varanegar.vaslibrary.model.emphaticproduct.EmphaticProductModelRepository;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.product.EmphaticProductApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/10/2017.
 */

public class EmphaticProductManager extends BaseManager<EmphaticProductModel> {

    public static final UUID PRODUCT = UUID.fromString("84BBB386-3BBB-4644-9E97-79F601627A5F");
    public static final UUID PACKAGE = UUID.fromString("7264025D-4001-48ED-A709-C290AB8F1E9C");

    public EmphaticProductManager(@NonNull Context context) {
        super(context, new EmphaticProductModelRepository());
    }

    public void sync(final UpdateCall updateCall) {
        try {
            deleteAll();
            save(updateCall);
        } catch (DbException e) {
            updateCall.failure(getContext().getString(R.string.failed_to_delete_old_emphatic_items));
        }
    }

    private void save(final UpdateCall updateCall) {
        EmphaticProductApi emphaticProductApi = new EmphaticProductApi(getContext());
        emphaticProductApi.runWebRequest(emphaticProductApi.getAll(), new WebCallBack<List<EmphaticProductModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            public void onSuccess(final List<EmphaticProductModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insert(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.EmphaticProducts);
                        Timber.i("Updating master table of emphatic products completed");
                        List<EmphaticProductCountModel> counts = Linq.mapMerge(result, new Linq.Map<EmphaticProductModel, List<EmphaticProductCountModel>>() {
                            @Override
                            public List<EmphaticProductCountModel> run(EmphaticProductModel item) {
                                for (EmphaticProductCountModel line :
                                        item.emphasisProductDetails) {
                                    line.RuleId = item.UniqueId;
                                }
                                return item.emphasisProductDetails;
                            }
                        }, new Linq.Merge<List<EmphaticProductCountModel>>() {
                            @Override
                            public List<EmphaticProductCountModel> run(List<EmphaticProductCountModel> item1, List<EmphaticProductCountModel> item2) {
                                item1.addAll(item2);
                                return item1;
                            }
                        });
                        EmphaticProductCountManager cmanager = new EmphaticProductCountManager(getContext());
                        cmanager.deleteAll();
                        cmanager.insert(counts);
                        Timber.i("EmphaticProductCount updated successfully.");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Emphatic list is empty");
                    updateCall.success();
                }

            }

            @Override
            public void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            public void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }


    public List<EmphaticProductModel> getAll() {
        return getItems(new Query().from(EmphaticProduct.EmphaticProductTbl));
    }

    public String getTypeName(EmphasisType type) {
        if (type == EmphasisType.Suggestion)
            return getContext().getString(R.string.emphatic_suggection);
        else if (type == EmphasisType.Deterrent)
            return getContext().getString(R.string.emphatic_detterent);
        else
            return getContext().getString(R.string.emphatic_warning);
    }



    public EmphaticProductModel getItemByRoleId(UUID roleId) {
        return getItem(new Query().from(EmphaticProduct.EmphaticProductTbl).whereAnd(Criteria.equals(EmphaticProduct.UniqueId, roleId)));
    }
    public EmphaticProductModel getCustomerCategory(UUID roleId) {
        return getItem(new Query().from(EmphaticProduct.EmphaticProductTbl).whereAnd(Criteria.equals(EmphaticProduct.CustomerCategoryId, roleId)));
    }

    public EmphaticProductModel getCustomerLevel(UUID roleId) {
        return getItem(new Query().from(EmphaticProduct.EmphaticProductTbl).whereAnd(Criteria.equals(EmphaticProduct.CustomerLevelId, roleId)));
    }

    public EmphaticProductModel getCustomerActivity(UUID roleId) {
        return getItem(new Query().from(EmphaticProduct.EmphaticProductTbl).whereAnd(Criteria.equals(EmphaticProduct.CustomerActivityId, roleId)));
    }

}
