package com.varanegar.vaslibrary.webapi.reviewreport;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 5/16/2018.
 */
@Table
public class SellReturnReviewReportViewModel extends ReviewReportViewModel {
    @Column
    public int RecordId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public String StoreName;
    @Column
    public String SellReturnDate;
    @Column
    public String SellReturnNo;
    @Column
    public String TSaleNo;
    @Column
    public String ReturnTypeName;
    @Column
    public String ReturnReasonName;
    @Column
    public Currency SellReturnAmount;
    @Column
    public Currency SellReturnDiscountAmount;
    @Column
    public Currency SellReturnAddAmount;
    @Column
    public Currency SellReturnNetAmount;
    @Column
    public String DistNo;
    @Column
    public String DistributerName;
    @Column
    public String Comment;
}
