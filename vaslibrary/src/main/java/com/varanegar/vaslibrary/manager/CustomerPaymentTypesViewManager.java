package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModel;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/7/2017.
 */

public class CustomerPaymentTypesViewManager extends BaseManager<CustomerPaymentTypesViewModel> {
    public CustomerPaymentTypesViewManager(Context context) {
        super(context, new CustomerPaymentTypesViewModelRepository());
    }

    public List<CustomerPaymentTypesViewModel> getSystemCheckPaymentTypes(UUID customerUniqueId, String checkSystemTypeUniqueId) {
        String sql;
        sql = "SELECT\n" +
                "pto.UniqueId AS UniqueId,\n" +
                "c.UniqueId AS CustomerUniqueId,\n" +
                "pto.GroupBackOfficeId AS PaymentTypeOrderGroupRef,\n" +
                "pto.PaymentTypeOrderName AS PaymentTypeOrderName,\n" +
                "pto.CheckDebit AS CheckDebit,\n" +
                "pto.CheckCredit AS CheckCredit,\n" +
                "pto.BackOfficeId AS BackOfficeId,\n" +
                "pto.PaymentTypeOrderGroupUniqueId AS PaymentTypeOrderGroupUniqueId,\n" +
                "pto.PaymentTypeOrderGroupName AS PaymentTypeOrderGroupName\n" +
                "FROM Customer c JOIN PaymentTypeOrder pto ON c.UniqueId = '" + customerUniqueId.toString() + "'\n" +
                //"JOIN DealerPaymentType ON  PaymentTypeOrderUniqueId = pto.UniqueId \n" +
                "WHERE (CASE WHEN (c.PayableTypes & pto.Code = pto.Code) THEN 1 ELSE 0 END) = 1 \n" +
                "AND pto.PaymentTypeOrderGroupUniqueId = '" + checkSystemTypeUniqueId + "'\n" +
                "ORDER BY pto.Code;";
        return getItems(sql, null);
    }

    public List<CustomerPaymentTypesViewModel> getCustomerPaymentType(UUID customerUniqueId) throws UnknownBackOfficeException {
        String sql;
        BackOfficeType backOfficeType = new SysConfigManager(getContext()).getBackOfficeType();
//        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
//        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SystemPaymentTypeId, SysConfigManager.cloud);
        if (backOfficeType == BackOfficeType.Varanegar) {
            sql = "SELECT\n" +
                    "pto.UniqueId AS UniqueId,\n" +
                    "c.UniqueId AS CustomerUniqueId,\n" +
                    "pto.GroupBackOfficeId AS PaymentTypeOrderGroupRef,\n" +
                    "pto.PaymentTypeOrderName AS PaymentTypeOrderName,\n" +
                    "pto.CheckDebit AS CheckDebit,\n" +
                    "pto.CheckCredit AS CheckCredit,\n" +
                    "pto.BackOfficeId AS BackOfficeId,\n" +
                    "pto.PaymentTypeOrderGroupUniqueId AS PaymentTypeOrderGroupUniqueId,\n" +
                    "pto.PaymentTypeOrderGroupName AS PaymentTypeOrderGroupName\n" +
                    "FROM Customer c JOIN PaymentTypeOrder pto ON c.UniqueId = '" + customerUniqueId.toString() + "'\n";
                    if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                        sql = sql + "JOIN DealerPaymentType ON  PaymentTypeOrderUniqueId = pto.UniqueId \n";
                    sql = sql + "WHERE (CASE WHEN (c.PayableTypes & pto.Code = pto.Code) THEN 1 ELSE 0 END) = 1 \n";
//            if (sysConfigModel != null)
//                sql = sql + "AND pto.PaymentTypeOrderGroupUniqueId <> '" + sysConfigModel.Value + "' \n";
            sql = sql + "ORDER BY pto.Code;";
        } else if (backOfficeType == BackOfficeType.Rastak) {
            sql = "SELECT\n" +
                    "pto.UniqueId AS UniqueId,\n" +
                    "c.UniqueId AS CustomerUniqueId,\n" +
                    "pto.GroupBackOfficeId AS PaymentTypeOrderGroupRef,\n" +
                    "pto.PaymentTypeOrderName AS PaymentTypeOrderName,\n" +
                    "pto.CheckDebit AS CheckDebit,\n" +
                    "pto.CheckCredit AS CheckCredit,\n" +
                    "pto.BackOfficeId AS BackOfficeId,\n" +
                    "pto.PaymentTypeOrderGroupUniqueId AS PaymentTypeOrderGroupUniqueId,\n" +
                    "pto.PaymentTypeOrderGroupName AS PaymentTypeOrderGroupName\n" +
                    "FROM Customer c JOIN PaymentTypeOrder pto ON c.UniqueId = '" + customerUniqueId.toString() + "' \n" +
                    "JOIN DealerPaymentType ON  PaymentTypeOrderUniqueId = pto.UniqueId \n" +
                    "ORDER BY pto.Code;";
        } else {    //BackOfficeType.ThirdParty
            sql = "SELECT\n" +
                    "pto.UniqueId AS UniqueId,\n" +
                    "c.UniqueId AS CustomerUniqueId,\n" +
                    "pto.GroupBackOfficeId AS PaymentTypeOrderGroupRef,\n" +
                    "pto.PaymentTypeOrderName AS PaymentTypeOrderName,\n" +
                    "pto.CheckDebit AS CheckDebit,\n" +
                    "pto.CheckCredit AS CheckCredit,\n" +
                    "pto.BackOfficeId AS BackOfficeId,\n" +
                    "pto.PaymentTypeOrderGroupUniqueId AS PaymentTypeOrderGroupUniqueId,\n" +
                    "pto.PaymentTypeOrderGroupName AS PaymentTypeOrderGroupName\n" +
                    "FROM Customer c JOIN PaymentTypeOrder pto ON c.UniqueId = '" + customerUniqueId.toString() + "' \n" +
                    "JOIN DealerPaymentType ON  PaymentTypeOrderUniqueId = pto.UniqueId \n" +
                    "WHERE (c.PayableTypes = pto.Code) \n" +
                    "ORDER BY pto.Code;";
        }

        return getItems(sql, null);
    }

    public CustomerPaymentTypesViewModel getPaymentTypeById(UUID orderPaymentTypeUniqueId) {
        return null;
    }
}
