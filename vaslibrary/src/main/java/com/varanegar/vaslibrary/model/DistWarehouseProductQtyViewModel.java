package com.varanegar.vaslibrary.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.base.VasHelperMethods;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 3/29/2020.
 */

@Table
public class DistWarehouseProductQtyViewModel extends BaseModel {
    @Column
    public UUID ProductTypeId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal TotalOrderQty;
    @Column
    public BigDecimal TotalReturnedQty;
    @Column
    public BigDecimal WellReturnQty;
    @Column
    public BigDecimal WasteReturnQty;
    @Column
    public BigDecimal WarehouseProductQty;
    @Column
    public String UnitName;
    @Column
    public String ConvertFactor;
    @Column
    public String ProductUnitId;
    @Property
    public String OnHandQtyView;
    @Property
    public String TotalReturnedQtyView;
    @Property
    public String WellReturnQtyView;
    @Property
    public String WasteReturnQtyView;
    @Property
    public String WarehouseProductQtyView;
    @Column
    public BigDecimal productWeight;
    @Column
    public BigDecimal TotalWeight;


    @Override
    public void setProperties() {
        super.setProperties();
        if (ProductTypeId == null || ProductTypeId.equals(UUID.fromString("7a16fdaa-8d2f-4b4e-9f38-92750e79902e"))) {
            // not bulk
            OnHandQtyView = VasHelperMethods.chopTotalQtyToString(OnHandQty, UnitName, ConvertFactor, ":", ":");
            TotalReturnedQtyView = VasHelperMethods.chopTotalQtyToString(TotalReturnedQty, UnitName, ConvertFactor, ":", ":");
            WellReturnQtyView = VasHelperMethods.chopTotalQtyToString(WellReturnQty, UnitName, ConvertFactor, ":", ":");
            WasteReturnQtyView = VasHelperMethods.chopTotalQtyToString(WasteReturnQty, UnitName, ConvertFactor, ":", ":");
            WarehouseProductQtyView = VasHelperMethods.chopTotalQtyToString(WarehouseProductQty, UnitName, ConvertFactor, ":", ":");
        } else {
            OnHandQtyView = HelperMethods.bigDecimalToString(OnHandQty);
            TotalReturnedQtyView = HelperMethods.bigDecimalToString(TotalReturnedQty);
            WellReturnQtyView = HelperMethods.bigDecimalToString(WellReturnQty);
            WasteReturnQtyView = HelperMethods.bigDecimalToString(WasteReturnQty);
            WarehouseProductQtyView = HelperMethods.bigDecimalToString(WarehouseProductQty);
        }

    }
}
