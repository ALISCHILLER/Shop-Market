package com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Table
public class CustomerGroupLastSalesReportModel extends BaseModel {
    @Column
    public UUID customerUniqeID ;
    @Column
    public String productBackOfficeCode ;
    @Column
    public String customerGroup ;
    @Column
    public String customerGroupTXT ;
    @Column
    public String customerActivity;
    @Column
    public String customerActivityTXT ;
    @Column
    public BigDecimal netWeight ;
    @Column
    public BigDecimal netCount_CA ;
    @Column
    public BigDecimal netCount_EA ;
    @Column
    public String productGroupName  ;
    @Column
    public int productGroupCode ;
    @Column
    public BigDecimal weightProductGroup ;
    @Column
    public String customerCode;
    @Column
    public BigDecimal reshteh;
    @Column
    public BigDecimal formi ;
    @Column
    public BigDecimal jumbo ;
    @Column
    public BigDecimal ashianeh;
    @Column
    public BigDecimal lazania;
    @Column
    public BigDecimal podrKeik;
    @Column
    public BigDecimal reshtehAsh;
    @Column
    public BigDecimal ard;
    @Column
    public BigDecimal haftGhaleh;
    @Column
    public BigDecimal vegan;
    @Column
    public BigDecimal protoeen;
    @Column
    public String lastSaleProductGroup;

}
