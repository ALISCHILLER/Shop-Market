package com.varanegar.vaslibrary.webapi.reviewreport;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;

/**
 * Created by A.Torabi on 5/16/2018.
 */
@Table
public class ProductGroupReviewReportViewModel extends ReviewReportViewModel {
    @Column
    public int RecordId;
    @Column
    public String ProductGroupName;
    @Column
    public BigDecimal SellQty;
    @Column
    public BigDecimal SellPackQty;
    @Column
    public Currency SellAmount;
    @Column
    public BigDecimal PrizeQty;
    @Column
    public BigDecimal FreeReasonQty;
    @Column
    public Currency SellDiscountAmount;
    @Column
    public Currency SellAddAmount;
    @Column
    public Currency SellNetAmount;
    @Column
    public BigDecimal SellReturnQty;
    @Column
    public BigDecimal HealthySellReturnQty;
    @Column
    public BigDecimal UnHealthySellReturnQty;
    @Column
    public Currency SellReturnNetAmount;
    @Column
    public Currency AmountWithoutReturn;
}
