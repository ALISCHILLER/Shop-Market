package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.call.CustomerCallReturn;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModelRepository;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequestModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQty;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyRequest;
import com.varanegar.vaslibrary.model.call.ReturnLines;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequest;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequestModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 4/10/2017.
 */

public class CustomerCallReturnManager extends BaseManager<CustomerCallReturnModel> {
    public CustomerCallReturnManager(Context context) {
        super(context, new CustomerCallReturnModelRepository());
    }

    public long addOrUpdateCallReturn(boolean isFromRequest,
                                      @NonNull final List<ReturnCalculatorItem> returnCalculatorItems,
                                      @NonNull final UUID customerUniqueId,
                                      @Nullable final UUID saleId,
                                      @Nullable final UUID stockId,
                                      @Nullable final Currency price,
                                      final String comment,
                                      final String saleNo,
                                      @Nullable UUID dealerId,
                                      UUID returnUniqueId,
                                      boolean ReplacementRegistration,
                                      @Nullable String referenceNo,
                                      @Nullable UUID editReasonId) throws ValidationException, DbException {
        CustomerCallReturnModel callReturn;
        Query query = new Query();
        if (saleId == null) {
            query.from(CustomerCallReturn.CustomerCallReturnTbl)
                    .whereAnd(Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerUniqueId.toString())
                            .and(Criteria.equals(CustomerCallReturn.BackOfficeInvoiceId, null)))
                    .whereAnd(Criteria.equals(CustomerCallReturn.IsFromRequest, isFromRequest));
        } else {
            query.from(CustomerCallReturn.CustomerCallReturnTbl)
                    .whereAnd(Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerUniqueId.toString())
                            .and(Criteria.equals(CustomerCallReturn.BackOfficeInvoiceId, saleId.toString())))
                    .whereAnd(Criteria.equals(CustomerCallReturn.IsFromRequest, isFromRequest));
        }
        if (returnUniqueId != null)
            query.whereAnd(Criteria.equals(CustomerCallReturn.UniqueId, returnUniqueId));
        callReturn = getItem(query);
        if (callReturn == null) {
            callReturn = new CustomerCallReturnModel();
            callReturn.CustomerUniqueId = customerUniqueId;
            callReturn.UniqueId = UUID.randomUUID();
            callReturn.Comment = comment;
            callReturn.DealerUniqueId = dealerId;
            callReturn.StartTime = new Date();
            callReturn.IsFromRequest = false;
            if (saleId == null)
                callReturn.ReturnTypeUniqueId = ReturnType.WithoutRef;
            else {
                callReturn.ReturnTypeUniqueId = ReturnType.WithRef;
                callReturn.BackOfficeInvoiceId = saleId;
                callReturn.BackOfficeInvoiceNo =  saleNo;
            }
            callReturn.ReplacementRegistration = ReplacementRegistration;
            callReturn.PersonnelUniqueId = UserManager.readFromFile(getContext()).UniqueId;
            final CustomerCallReturnModel finalCallReturn = callReturn;
            long affectedRows = insert(callReturn);
            ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
            affectedRows += returnLinesManager.addOrUpdateReturnLines(returnCalculatorItems, finalCallReturn.UniqueId, stockId, price , referenceNo, editReasonId);
            return affectedRows;
        } else {
            if (!isFromRequest) {
                callReturn.Comment = comment;
                callReturn.DealerUniqueId = dealerId;
            } else if (callReturn.StartTime == null)
                callReturn.StartTime = new Date();
            callReturn.ReplacementRegistration = ReplacementRegistration;
            callReturn.EndTime = new Date();
            long affectedRows = update(callReturn);
            ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
            affectedRows += returnLinesManager.addOrUpdateReturnLines(returnCalculatorItems, callReturn.UniqueId, stockId, price , referenceNo, editReasonId);
            return affectedRows;
        }
    }

    public List<CustomerCallReturnModel> getReturnCalls(UUID customerId, @Nullable Boolean withRef, @Nullable Boolean isFromRequest) {
        if (withRef == null) {
            Query query = new Query();
            query.from(CustomerCallReturn.CustomerCallReturnTbl).whereAnd(Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerId.toString()));
            if (isFromRequest != null)
                query.whereAnd(Criteria.equals(CustomerCallReturn.IsFromRequest, isFromRequest));
            return getItems(query);
        } else {
            Query query = new Query();
            if (withRef)
                query.from(CustomerCallReturn.CustomerCallReturnTbl).whereAnd(Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerId.toString())
                        .and(Criteria.equals(CustomerCallReturn.ReturnTypeUniqueId, ReturnType.WithRef)));
            else
                query.from(CustomerCallReturn.CustomerCallReturnTbl).whereAnd(Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerId.toString())
                        .and(Criteria.equals(CustomerCallReturn.ReturnTypeUniqueId, ReturnType.WithoutRef)));
            if (isFromRequest != null)
                query.whereAnd(Criteria.equals(CustomerCallReturn.IsFromRequest, isFromRequest));
            return getItems(query);
        }
    }

    public void cancel(final CustomerModel customerModel, final boolean withRef, @Nullable Boolean isFromRequest) throws DbException, ValidationException {
        List<CustomerCallReturnModel> callReturnModels = getReturnCalls(customerModel.UniqueId, withRef, isFromRequest);
        if (callReturnModels.size() == 0)
            return;
        Criteria criteria;
        if (withRef)
            criteria = Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerModel.UniqueId)
                    .and(Criteria.notEquals(CustomerCallReturn.BackOfficeInvoiceId, null));
        else
            criteria = Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerModel.UniqueId)
                    .and(Criteria.equals(CustomerCallReturn.BackOfficeInvoiceId, null));
        delete(criteria);
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        CustomerCallType type;
        if (withRef)
            type = CustomerCallType.SaveReturnRequestWithRef;
        else
            type = CustomerCallType.SaveReturnRequestWithoutRef;
        callManager.removeCalls(customerModel.UniqueId, type);
        callManager.unConfirmAllCalls(customerModel.UniqueId);
    }

    public void cancelAllReturns(final UUID customerId, @Nullable Boolean isFromRequest) throws DbException, ValidationException {
        List<CustomerCallReturnModel> callReturnModels = getReturnCalls(customerId, null, isFromRequest);
        if (callReturnModels.size() > 0) {
            delete(Criteria.equals(CustomerCallReturn.CustomerUniqueId, customerId));
            final CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
            customerCallManager.removeCall(CustomerCallType.SaveReturnRequestWithRef, customerId);
            customerCallManager.removeCall(CustomerCallType.SaveReturnRequestWithoutRef, customerId);
            customerCallManager.unConfirmAllCalls(customerId);
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void initCall(@NonNull UUID customerId, boolean withRef) throws ReturnInitException, DbException, ValidationException {
        CustomerManager customerManager = new CustomerManager(getContext());
        CustomerModel customerModel = customerManager.getItem(customerId);
        CustomerCallReturnManager callManager = new CustomerCallReturnManager(getContext());
        callManager.cancel(customerModel, withRef, true);
        CustomerCallReturnRequestManager customerCallReturnRequestManager = new CustomerCallReturnRequestManager(getContext());
        final List<CustomerCallReturnRequestModel> callReturnRequestModels = customerCallReturnRequestManager.getCustomerCallReturns(customerId, withRef);
        if (callReturnRequestModels.size() == 0)
            throw new ReturnInitException(getContext().getString(R.string.order_id_not_found));

        for (CustomerCallReturnRequestModel request :
                callReturnRequestModels) {
            try {

                ReturnLinesRequestManager returnLinesRequestManager = new ReturnLinesRequestManager(getContext());
                List<ReturnLinesRequestModel> lines = returnLinesRequestManager.getLines(request.UniqueId);
                if (lines.size() == 0)
                    throw new ReturnInitException(getContext().getString(R.string.order_line_items_empty));

                delete(request.UniqueId);
                CustomerCallReturnModel returnModel = request.convertRequestToReturnModel();
                returnModel.StartTime = new Date();
                insert(returnModel);
                Timber.i("Customer call return for request= (" + request.UniqueId + ") init ");

                TableMap map2 = new TableMap(ReturnLinesRequest.ReturnLinesRequestTbl, ReturnLines.ReturnLinesTbl);
                map2.addAllColumns();
                insert(map2, Criteria.equals(ReturnLinesRequest.ReturnUniqueId, request.UniqueId.toString()));
                Timber.i("Customer call return line for request =(" + request.UniqueId + ") init ");

                List<String> returnLinesIds = Linq.map(lines, new Linq.Map<ReturnLinesRequestModel, String>() {
                    @Override
                    public String run(ReturnLinesRequestModel item) {
                        return item.UniqueId.toString();
                    }
                });
                TableMap map3 = new TableMap(ReturnLineQtyRequest.ReturnLineQtyRequestTbl, ReturnLineQty.ReturnLineQtyTbl);
                map3.addAllColumns();
                insert(map3, Criteria.in(ReturnLineQtyRequest.ReturnLineUniqueId, returnLinesIds));
                Timber.i("Customer call return line qty details init ");
            } catch (DbException e) {
                Timber.e(e);
                throw new ReturnInitException(getContext().getString(R.string.order_line_items_empty));
            }
        }
    }

    public List<UUID> getEnabledReturnTypes(@Nullable UUID customerId) {
        SysConfigManager configManager = new SysConfigManager(getContext());
        SysConfigModel allowReturnWithRef = configManager.read(ConfigKey.AllowReturnWithRef, SysConfigManager.cloud);
        SysConfigModel allowReturnWithoutRef = configManager.read(ConfigKey.AllowReturnWithoutRef, SysConfigManager.cloud);
        SysConfigModel returnFromRequest = configManager.read(ConfigKey.RefRetOrder, SysConfigManager.cloud);

        boolean withRef = SysConfigManager.compare(allowReturnWithRef, true);
        boolean withoutRef = SysConfigManager.compare(allowReturnWithoutRef, true);
        boolean withRefFromRequest = false;
        boolean withoutRefFromRequest = false;

        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ReplacementRegistration, SysConfigManager.cloud);
            List<UUID> returnTypes = new ArrayList<>();
            if (withRef)
                returnTypes.add(ReturnType.WithRef);
            if (withoutRef)
                returnTypes.add(ReturnType.WithoutRef);
            if (SysConfigManager.compare(sysConfigModel, true)) {
                if (withRef)
                    returnTypes.add(ReturnType.WithRefReplacementRegistration);
                if (withoutRef)
                    returnTypes.add(ReturnType.WithoutRefReplacementRegistration);
            }
            return returnTypes;
        } else {
            if (SysConfigManager.compare(returnFromRequest, "1")) {
                withRef = false;
                withoutRef = false;
            }
            List<CustomerCallReturnRequestModel> requestModels = new ArrayList<>();
            if (customerId != null) {
                CustomerCallReturnRequestManager manager = new CustomerCallReturnRequestManager(getContext());
                requestModels = manager.getCustomerCallReturns(customerId, null);
            }
            for (CustomerCallReturnRequestModel request :
                    requestModels) {
                if (request.BackOfficeInvoiceId != null)
                    withRefFromRequest = true;
                else
                    withoutRefFromRequest = true;
            }
            List<UUID> returnTypes = new ArrayList<>();
            if (withRef)
                returnTypes.add(ReturnType.WithRef);
            if (withoutRef)
                returnTypes.add(ReturnType.WithoutRef);
            if (withRefFromRequest)
                returnTypes.add(ReturnType.WithRefFromRequest);
            if (withoutRefFromRequest)
                returnTypes.add(ReturnType.WithoutRefFromRequest);
            return returnTypes;
        }

    }

}
