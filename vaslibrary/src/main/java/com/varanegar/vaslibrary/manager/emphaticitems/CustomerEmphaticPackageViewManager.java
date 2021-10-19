package com.varanegar.vaslibrary.manager.emphaticitems;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customeremphaticpackageview.CustomerEmphaticPackageView;
import com.varanegar.vaslibrary.model.customeremphaticpackageview.CustomerEmphaticPackageViewModel;
import com.varanegar.vaslibrary.model.customeremphaticpackageview.CustomerEmphaticPackageViewModelRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/16/2017.
 */

public class CustomerEmphaticPackageViewManager extends BaseManager<CustomerEmphaticPackageViewModel> {

    public CustomerEmphaticPackageViewManager(@NonNull Context context) {
        super(context, new CustomerEmphaticPackageViewModelRepository());
    }

    public List<CustomerEmphaticPackageViewModel> getPackageRules(@NonNull UUID customerId) {
        Date date = new Date();
        Query query = new Query();
        query.from(CustomerEmphaticPackageView.CustomerEmphaticPackageViewTbl)
                .whereAnd(Criteria.valueBetween(date.getTime(), CustomerEmphaticPackageView.FromDate, CustomerEmphaticPackageView.ToDate)
                        .and(Criteria.equals(CustomerEmphaticPackageView.CustomerId, customerId.toString())));

        return getItems(query);
    }

    public EmphaticPackageCheckResult checkEmphaticPackages(UUID customerId, List<CustomerCallOrderOrderViewModel> orderItems) {
        String error = null;
        String warning = null;
        HashMap<UUID, BigDecimal> packageCounts = new HashMap<>();
        for (CustomerCallOrderOrderViewModel line :
                orderItems) {
            if (line.EmphasisRuleId != null) {
                if (packageCounts.containsKey(line.EmphasisRuleId))
                    packageCounts.put(line.EmphasisRuleId, line.TotalQty.add(packageCounts.get(line.EmphasisRuleId)));
                else
                    packageCounts.put(line.EmphasisRuleId, line.TotalQty);
            }
        }

        CustomerEmphaticPackageViewManager emphaticProductManager = new CustomerEmphaticPackageViewManager(getContext());
        List<CustomerEmphaticPackageViewModel> emphaticProductModels = emphaticProductManager.getPackageRules(customerId);
        HashMap<UUID, BigDecimal> qtys = new HashMap<>();
        for (CustomerEmphaticPackageViewModel emphaticProductModel :
                emphaticProductModels) {
            if (!packageCounts.containsKey(emphaticProductModel.RuleId)) {
                qtys.put(emphaticProductModel.RuleId, BigDecimal.ZERO);
                if (emphaticProductModel.TypeId.equals(EmphasisProductErrorTypeId.DETERRENT)) {
                    if (error == null)
                        error = "";
                    error += getContext().getString(R.string.package_ephatic_items_error, VasHelperMethods.bigDecimalToString(emphaticProductModel.PackageCount), emphaticProductModel.Title, "0") + "\n";
                } else if (emphaticProductModel.TypeId.equals(EmphasisProductErrorTypeId.WARNING))
                    warning += getContext().getString(R.string.package_ephatic_items_error, VasHelperMethods.bigDecimalToString(emphaticProductModel.PackageCount), emphaticProductModel.Title, "0") + "\n";
            } else {
                BigDecimal qty = packageCounts.get(emphaticProductModel.RuleId);
                qtys.put(emphaticProductModel.RuleId, qty);
                if (qty.compareTo(emphaticProductModel.PackageCount) < 0) {
                    if (emphaticProductModel.TypeId.equals(EmphasisProductErrorTypeId.DETERRENT)) {
                        if (error == null)
                            error = "";
                        error += getContext().getString(R.string.package_ephatic_items_error, VasHelperMethods.bigDecimalToString(emphaticProductModel.PackageCount), emphaticProductModel.Title, VasHelperMethods.bigDecimalToString(qty)) + "\n";
                    } else if (emphaticProductModel.TypeId.equals(EmphasisProductErrorTypeId.WARNING))
                        warning += getContext().getString(R.string.package_ephatic_items_error, VasHelperMethods.bigDecimalToString(emphaticProductModel.PackageCount), emphaticProductModel.Title, "0") + "\n";
                }
            }
        }
        return new EmphaticPackageCheckResult(error, warning, qtys);
    }
}
