package com.varanegar.vaslibrary.model.WarehouseProductQtyView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.base.VasHelperMethods;

import java.math.BigDecimal;
import java.util.UUID;


/**
 * Created by s.foroughi on 16/01/2017.
 */
@Table
public class WarehouseProductQtyViewModel extends BaseModel {
    @Column
    public UUID ProductTypeId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal RenewQty;
    @Column
    public BigDecimal TotalQty;
    @Column
    public BigDecimal RemainedQty;
    @Column
    public String UnitName;
    @Column
    public String ConvertFactor;
    @Column
    public Currency SalePrice;
    @Column
    public Currency RemainedPriceQty;
    @Column
    public String BatchNo;
    @Column
    public BigDecimal TotalReturnQty;
    @Property
    public String OnHandQtyView;
    @Property
    public String RenewQtyView;
    @Property
    public String TotalQtyView;
    @Property
    public String RemainedQtyView;
    @Column
    public BigDecimal ReservedQty;
    @Column
    public BigDecimal RemainedAfterReservedQty;

    @Override
    public void setProperties() {
        super.setProperties();
        if (ProductTypeId == null || ProductTypeId.equals(UUID.fromString("7a16fdaa-8d2f-4b4e-9f38-92750e79902e"))) {
            // not bulk
            OnHandQtyView = VasHelperMethods.chopTotalQtyToString(OnHandQty, UnitName, ConvertFactor, ":", ":");
            RenewQtyView = VasHelperMethods.chopTotalQtyToString(RenewQty, UnitName, ConvertFactor, ":", ":");
            TotalQtyView = VasHelperMethods.chopTotalQtyToString(TotalQty, UnitName, ConvertFactor, ":", ":");
            BigDecimal onHand = OnHandQty == null ? BigDecimal.ZERO : OnHandQty;
            BigDecimal total = TotalQty == null ? BigDecimal.ZERO : TotalQty;
            RemainedQtyView = VasHelperMethods.chopTotalQtyToString(onHand.subtract(total), UnitName, ConvertFactor, ":", ":");
        } else {
            OnHandQtyView = HelperMethods.bigDecimalToString(OnHandQty);
            RenewQtyView = HelperMethods.bigDecimalToString(RenewQty);
            TotalQtyView = HelperMethods.bigDecimalToString(TotalQty);
            BigDecimal onHand = OnHandQty == null ? BigDecimal.ZERO : OnHandQty;
            BigDecimal total = TotalQty == null ? BigDecimal.ZERO : TotalQty;
            RemainedQtyView = HelperMethods.bigDecimalToString(onHand.subtract(total));
        }

    }
}
