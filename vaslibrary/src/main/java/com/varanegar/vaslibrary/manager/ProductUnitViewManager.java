package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitView;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModelRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 28/02/2017.
 */

public class ProductUnitViewManager extends BaseManager<ProductUnitViewModel> {

    public ProductUnitViewManager(Context context) {

        super(context, new ProductUnitViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl);
        return query;
    }

    public List<ProductUnitViewModel> getProductUnits(){
        return getItems(getAll());
    }

    public List<ProductUnitViewModel> getProductUnitsId(UUID productId){
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl).whereAnd(Criteria.equals(ProductUnitView.ProductId, productId.toString()));
        return getItems(query);
    }
    @NonNull
    public List<ProductUnitViewModel> getProductUnits(@NonNull UUID productId, ProductType productType) throws UnitNotFoundException, IllegalArgumentException {
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl).whereAnd(Criteria.equals(ProductUnitView.ProductId, productId.toString())).orderByDescending(ProductUnitView.ConvertFactor);
        if (productType == ProductType.isForReturn)
            query.whereAnd(Criteria.equals(ProductUnitView.IsForReturn, true));
        else if (productType == ProductType.isForSale)
            query.whereAnd(Criteria.equals(ProductUnitView.IsForSale, true));
        List<ProductUnitViewModel> units = getItems(query);
        if (units.size() == 0)
            throw new UnitNotFoundException(productId, "", "");
        else
            return units;
    }

    @NonNull
    public List<ProductUnitViewModel> getProductUnits(int backOfficeId, ProductType productType) throws UnitNotFoundException, IllegalArgumentException {
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl).whereAnd(Criteria.equals(ProductUnitView.BackOfficeId, backOfficeId)).orderByDescending(ProductUnitView.ConvertFactor);
        if (productType == ProductType.isForReturn)
            query.whereAnd(Criteria.equals(ProductUnitView.IsForReturn, true));
        else if (productType == ProductType.isForSale)
            query.whereAnd(Criteria.equals(ProductUnitView.IsForSale, true));
        List<ProductUnitViewModel> units = getItems(query);
        if (units.size() == 0)
            throw new UnitNotFoundException(backOfficeId, "", "");
        else
            return units;
    }

    @NonNull
    public List<ProductUnitViewModel> getProductUnits(@NonNull ProductModel product, ProductType productType) throws UnitNotFoundException, IllegalArgumentException {
        if (product.UniqueId != null) {
            Query query = new Query();
            query.from(ProductUnitView.ProductUnitViewTbl).whereAnd(Criteria.equals(ProductUnitView.ProductId, product.UniqueId.toString())).orderByDescending(ProductUnitView.ConvertFactor);
            if (productType == ProductType.isForReturn)
                query.whereAnd(Criteria.equals(ProductUnitView.IsForReturn, true));
            else if (productType == ProductType.isForSale)
                query.whereAnd(Criteria.equals(ProductUnitView.IsForSale, true));
            List<ProductUnitViewModel> units = getItems(query);
            if (units.size() == 0)
                throw new UnitNotFoundException(product.UniqueId, product.ProductCode, product.ProductName);
            else
                return units;
        } else
            throw new IllegalArgumentException("product uniqueId is null");
    }

    public ProductUnitViewModel getUnit(UUID productUnitId) throws UnitNotFoundException {
        ProductUnitViewModel productUnitModel = getItem(productUnitId);
        if (productUnitModel == null)
            throw new UnitNotFoundException(null, null, null);
        return productUnitModel;
    }

    public List<ProductUnitViewModel> getUnits(ProductType productType) {
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl).orderByDescending(ProductUnitView.ConvertFactor);
        if (productType == ProductType.isForReturn)
            query.whereAnd(Criteria.equals(ProductUnitView.IsForReturn, true));
        else if (productType == ProductType.isForSale)
            query.whereAnd(Criteria.equals(ProductUnitView.IsForSale, true));
        List<ProductUnitViewModel> units = getItems(query);
        return units;
    }

    public class ProductUnits {
        public ProductUnitViewModel SmallUnit;
        public ProductUnitViewModel MiddleUnit;
        public ProductUnitViewModel LargeUnit;

        public boolean isBulk() {
            return (SmallUnit != null && SmallUnit.Decimal > 0) || (MiddleUnit != null && MiddleUnit.Decimal > 0) || (LargeUnit != null && LargeUnit.Decimal > 0);
        }

        public boolean hasThirdUnit() {
            int u = 0;
            if (SmallUnit != null)
                u += 1;
            if (MiddleUnit != null)
                u += 1;
            if (LargeUnit != null)
                u += 1;
            return u == 3;
        }
    }

    public HashMap<UUID, ProductUnits> getUnitSet(ProductType productType) {
        List<ProductUnitViewModel> list = getUnits(productType);
        HashMap<UUID, ProductUnits> map = new HashMap<UUID, ProductUnits>();
        for (ProductUnitViewModel productUnitsViewModel :
                list) {
            if (!map.containsKey(productUnitsViewModel.ProductId)) {
                ProductUnits productUnits = new ProductUnits();
                productUnits.SmallUnit = productUnitsViewModel;
                map.put(productUnitsViewModel.ProductId, productUnits);
            } else {
                ProductUnits productUnits = map.get(productUnitsViewModel.ProductId);
                if (productUnits.SmallUnit == null)
                    productUnits.SmallUnit = productUnitsViewModel;
                else {
                    if (productUnits.SmallUnit.ConvertFactor > productUnitsViewModel.ConvertFactor) {
                        ProductUnitViewModel temp = productUnits.SmallUnit;
                        productUnits.SmallUnit = productUnitsViewModel;
                        if (productUnits.LargeUnit == null)
                            productUnits.LargeUnit = temp;
                        else
                            productUnits.MiddleUnit = temp;
                    } else {
                        if (productUnits.LargeUnit == null)
                            productUnits.LargeUnit = productUnitsViewModel;
                        else if (productUnits.LargeUnit.ConvertFactor < productUnitsViewModel.ConvertFactor) {
                            ProductUnitViewModel temp = productUnits.LargeUnit;
                            productUnits.LargeUnit = productUnitsViewModel;
                            productUnits.MiddleUnit = temp;
                        }
                    }
                }
            }
        }
        return map;
    }

    public class UnitNotFoundException extends Exception {
        public UnitNotFoundException(UUID productId, String productCode, String productName) {
            super(String.format("Product unit Not found. Product id = %s , product code = %s , product name = %s", productId == null ? " " : productId.toString(), productCode, productName));
        }

        public UnitNotFoundException(int productBackOfficeId, String productCode, String productName) {
            super(String.format("Product unit Not found. Product BackOffice Id = %s , product code = %s , product name = %s", productBackOfficeId, productCode, productName));
        }
    }

    public ProductUnitViewModel getSmallUnit(UUID productId) {
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl).
                whereAnd(Criteria.equals(ProductUnitView.ProductId, productId.toString())).
                //  whereAnd(Criteria.equals(ProductUnitView.IsForSale,1)).
                        orderByAscending(ProductUnitView.ConvertFactor);
        ProductUnitViewModel productUnitViewModel = getItem(query);
        return productUnitViewModel;
    }

    public ProductUnitViewModel getLargeUnit(UUID productId) {
        Query query = new Query();
        query.from(ProductUnitView.ProductUnitViewTbl).
                whereAnd(Criteria.equals(ProductUnitView.ProductId, productId.toString())).
                // whereAnd(Criteria.equals(ProductUnitView.IsForSale,1)).
                        orderByDescending(ProductUnitView.ConvertFactor);
        ProductUnitViewModel productUnitViewModel = getItem(query);
        return productUnitViewModel;
    }


    public ArrayList<String> getAmountLargeSmall(UUID productId, BigDecimal qty) {
        ProductUnitViewModel largeUnit = getLargeUnit(productId);
        ProductUnitViewModel smallUnit = getLargeUnit(productId);
        ArrayList<String> results = new ArrayList<>();

        if (smallUnit != null) {
            BigDecimal sub;
            if (smallUnit.UnitId != largeUnit.UnitId) {
                sub = qty.divide(BigDecimal.valueOf(largeUnit.ConvertFactor), 5, BigDecimal.ROUND_DOWN);
                BigDecimal larqQty = sub.setScale(0, BigDecimal.ROUND_FLOOR);
                BigDecimal smallQty = qty.subtract(larqQty.multiply(BigDecimal.valueOf(largeUnit.ConvertFactor)));
                results.add(larqQty.toString() + ":" + smallQty.toString());
                results.add(largeUnit.UnitName + ":" + smallUnit.UnitName);
                results.add(largeUnit.UnitRef + ":" + smallUnit.UnitRef);
                results.add(largeUnit.UnitId.toString() + ":" + smallUnit.UnitId.toString());
            } else {
                results.add(qty.toString());
                results.add(smallUnit.UnitName);
                results.add(smallUnit.UnitRef + "");
                results.add(smallUnit.UnitId.toString());
            }

        }
        return results;
    }

}
