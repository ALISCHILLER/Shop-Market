package com.varanegar.vaslibrary.base;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/7/2018.
 */

public class VasHelperMethods extends HelperMethods {
    /**
     * @param totalQty
     * @param unitNames      for example box,item
     * @param convertFactors for example 10:5
     * @param delimiter1     delimiter for unit names, you can leave it null, default delimiter value is :
     * @param delimiter2     delimiter for convert factors, you can leave it null, default delimiter value is :
     * @return
     */
    @NonNull
    public static List<BaseUnit> chopTotalQty(BigDecimal totalQty, String unitNames, String convertFactors, @Nullable String delimiter1, @Nullable String delimiter2) {
        final List<BaseUnit> units = new ArrayList<>();
        if (totalQty == null || totalQty.compareTo(BigDecimal.ZERO) == 0)
            return units;
        if (unitNames != null
                && convertFactors != null
                && !unitNames.isEmpty()
                && !convertFactors.isEmpty()) {
            if (delimiter1 == null)
                delimiter1 = ":";
            if (delimiter2 == null)
                delimiter2 = ":";
            String[] unitNameList = unitNames.split(delimiter1);
            String[] strUnitList = convertFactors.split(delimiter2);
            List<Pair<String, BigDecimal>> unitss = new ArrayList<>();
            int j = 0;
            for (String name :
                    unitNameList) {
                unitss.add(new Pair<>(name, new BigDecimal(strUnitList[j])));
                j++;
            }
            List<Double> strUnitsInts = new ArrayList<Double>();
            for (String aStrUnitList : strUnitList) {
                strUnitsInts.add(Double.valueOf(aStrUnitList));
            }
            Linq.sort(unitss, new Comparator<Pair<String, BigDecimal>>() {
                @Override
                public int compare(Pair<String, BigDecimal> t1, Pair<String, BigDecimal> t2) {
                    if (t1.second == null && t2.second == null)
                        return 0;
                    if (t2.second == null)
                        return -1;
                    if (t1.second == null)
                        return 1;
                    return t2.second.compareTo(t1.second);
                }
            });
            Linq.sort(strUnitsInts, new Comparator<Double>() {
                @Override
                public int compare(Double t1, Double t2) {
                    if (t1.equals(t2))
                        return 0;
                    else if (t1.compareTo(t2) > 0)
                        return 1;
                    else
                        return -1;
                }
            });

            BigDecimal Rem = totalQty;
            for (Pair<String, BigDecimal> un : unitss) {
                DiscreteUnit unit = new DiscreteUnit();
                BigDecimal value = un.second;
                if (value.compareTo(BigDecimal.ZERO) == 0)
                    unit.value = 0;
                else
                    unit.value = (int) Rem.divide(value, 0, RoundingMode.FLOOR).doubleValue();
                Rem = Rem.subtract(new BigDecimal(unit.value).multiply(un.second));
                unit.Name = un.first;
                unit.ConvertFactor = un.second.doubleValue();
                units.add(unit);
            }
        }
        return units;
    }

    /**
     * @param totalQty
     * @param unitNames      for example box,item
     * @param convertFactors for example 10:5
     * @param delimiter1     delimiter for unit names, you can leave it null, default delimiter value is :
     * @param delimiter2     delimiter for convert factors, you can leave it null, default delimiter value is :
     * @return
     */
    @NonNull
    public static String chopTotalQtyToString(BigDecimal totalQty, String unitNames, String convertFactors, @Nullable String delimiter1, @Nullable String delimiter2) {
        String units = "";
        if (totalQty == null)
            return units;
        if (totalQty.compareTo(BigDecimal.ZERO) == 0)
            return "0";
        if (unitNames != null
                && convertFactors != null
                && !unitNames.isEmpty()
                && !convertFactors.isEmpty()) {
            if (delimiter1 == null)
                delimiter1 = ":";
            if (delimiter2 == null)
                delimiter2 = ":";
            String[] unitNameList = unitNames.split(delimiter1);
            String[] strUnitList = convertFactors.split(delimiter2);
            List<Pair<String, BigDecimal>> unitss = new ArrayList<>();
            int j = 0;
            for (String name :
                    unitNameList) {
                unitss.add(new Pair<>(name, new BigDecimal(strUnitList[j])));
                j++;
            }
            List<Double> strUnitsInts = new ArrayList<Double>();
            for (String aStrUnitList : strUnitList) {
                strUnitsInts.add(Double.valueOf(aStrUnitList));
            }
            Linq.sort(unitss, new Comparator<Pair<String, BigDecimal>>() {
                @Override
                public int compare(Pair<String, BigDecimal> t1, Pair<String, BigDecimal> t2) {
                    if (t1.second == null && t2.second == null)
                        return 0;
                    if (t2.second == null)
                        return -1;
                    if (t1.second == null)
                        return 1;
                    return t2.second.compareTo(t1.second);
                }
            });
            Linq.sort(strUnitsInts, new Comparator<Double>() {
                @Override
                public int compare(Double t1, Double t2) {
                    if (t1.equals(t2))
                        return 0;
                    else if (t1.compareTo(t2) > 0)
                        return 1;
                    else
                        return -1;
                }
            });

            BigDecimal Rem = totalQty;
            for (Pair<String, BigDecimal> un : unitss) {
                BigDecimal convertFactor = un.second;
                double value = 0;
                if (convertFactor.compareTo(BigDecimal.ZERO) == 0)
                    value = 0;
                else
                    value = (int) Rem.divide(convertFactor, 0, RoundingMode.FLOOR).doubleValue();
                Rem = Rem.subtract(new BigDecimal(value).multiply(un.second));
                if (units.isEmpty())
                    units += HelperMethods.doubleToString(value);
                else
                    units += ":" + HelperMethods.doubleToString(value);
            }
        }
        return units;
    }

