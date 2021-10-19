package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQty;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModel;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModelRepository;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 7/10/2018.
 */

public class ProductBatchOnHandQtyManager extends BaseManager<ProductBatchOnHandQtyModel> {
    public ProductBatchOnHandQtyManager(@NonNull Context context) {
        super(context, new ProductBatchOnHandQtyModelRepository());
    }

    public void sync(List<ProductBatchOnHandQtyModel> result, @NonNull final UpdateCall updateCall) {
        if (result != null && result.size() > 0) {
            try {
                insert(result);
                new UpdateManager(getContext()).addLog(UpdateKey.ProductBatchOnHandQty);
                Timber.i("Updating productonhandqty completed");
                updateCall.success();
            } catch (ValidationException e) {
                Timber.e(e);
                updateCall.failure(getContext().getString(R.string.data_validation_failed));
            } catch (DbException e) {
                Timber.e(e);
                updateCall.failure(getContext().getString(R.string.data_error));
            }
        } else {
            Timber.i("Updating ProductBatchOnHandQty completed. List was empty");
            updateCall.success();
        }
    }

    public static Query getProductBatchesQuery(UUID productId) {
        Query query = new Query();
        return query.from(ProductBatchOnHandQty.ProductBatchOnHandQtyTbl).whereAnd(Criteria.equals(ProductBatchOnHandQty.ProductId, productId));
    }

    public List<ProductBatchOnHandQtyModel> getProductBatches(UUID productId) {
        Query query = getProductBatchesQuery(productId);
        return getItems(query);
    }

}
