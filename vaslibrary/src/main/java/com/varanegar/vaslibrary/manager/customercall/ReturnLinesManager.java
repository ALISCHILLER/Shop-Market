package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.TableMap;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesRequestViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.PromotionException;
import com.varanegar.vaslibrary.manager.ReturnReasonManager;
import com.varanegar.vaslibrary.model.call.CustomerCallReturn;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQty;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModel;
import com.varanegar.vaslibrary.model.call.ReturnLines;
import com.varanegar.vaslibrary.model.call.ReturnLinesModel;
import com.varanegar.vaslibrary.model.call.ReturnLinesModelRepository;
import com.varanegar.vaslibrary.model.call.tempreturn.CustomerCallReturnTemp;
import com.varanegar.vaslibrary.model.call.tempreturn.CustomerCallReturnTempModelRepository;
import com.varanegar.vaslibrary.model.call.tempreturn.ReturnLineQtyTemp;
import com.varanegar.vaslibrary.model.call.tempreturn.ReturnLinesTemp;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesRequestViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorItem;
import com.varanegar.vaslibrary.ui.fragment.ReturnProductTypeDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

/**
 * Created by atp on 4/7/2017.
 */

public class ReturnLinesManager extends BaseManager<ReturnLinesModel> {

    public ReturnLinesManager(Context context) {
        super(context, new ReturnLinesModelRepository());
    }


    public long addOrUpdateReturnLines(@NonNull final List<ReturnCalculatorItem> returnCalculatorItems, @NonNull final UUID returnUniqueId, @Nullable final UUID stockId, @Nullable final Currency price , @Nullable String referenceNo, @Nullable UUID editReasonId) throws DbException, ValidationException {
        if (returnCalculatorItems.size() == 0)
            throw new IllegalArgumentException();
        final ProductUnitViewModel productUnitViewModel;
        if (returnCalculatorItems.get(0).getDiscreteUnits().size() > 0)
            productUnitViewModel = new ProductUnitViewManager(getContext()).getItem(returnCalculatorItems.get(0).getDiscreteUnits().get(0).ProductUnitId);
        else
            productUnitViewModel = new ProductUnitViewManager(getContext()).getItem(returnCalculatorItems.get(0).getBulkUnit().ProductUnitId);
        List<ReturnLinesModel> returnLinesModels = getItems(new Query().from(ReturnLines.ReturnLinesTbl).whereAnd(Criteria.equals(ReturnLines.ProductUniqueId, productUnitViewModel.ProductId)
                .and(Criteria.equals(ReturnLines.ReturnUniqueId, returnUniqueId.toString()))));

        long affectedRows = 0;
        for (final ReturnCalculatorItem returnCalculatorItem :
                returnCalculatorItems) {
            if (returnCalculatorItem.getDiscreteUnits().size() == 0 && returnCalculatorItem.getBulkUnit() == null)
                throw new IllegalArgumentException("empty qty lines!");

            ReturnLinesModel returnLinesModel = Linq.findFirst(returnLinesModels, new Linq.Criteria<ReturnLinesModel>() {
                @Override
                public boolean run(ReturnLinesModel item) {
                    return item.ReturnUniqueId.equals(returnUniqueId) &&
                            !item.IsPromoLine &&
                            item.ProductUniqueId.equals(productUnitViewModel.ProductId) &&
                            item.ReturnReasonId.equals(returnCalculatorItem.getReturnReason()) &&
                            item.ReturnProductTypeId.equals(returnCalculatorItem.getReturnType());
                }
            });
            boolean isNew = false;
            if (returnLinesModel == null) {
                isNew = true;
                returnLinesModel = new ReturnLinesModel();
                returnLinesModel.UniqueId = UUID.randomUUID();
                returnLinesModel.ReturnUniqueId = returnUniqueId;
                returnLinesModel.ProductUniqueId = productUnitViewModel.ProductId;
                returnLinesModel.ReturnReasonId = returnCalculatorItem.getReturnReason();
                returnLinesModel.ReturnProductTypeId = returnCalculatorItem.getReturnType();
                returnLinesModel.ReferenceNo = referenceNo;
            }
            returnLinesModel.RequestUnitPrice = price;
            returnLinesModel.StockId = stockId;
            if (returnCalculatorItem.getBulkUnit() != null) {
                returnLinesModel.RequestBulkQty = returnCalculatorItem.getBulkUnit().getQty();
                returnLinesModel.RequestBulkUnitId = returnCalculatorItem.getBulkUnit().ProductUnitId;
            }
            returnLinesModel.EditReasonId = editReasonId;
            if (isNew)
                affectedRows += insertNewReturnLineModel(returnCalculatorItem.getDiscreteUnits(), returnCalculatorItem.getBulkUnit(), returnLinesModel);
            else
                affectedRows += updateReturnLineModel(returnCalculatorItem.getDiscreteUnits(), returnCalculatorItem.getBulkUnit(), returnLinesModel);
        }
        return affectedRows;
    }

