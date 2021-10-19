package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.totalproductsaleview.CustomerTotalProductSaleModel;
import com.varanegar.vaslibrary.model.totalproductsaleview.CustomerTotalProductSaleModelRepository;
import com.varanegar.vaslibrary.model.totalproductsaleview.TotalProductSaleViewModel;
import com.varanegar.vaslibrary.model.totalproductsaleview.TotalProductSaleViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/17/2017.
 */

public class CustomerTotalProductSaleManager extends BaseManager<CustomerTotalProductSaleModel> {
    public CustomerTotalProductSaleManager(@NonNull Context context) {
        super(context, new CustomerTotalProductSaleModelRepository());
    }

    public void calculate(UUID customerId) throws ValidationException, DbException {
        TotalProductSaleViewModelRepository repository = new TotalProductSaleViewModelRepository();
        List<TotalProductSaleViewModel> items =
                repository.getItems(
                        TotalProductSaleViewManager.baseQuery(customerId));
        final List<CustomerTotalProductSaleModel> models = Linq.map(items, new Linq.Map<TotalProductSaleViewModel, CustomerTotalProductSaleModel>() {
            @Override
            public CustomerTotalProductSaleModel run(TotalProductSaleViewModel item) {
                CustomerTotalProductSaleModel model = new CustomerTotalProductSaleModel();
                model.UniqueId = UUID.randomUUID();
                model.ProductId = item.ProductId;
                model.TotalQty = item.TotalQty;
                model.CustomerId = item.CustomerId;
                model.InvoiceCount = item.InvoiceCount;
                return model;
            }
        });
        deleteAll();
        if (models.size() > 0)
            insert(models);
    }
}
