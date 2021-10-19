package com.varanegar.vaslibrary.manager.productrequest;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLine;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineModelRepository;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQtyModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/25/2018.
 */

public class RequestLineManager extends BaseManager<RequestLineModel> {
    public RequestLineManager(@NonNull Context context) {
        super(context, new RequestLineModelRepository());
    }

    public List<RequestLineModel> getRequestLines() {
        Query query = new Query();
        query.from(RequestLine.RequestLineTbl);
        return getItems(query);
    }

    public RequestLineModel getRequestLine(@NonNull UUID productId) {
        Query query = new Query();
        query.from(RequestLine.RequestLineTbl).whereAnd(Criteria.equals(RequestLine.ProductId, productId.toString()));
        return getItem(query);
    }

    public void addOrUpdateQty(@NonNull List<DiscreteUnit> productQtyLines, @Nullable BaseUnit bulkQty) throws ValidationException, DbException {
        if (productQtyLines.size() == 0 && bulkQty == null)
            throw new IllegalArgumentException("empty qty is not supported");
        final ProductUnitViewModel productUnitViewModel;
        if (productQtyLines.size() > 0)
            productUnitViewModel = new ProductUnitViewManager(getContext()).getItem(productQtyLines.get(0).ProductUnitId);
        else
            productUnitViewModel = new ProductUnitViewManager(getContext()).getItem(bulkQty.ProductUnitId);
        final List<RequestLineModel> requestLineModels = getRequestLines();
        final RequestLineModel requestLineModel = Linq.findFirst(requestLineModels, new Linq.Criteria<RequestLineModel>() {
            @Override
            public boolean run(RequestLineModel item) {
                return item.ProductId.equals(productUnitViewModel.ProductId);
            }
        });
        if (requestLineModel == null) {
            addRequestLine(productQtyLines, bulkQty, productUnitViewModel.ProductId);
        } else {
            updateRequestLine(requestLineModel, productQtyLines, bulkQty);
        }
    }

    private void addRequestLine(
            final List<DiscreteUnit> productQtyLines,
            final BaseUnit bulkUnit,
            UUID productId) throws ValidationException, DbException {
        final RequestLineModel newCustomerCallOrderLine = new RequestLineModel();
        newCustomerCallOrderLine.UniqueId = UUID.randomUUID();
        newCustomerCallOrderLine.ProductId = productId;
        Query query = new Query().from(RequestLine.RequestLineTbl).select(Projection.max(RequestLine.RowIndex))
                .orderByDescending(RequestLine.RowIndex);
        Integer last = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
        if (last == null) last = 0;
        last++;
        newCustomerCallOrderLine.RowIndex = last;
        if (bulkUnit != null) {
            newCustomerCallOrderLine.BulkQty = new BigDecimal(bulkUnit.value);
            newCustomerCallOrderLine.BulkQtyUnitUniqueId = bulkUnit.ProductUnitId;
        }
        insert(newCustomerCallOrderLine);
        if (productQtyLines.size() > 0) {
            final int[] no = {0};
            for (DiscreteUnit qtyLine :
                    productQtyLines) {
                insertOrUpdateOrderLineQty(qtyLine, newCustomerCallOrderLine.UniqueId);
            }
        } else if (productQtyLines.size() == 0 && bulkUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = bulkUnit.value;
            u.Name = bulkUnit.Name;
            u.ProductUnitId = bulkUnit.ProductUnitId;
            insertOrUpdateOrderLineQty(u, newCustomerCallOrderLine.UniqueId);
        }

    }

    private void updateRequestLine(final RequestLineModel requestLineModel, final List<DiscreteUnit> qtyLines, final BaseUnit bulkUnit) throws ValidationException, DbException {

        if (bulkUnit != null) {
            requestLineModel.BulkQty = new BigDecimal(bulkUnit.value);
            requestLineModel.BulkQtyUnitUniqueId = bulkUnit.ProductUnitId;
        }
        update(requestLineModel);
        if (qtyLines.size() > 0) {
            for (DiscreteUnit qtyLine :
                    qtyLines) {
                insertOrUpdateOrderLineQty(qtyLine, requestLineModel.UniqueId);
            }
        } else if (qtyLines.size() == 0 && bulkUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = bulkUnit.value;
            u.Name = bulkUnit.Name;
            u.ProductUnitId = bulkUnit.ProductUnitId;
            insertOrUpdateOrderLineQty(u, requestLineModel.UniqueId);
        }
    }

    private void insertOrUpdateOrderLineQty(DiscreteUnit qtyLine, UUID requestLineUniqueId) throws ValidationException, DbException {
        RequestLineQtyManager qtyManager = new RequestLineQtyManager(getContext());
        RequestLineQtyModel qty = qtyManager.getItem(RequestLineQtyManager.getRequestQtyDetail(qtyLine.ProductUnitId));
        if (qty != null) {
            qty.Qty = qtyLine.getQty();
            qtyManager.update(qty);
        } else {
            qty = new RequestLineQtyModel();
            qty.UniqueId = UUID.randomUUID();
            qty.Qty = qtyLine.getQty();
            qty.RequestLineUniqueId = requestLineUniqueId;
            qty.ProductUnitId = qtyLine.ProductUnitId;
            qtyManager.insert(qty);
        }

    }

    public void deleteProduct(@NonNull UUID productId) throws DbException {
        delete(Criteria.equals(RequestLine.ProductId, productId.toString()));
    }


    public long deleteAllLines() throws DbException {
        return deleteAll();
    }


}