    private long insertNewReturnLineModel(@NonNull final List<DiscreteUnit> discreteUnits, @Nullable BaseUnit bulkUnit, @NonNull final ReturnLinesModel returnLinesModel) throws ValidationException, DbException {
        long affectedRows = insert(returnLinesModel);
        if (discreteUnits.size() > 0) {
            for (DiscreteUnit discreteUnit :
                    discreteUnits) {
                affectedRows += insertOrUpdateQty(discreteUnit, returnLinesModel.UniqueId);
            }
        } else if (discreteUnits.size() == 0 && bulkUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = bulkUnit.value;
            u.Name = bulkUnit.Name;
            u.ProductUnitId = bulkUnit.ProductUnitId;
            affectedRows += insertOrUpdateQty(u, returnLinesModel.UniqueId);
        }
        return affectedRows;
    }

    private long updateReturnLineModel(@NonNull final List<DiscreteUnit> discreteUnits, @Nullable BaseUnit bulkUnit, @NonNull final ReturnLinesModel returnLinesModel) throws ValidationException, DbException {
        long affectedRows = update(returnLinesModel);
        if (discreteUnits.size() > 0) {
            for (DiscreteUnit discreteUnit :
                    discreteUnits) {
                affectedRows += insertOrUpdateQty(discreteUnit, returnLinesModel.UniqueId);
            }
        } else if (discreteUnits.size() == 0 && bulkUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = bulkUnit.value;
            u.Name = bulkUnit.Name;
            u.ProductUnitId = bulkUnit.ProductUnitId;
            affectedRows += insertOrUpdateQty(u, returnLinesModel.UniqueId);
        }
        return affectedRows;
    }


