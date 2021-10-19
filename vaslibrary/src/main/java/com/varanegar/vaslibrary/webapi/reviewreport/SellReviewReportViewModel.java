package com.varanegar.vaslibrary.webapi.reviewreport;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 5/16/2018.
 */
@Table
public class SellReviewReportViewModel extends ReviewReportViewModel {
    @Column
    public int RecordId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public String StoreName;
    @Column
    public String DistStatus;
    @Column
    public String SellNo;
    @Column
    public String SellDate;
    @Column
    public String VoucherNo;
    @Column
    public String VoucherDate;
    @Column
    public String PaymentUsanceTitle;
    @Column
    public Currency SellAmount;
    @Column
    public Currency SellDiscountAmount;
    @Column
    public Currency SellAddAmount;
    @Column
    public Currency SellNetAmount;
    @Column
    public String DistDate;
    @Column
    public String DistributerName;
    @Column
    public String DistNo;
    @Column
    public String Comment;
}