    /**
     * @param totalQty
     * @param productUnits
     * @return
     */
    @NonNull
    public static List<DiscreteUnit> chopTotalQty(BigDecimal totalQty, List<ProductUnitViewModel> productUnits, boolean allowZeroQty) {
        final List<DiscreteUnit> units = new ArrayList<>();
        if (!allowZeroQty && (totalQty == null || totalQty.compareTo(BigDecimal.ZERO) == 0))
            return units;

        if (totalQty == null)
            totalQty = BigDecimal.ZERO;

        Linq.sort(productUnits, new Comparator<ProductUnitViewModel>() {
            @Override
            public int compare(ProductUnitViewModel t1, ProductUnitViewModel t2) {
                return Double.compare(t2.ConvertFactor, t1.ConvertFactor);
            }
        });

        BigDecimal Rem = totalQty;
        for (ProductUnitViewModel productUnit : productUnits) {
            DiscreteUnit unit = new DiscreteUnit();
            unit.value = (int) Rem.divide(new BigDecimal(String.valueOf(productUnit.ConvertFactor)), 0, RoundingMode.FLOOR).doubleValue();
            Rem = Rem.subtract(new BigDecimal(unit.value).multiply(new BigDecimal(productUnit.ConvertFactor)));
            unit.Name = productUnit.UnitName;
            unit.ConvertFactor = productUnit.ConvertFactor;
            unit.ProductUnitId = productUnit.UniqueId;
            units.add(unit);
        }

        return units;
    }

    @NonNull
    public static Locale getSysConfigLocale(Context context) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel languageConfig = sysConfigManager.read(ConfigKey.ServerLanguage, SysConfigManager.cloud);
        UUID languageId = SysConfigManager.getUUIDValue(languageConfig);
//        if (languageId == null)
//            return Locale.getDefault();
        Locale locale = LocalModel.getLocal(languageId);
        if (locale != null)
            return locale;
        return Locale.getDefault();
    }

    public static Boolean canNotEditOperationAfterPrint(Context context, UUID customerId) {
        int printCounts = 0;
        Boolean unSubmitCancellation = false;
        if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            CustomerPrintCountManager customerPrintCountManager = new CustomerPrintCountManager(context);
            printCounts = customerPrintCountManager.getCount(customerId);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            SysConfigModel unSubmitCancellationConfig = sysConfigManager.read(ConfigKey.UnSubmitCancellation, SysConfigManager.cloud);
            unSubmitCancellation = SysConfigManager.compare(unSubmitCancellationConfig, true);
        }
        return (unSubmitCancellation && (printCounts > 0));
    }

    @NonNull
    public static List<BaseUnit> totalQtyUnits(BigDecimal totalQty, String UnitId, String convertFactors, @Nullable String delimiter1, @Nullable String delimiter2) {
        final List<BaseUnit> units = new ArrayList<>();
        if (totalQty == null || totalQty.compareTo(BigDecimal.ZERO) == 0)
            return units;
        if (UnitId != null
                && convertFactors != null
                && !UnitId.isEmpty()
                && !convertFactors.isEmpty()) {
            if (delimiter1 == null)
                delimiter1 = ":";
            if (delimiter2 == null)
                delimiter2 = ":";
            String[] strUnitIdList = UnitId.split(delimiter1);
            String[] strConvertFactorList = convertFactors.split(delimiter2);
            List<Pair<String, BigDecimal>> pairUnits = new ArrayList<>();
            int j = 0;
            for (String unitId :
                    strUnitIdList) {
                pairUnits.add(new Pair<>(unitId, new BigDecimal(strConvertFactorList[j])));
                j++;
            }
            Linq.sort(pairUnits, new Comparator<Pair<String, BigDecimal>>() {
                @Override
                public int compare(Pair<String, BigDecimal> t1, Pair<String, BigDecimal> t2) {
                    if (t1.second == null && t2.second == null)
                        return 0;
                    if (t2.second == null)
                        return -1;
                    if (t1.second == null)
                        return 1;
                    return t2.second.compareTo(t1.second);
                }
            });
            BigDecimal Rem = totalQty;
            for (Pair<String, BigDecimal> un : pairUnits) {
                DiscreteUnit unit = new DiscreteUnit();
                BigDecimal value = un.second;
                if (value.compareTo(BigDecimal.ZERO) == 0)
                    unit.value = 0;
                else
                    unit.value = (int) Rem.divide(value, 0, RoundingMode.FLOOR).doubleValue();
                Rem = Rem.subtract(new BigDecimal(unit.value).multiply(un.second));
                unit.ProductUnitId = UUID.fromString(un.first);
                unit.ConvertFactor = un.second.doubleValue();
                units.add(unit);
            }
        }
        return units;
    }
}
