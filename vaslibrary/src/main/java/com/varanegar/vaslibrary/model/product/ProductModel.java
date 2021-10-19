package com.varanegar.vaslibrary.model.product;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 8/10/2016.
 */
@Table
public class ProductModel extends BaseModel {

    @Column
    public int BackOfficeId;
    @Column
    public String ProductTypeId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public String Description;
    @Column
    public String ProductBoGroupId;
    @Column
    public double productWeight;
    @Column
    public int BrandRef;
    @Column
    public String ProductCtgrRef;
    @Column
    public BigDecimal TaxPercent;
    @Column
    public BigDecimal ChargePercent;
    @Column
    public int CartonType;
    @Column
    public int HasBatch;
    @Column
    public String ProductSubGroup1IdVnLite;
    @Column
    public String ProductSubGroup2IdVnLite;
    @Column
    public int CartonPrizeQty;
    @Column
    public int GoodsVolume;
    @Column
    public String ShipTypeId;
    @Column
    public int HasImage;
    @Column
    public boolean IsFreeItem;
    @Column
    public boolean IsForSale;
    @Column
    public boolean IsForReturnWithRef;
    @Column
    public boolean IsForReturnWithOutRef;
    @Column
    public boolean IsForCount;
    @Column
    public int ShipTypeRef;
    @Column
    public UUID StockUniqueId;
    @Column
    public UUID ProductGroupId;
    @Column
    public String ManufacturerCode;
    @Column
    public int ManufacturerRef;
    @Column
    public BigDecimal OrderPoint;
    @Column
    public boolean IsForRequest;
    @Column
    public UUID ReturnStockUniqueId;
    @Column
    public int	PackUnitRef;
    @Column
    public int	UnitRef;
    @Column
    public int PayDuration;
    @Column
    public boolean IsActive;

    @Override
    public String toString() {
        return ProductName + "(" + ProductCode + ")";
    }
}