    public void removeReturnLineModel(@NonNull final ReturnCalculatorItem returnCalculatorItem) throws DbException {
        final ProductUnitViewModel productUnitViewModel;
        if (returnCalculatorItem.getDiscreteUnits().size() > 0)
            productUnitViewModel = new ProductUnitViewManager(getContext()).getItem(returnCalculatorItem.getDiscreteUnits().get(0).ProductUnitId);
        else
            productUnitViewModel = new ProductUnitViewManager(getContext()).getItem(returnCalculatorItem.getBulkUnit().ProductUnitId);


        List<ReturnLinesModel> returnLinesModels = getItems(new Query().from(ReturnLines.ReturnLinesTbl)
                .whereAnd(Criteria.equals(ReturnLines.ProductUniqueId, productUnitViewModel.ProductId)
                        .and(Criteria.equals(ReturnLines.ReturnUniqueId, returnCalculatorItem.ReturnUniqueId.toString()))));


        ReturnLinesModel returnLinesModel = Linq.findFirst(returnLinesModels, new Linq.Criteria<ReturnLinesModel>() {
            @Override
            public boolean run(ReturnLinesModel item) {
                return item.ReturnUniqueId.equals(returnCalculatorItem.ReturnUniqueId) &&
                        item.ProductUniqueId.equals(productUnitViewModel.ProductId) &&
                        item.ReturnReasonId.equals(returnCalculatorItem.getReturnReason()) &&
                        item.ReturnProductTypeId.equals(returnCalculatorItem.getReturnType());
            }
        });
        if (returnLinesModel != null) {
            if (returnLinesModels.size() > 1)
                delete(returnLinesModel.UniqueId);
            else if (returnLinesModels.size() == 1) {
                CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getContext());
                returnManager.delete(returnCalculatorItem.ReturnUniqueId);
            }
        }
    }

    public void removeReturnLineModel(@NonNull final CustomerCallReturnViewModel returnViewModel) throws DbException {

        final List<ReturnLinesModel> returnLinesModels = getItems(new Query().from(ReturnLines.ReturnLinesTbl)
                .whereAnd(Criteria.equals(ReturnLines.ReturnUniqueId, returnViewModel.ReturnUniqueId.toString())));

        if (returnLinesModels.size() == 0)
            throw new DbException(getContext().getString(R.string.error_saving_request));

        List<ReturnLinesModel> deleteLines = Linq.findAll(returnLinesModels, new Linq.Criteria<ReturnLinesModel>() {
            @Override
            public boolean run(ReturnLinesModel item) {
                return item.ReturnUniqueId.equals(returnViewModel.ReturnUniqueId) &&
                        item.ProductUniqueId.equals(returnViewModel.ProductId);
            }
        });
        if (deleteLines.size() > 0) {
            for (ReturnLinesModel l :
                    deleteLines) {
                delete(l.UniqueId);
            }
            if (deleteLines.size() == returnLinesModels.size()) {
                CustomerCallReturnManager returnManager = new CustomerCallReturnManager(getContext());
                returnManager.delete(returnViewModel.ReturnUniqueId);
            }
        }
    }

    private long insertOrUpdateQty(DiscreteUnit discreteUnit, UUID returnLineId) throws ValidationException, DbException {
        ReturnLineQtyManager qtyManager = new ReturnLineQtyManager(getContext());
        ReturnLineQtyModel qty = qtyManager.getQtyLine(returnLineId, discreteUnit.ProductUnitId);
        if (qty == null) {
            qty = new ReturnLineQtyModel();
            qty.UniqueId = UUID.randomUUID();
            qty.ProductUnitId = discreteUnit.ProductUnitId;
            qty.Qty = discreteUnit.getQty();
            qty.ReturnLineUniqueId = returnLineId;
            return qtyManager.insert(qty);
        } else {
            qty.Qty = discreteUnit.getQty();
            return qtyManager.update(qty);
        }
    }

    public List<ReturnLinesModel> getReturnLines(@NonNull UUID returnCallId) {
        Query query = new Query();
        query.from(ReturnLines.ReturnLinesTbl).whereAnd(Criteria.equals(ReturnLines.ReturnUniqueId, returnCallId.toString()));
        return getItems(query);
    }

    private void insertPromotionLine(@NonNull final DiscountCallReturnLineData discountCallReturnLineData) throws ValidationException, DbException, ProductUnitViewManager.UnitNotFoundException, PromotionException {
        ReturnLinesModel returnLine = new ReturnLinesModel();
        returnLine.UniqueId = UUID.fromString(discountCallReturnLineData.returnLineUniqueId);
        returnLine.ReturnUniqueId = UUID.fromString(discountCallReturnLineData.callUniqueId);
        returnLine.ReturnReasonId = discountCallReturnLineData.returnReasonId != null ? discountCallReturnLineData.returnReasonId : new ReturnReasonManager(getContext()).getAll().get(0).UniqueId;
        returnLine.ReturnProductTypeId = discountCallReturnLineData.ReturnProductTypeId != null ? discountCallReturnLineData.ReturnProductTypeId : ReturnType.Well;
        returnLine.ProductUniqueId = UUID.fromString(new ProductManager(getContext()).getIdByBackofficeId(discountCallReturnLineData.productId));
        returnLine.TotalRequestAdd1Amount = discountCallReturnLineData.totalReturnAdd1Amount == null ? null : new Currency(discountCallReturnLineData.totalReturnAdd1Amount);
        returnLine.TotalRequestAdd2Amount = discountCallReturnLineData.totalReturnAdd2Amount == null ? null : new Currency(discountCallReturnLineData.totalReturnAdd2Amount);
        returnLine.TotalRequestDis1Amount = discountCallReturnLineData.totalReturnDis1 == null ? null : new Currency(discountCallReturnLineData.totalReturnDis1);
        returnLine.TotalRequestDis2Amount = discountCallReturnLineData.totalReturnDis2 == null ? null : new Currency(discountCallReturnLineData.totalReturnDis2);
        returnLine.TotalRequestDis3Amount = discountCallReturnLineData.totalReturnDis3 == null ? null : new Currency(discountCallReturnLineData.totalReturnDis3);
        returnLine.TotalRequestAddOtherAmount = discountCallReturnLineData.totalReturnAddOtherAmount == null ? null : new Currency(discountCallReturnLineData.totalReturnAddOtherAmount);
        returnLine.TotalRequestDisOtherAmount = discountCallReturnLineData.totalReturnDisOther == null ? null : new Currency(discountCallReturnLineData.totalReturnDisOther);
        returnLine.TotalRequestTax = discountCallReturnLineData.totalReturnTax == null ? null : new Currency(discountCallReturnLineData.totalReturnTax);
        returnLine.TotalRequestCharge = discountCallReturnLineData.totalReturnCharge == null ? null : new Currency(discountCallReturnLineData.totalReturnCharge);
        returnLine.TotalRequestNetAmount = discountCallReturnLineData.totalReturnNetAmount == null ? null : new Currency(discountCallReturnLineData.totalReturnNetAmount);
        returnLine.IsPromoLine = discountCallReturnLineData.isPromoLine;
        if (discountCallReturnLineData.unitPrice != null)
            returnLine.RequestUnitPrice = Currency.valueOf(discountCallReturnLineData.unitPrice.doubleValue());
        insert(returnLine);
        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
        List<ProductUnitViewModel> units = productUnitViewManager.getProductUnits(discountCallReturnLineData.productId, ProductType.isForSale);
        final List<DiscreteUnit> qtyLines = VasHelperMethods.chopTotalQty(discountCallReturnLineData.returnTotalQty, units, true);
        if (qtyLines.size() > 0) {
            for (BaseUnit qtyLine :
                    qtyLines) {
                insertOrUpdateQty((DiscreteUnit) qtyLine, returnLine.UniqueId);
            }
        } else {
            throw new PromotionException(getContext().getString(R.string.error_saving_request));
        }
    }

    public void removePromotions(@NonNull final UUID customerId, @Nullable Boolean isFromRequest) throws DbException {
        try {
            VaranegarApplication.getInstance().getDbHandler().beginTransaction();
            CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getContext());
            CustomerCallReturnTempModelRepository returnTempModelRepository = new CustomerCallReturnTempModelRepository();

            List<CustomerCallReturnModel> returnModels = callReturnManager.getReturnCalls(customerId, true, isFromRequest);
            for (CustomerCallReturnModel returnModel :
                    returnModels) {
                callReturnManager.delete(returnModel.UniqueId);

                TableMap map = new TableMap(CustomerCallReturnTemp.CustomerCallReturnTempTbl, CustomerCallReturn.CustomerCallReturnTbl);
                map.addAllColumns();
                insert(map, Criteria.equals(CustomerCallReturnTemp.UniqueId, returnModel.UniqueId.toString()));

                TableMap linesMap = new TableMap(ReturnLinesTemp.ReturnLinesTempTbl, ReturnLines.ReturnLinesTbl);
                linesMap.addAllColumns();
                insert(linesMap, Criteria.equals(ReturnLinesTemp.ReturnUniqueId, returnModel.UniqueId.toString()));

                List<ReturnLinesModel> returnLinesModels = getReturnLines(returnModel.UniqueId);
                List<String> returnLinesIds = Linq.map(returnLinesModels, new Linq.Map<ReturnLinesModel, String>() {
                    @Override
                    public String run(ReturnLinesModel item) {
                        return item.UniqueId.toString();
                    }
                });
                TableMap qtyMap = new TableMap(ReturnLineQtyTemp.ReturnLineQtyTempTbl, ReturnLineQty.ReturnLineQtyTbl);
                qtyMap.addAllColumns();
                insert(qtyMap, Criteria.in(ReturnLineQtyTemp.ReturnLineUniqueId, returnLinesIds));

                returnTempModelRepository.delete(returnModel.UniqueId);
            }
            VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
        } catch (Exception ex) {
            Timber.e(ex);
            throw ex;
        } finally {
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
        }
    }

    public interface OnPromotionCallBack {
        void onSuccess();

        void onFailure();

        void onCancel();
    }

    public void savePromotion(AppCompatActivity activity, @NonNull final UUID customerId, @Nullable final Boolean isFromRequest, @Nullable final List<DiscountCallReturnLineData> discountCallReturnLineDataList, final OnPromotionCallBack onPromotionCallBack) {
        saveTempReturn(customerId, isFromRequest);
        if (discountCallReturnLineDataList != null && discountCallReturnLineDataList.size() > 0) {
            for (final DiscountCallReturnLineData discountCallReturnLineData :
                    discountCallReturnLineDataList) {
                UUID callUniqueId = UUID.fromString(discountCallReturnLineData.callUniqueId);
                if (discountCallReturnLineData.isPromoLine) {
                    List<CustomerCallReturnLinesViewModel> returnLinesViewModels = new CustomerCallReturnLinesViewManager(getContext()).getReturnLines(callUniqueId, true);
                    CustomerCallReturnLinesViewModel promotion = Linq.findFirst(returnLinesViewModels, new Linq.Criteria<CustomerCallReturnLinesViewModel>() {
                        @Override
                        public boolean run(CustomerCallReturnLinesViewModel item) {
                            return item.ProductCode.equals(discountCallReturnLineData.productCode) && item.IsPromoLine;
                        }
                    });
                    if (promotion != null)
                        discountCallReturnLineData.returnLineUniqueId = promotion.UniqueId.toString();
                }
            }


            List<DiscountCallReturnLineData> dialogItems = new ArrayList<>();
            for (DiscountCallReturnLineData discountCallReturnLineData :
                    discountCallReturnLineDataList) {
                UUID callUniqueId = UUID.fromString(discountCallReturnLineData.callUniqueId);

                if (discountCallReturnLineData.isPromoLine) {
                    List<CustomerCallReturnLinesViewModel> returnLinesViewModels = new CustomerCallReturnLinesViewManager(getContext()).getReturnLines(callUniqueId, true);
                    BigDecimal max = BigDecimal.ZERO;
                    for (CustomerCallReturnLinesViewModel returnLine :
                            returnLinesViewModels) {
                        if (returnLine.ProductCode.equals(discountCallReturnLineData.productCode) && returnLine.TotalReturnQty.compareTo(max) > 0)
                            max = BigDecimal.valueOf(returnLine.TotalReturnQty.doubleValue());
                    }
                    if (discountCallReturnLineData.returnTotalQty.compareTo(max) < 0) {
                        for (CustomerCallReturnLinesViewModel returnLine :
                                returnLinesViewModels) {
                            if (returnLine.ProductCode.equals(discountCallReturnLineData.productCode) && returnLine.TotalReturnQty.compareTo(discountCallReturnLineData.returnTotalQty) > 0)
                                discountCallReturnLineData.ReturnProductTypeId = returnLine.ReturnProductTypeId;
                        }
                    } else {
                        dialogItems.add(discountCallReturnLineData);
                    }
                }
            }

            if (dialogItems.size() == 0) {
                try {
                    insertOrUpdatePromoLines(customerId, isFromRequest, discountCallReturnLineDataList);
                    onPromotionCallBack.onSuccess();
                } catch (Exception e) {
                    Timber.e(e);
                    onPromotionCallBack.onFailure();
                }
            } else
                showDialog(activity, dialogItems, new OnSelectReturnTypeResult() {
                    @Override
                    public void onOk() {
                        try {
                            insertOrUpdatePromoLines(customerId, isFromRequest, discountCallReturnLineDataList);
                            onPromotionCallBack.onSuccess();
                        } catch (Exception ex) {
                            onPromotionCallBack.onFailure();
                            Timber.e(ex);
                        }
                    }

                    @Override
                    public void onCancel() {
                        onPromotionCallBack.onCancel();
                    }
                });
        } else
            onPromotionCallBack.onSuccess();
    }

    private void saveTempReturn(UUID customerId, Boolean isFromRequest) {
        CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getContext());
        CustomerCallReturnTempModelRepository tempModelRepository = new CustomerCallReturnTempModelRepository();
        List<CustomerCallReturnModel> returnModels = callReturnManager.getReturnCalls(customerId, true, isFromRequest);

        for (CustomerCallReturnModel returnModel :
                returnModels) {
            tempModelRepository.delete(returnModel.UniqueId);

            TableMap map = new TableMap(CustomerCallReturn.CustomerCallReturnTbl, CustomerCallReturnTemp.CustomerCallReturnTempTbl);
            map.addAllColumns();
            insert(map, Criteria.equals(CustomerCallReturn.UniqueId, returnModel.UniqueId.toString()));

            TableMap linesMap = new TableMap(ReturnLines.ReturnLinesTbl, ReturnLinesTemp.ReturnLinesTempTbl);
            linesMap.addAllColumns();
            insert(linesMap, Criteria.equals(ReturnLines.ReturnUniqueId, returnModel.UniqueId.toString()));

            List<ReturnLinesModel> returnLinesModels = getReturnLines(returnModel.UniqueId);
            List<String> returnLinesIds = Linq.map(returnLinesModels, new Linq.Map<ReturnLinesModel, String>() {
                @Override
                public String run(ReturnLinesModel item) {
                    return item.UniqueId.toString();
                }
            });
            TableMap qtyMap = new TableMap(ReturnLineQty.ReturnLineQtyTbl, ReturnLineQtyTemp.ReturnLineQtyTempTbl);
            qtyMap.addAllColumns();
            insert(qtyMap, Criteria.in(ReturnLineQty.ReturnLineUniqueId, returnLinesIds));
        }
    }

    private void showDialog(AppCompatActivity activity, List<DiscountCallReturnLineData> discountCallReturnLineDataList, OnSelectReturnTypeResult onSelectReturnTypeResult) {
        ReturnProductTypeDialog returnProductTypeDialog = new ReturnProductTypeDialog();
        returnProductTypeDialog.show(activity.getSupportFragmentManager(), "ReturnProductTypeDialog");
        returnProductTypeDialog.DiscountCallReturnLineDataList = discountCallReturnLineDataList;
        returnProductTypeDialog.onSelectReturnTypeResult = onSelectReturnTypeResult;
    }

    public interface OnSelectReturnTypeResult {
        void onOk();

        void onCancel();
    }

    public void insertOrUpdatePromoLines(@NonNull UUID customerId, @Nullable Boolean isFromRequest, @Nullable List<DiscountCallReturnLineData> discountCallReturnLines) throws ValidationException, DbException, ProductUnitViewManager.UnitNotFoundException, PromotionException {
        try {
            VaranegarApplication.getInstance().getDbHandler().beginTransaction();
            if (discountCallReturnLines != null && discountCallReturnLines.size() > 0) {

                for (DiscountCallReturnLineData returnLine :
                        discountCallReturnLines) {
                    delete(Criteria.equals(ReturnLines.ReturnUniqueId, returnLine.callUniqueId));
                }

                for (final DiscountCallReturnLineData discountCallReturnLine :
                        discountCallReturnLines) {
                    insertPromotionLine(discountCallReturnLine);
                }
            }
            VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
        } catch (Exception ex) {
            Timber.e(ex);
            CustomerCallReturnTempModelRepository tempModelRepository = new CustomerCallReturnTempModelRepository();
            CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getContext());
            List<CustomerCallReturnModel> returnModels = callReturnManager.getReturnCalls(customerId, true, isFromRequest);
            for (CustomerCallReturnModel returnModel :
                    returnModels) {
                tempModelRepository.delete(returnModel.UniqueId);
            }
            throw ex;
        } finally {
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
        }
    }


    private boolean isEmpty(UUID customerId, @Nullable UUID returnId, boolean withRef) {
        CustomerCallReturnLinesViewManager linesViewManager = new CustomerCallReturnLinesViewManager(getContext());
        List<CustomerCallReturnLinesViewModel> lines;
        if (returnId == null)
            lines = linesViewManager.getLines(customerId, withRef, true);
        else
            lines = linesViewManager.getReturnLines(returnId, false);
        for (CustomerCallReturnLinesViewModel line :
                lines) {
            if (line.TotalReturnQty.compareTo(BigDecimal.ZERO) > 0 && !line.IsPromoLine)
                return false;
        }
        return true;
    }

    private boolean isChangedFromRequest(UUID customerId, @Nullable UUID returnId,
                                         boolean withRef) {
        CustomerCallReturnLinesViewManager linesViewManager = new CustomerCallReturnLinesViewManager(getContext());
        CustomerCallReturnLinesRequestViewManager requestViewManager = new CustomerCallReturnLinesRequestViewManager(getContext());
        List<CustomerCallReturnLinesViewModel> lines;
        if (returnId == null)
            lines = linesViewManager.getLines(customerId, withRef, true);
        else
            lines = linesViewManager.getReturnLines(returnId, false);
        List<CustomerCallReturnLinesRequestViewModel> requestLines = requestViewManager.getLines(customerId, true);
        HashMap<UUID, CustomerCallReturnLinesRequestViewModel> requestLinesMap = Linq.toHashMap(requestLines, new Linq.Map<CustomerCallReturnLinesRequestViewModel, UUID>() {
            @Override
            public UUID run(CustomerCallReturnLinesRequestViewModel item) {
                return item.UniqueId;
            }
        });
        for (CustomerCallReturnLinesViewModel line :
                lines) {
            if (!requestLinesMap.containsKey(line.UniqueId))
                return true;
            else {
                CustomerCallReturnLinesRequestViewModel requestLine = requestLinesMap.get(line.UniqueId);
                if (requestLine.TotalReturnQty.compareTo(line.TotalReturnQty) != 0)
                    return true;
            }
        }
        return false;
    }

}
