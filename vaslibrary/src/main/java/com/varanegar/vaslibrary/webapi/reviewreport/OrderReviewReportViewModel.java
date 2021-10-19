package com.varanegar.vaslibrary.webapi.reviewreport;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 5/16/2018.
 */
@Table
public class OrderReviewReportViewModel extends ReviewReportViewModel {
    @Column
    public int RecordId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public String StoreName;
    @Column
    public String OrderNo;
    @Column
    public String OrderDate;
    @Column
    public Currency OrderAmount;
    @Column
    public String OrderStatus;
    @Column
    public String PaymentUsanceTitle;
    @Column
    public String Comment;
}
