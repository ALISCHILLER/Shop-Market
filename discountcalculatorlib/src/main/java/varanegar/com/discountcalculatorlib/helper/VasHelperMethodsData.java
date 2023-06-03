package varanegar.com.discountcalculatorlib.helper;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import varanegar.com.discountcalculatorlib.viewmodel.ProductUnitModelData;

public class VasHelperMethodsData extends HelperMethods {

    @NonNull
    public static List<BaseUnitData> chopTotalQty(BigDecimal totalQty, String unitNames, String convertFactors, @Nullable String delimiter1, @Nullable String delimiter2) {
        final List<BaseUnitData> units = new ArrayList<>();
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
                DiscreteUnitData unit = new DiscreteUnitData();
                BigDecimal value = un.second;
                if (value.compareTo(BigDecimal.ZERO) == 0)
                    unit.value = 0;
                else
                    unit.value =  Rem.divide(value, 0, RoundingMode.FLOOR).doubleValue();
                Rem = Rem.subtract(new BigDecimal(unit.value).multiply(un.second));
                unit.Name = un.first;
                unit.ConvertFactor = un.second.doubleValue();
                units.add(unit);
            }
        }
        return units;
    }

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



    @NonNull
    public static List<DiscreteUnitData> chopTotalQty(BigDecimal totalQty, List<ProductUnitModelData> productUnits, boolean allowZeroQty) {
        final List<DiscreteUnitData> units = new ArrayList<>();
        if (!allowZeroQty && (totalQty == null || totalQty.compareTo(BigDecimal.ZERO) == 0))
            return units;

        if (totalQty == null)
            totalQty = BigDecimal.ZERO;

        Linq.sort(productUnits, new Comparator<ProductUnitModelData>() {
            @Override
            public int compare(ProductUnitModelData t1, ProductUnitModelData t2) {
                return Double.compare(t2.ConvertFactor, t1.ConvertFactor);
            }
        });

        BigDecimal Rem = totalQty;
        for (ProductUnitModelData productUnit : productUnits) {
            DiscreteUnitData unit = new DiscreteUnitData();
            unit.value = (int) Rem.divide(new BigDecimal(String.valueOf(productUnit.ConvertFactor)), 0, RoundingMode.FLOOR).doubleValue();
            Rem = Rem.subtract(new BigDecimal(unit.value).multiply(new BigDecimal(productUnit.ConvertFactor)));
            unit.Name = productUnit.UnitName;
            unit.ConvertFactor = productUnit.ConvertFactor;
            unit.ProductUnitId = productUnit.UniqueId;
            units.add(unit);
        }

        return units;
    }
}
