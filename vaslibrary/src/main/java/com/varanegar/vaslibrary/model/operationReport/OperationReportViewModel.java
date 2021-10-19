package com.varanegar.vaslibrary.model.operationReport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by g.aliakbar on 22/04/2018.
 */
@Table
public class OperationReportViewModel extends BaseModel {
        @Column
        public String CustomerName;
        @Column
        public String CustomerCode;
        @Column
        public String StoreName;
        @Column
        public Currency TotalNetAmount;
        @Column
        public Currency TotalAmount;
        @Column
        public Currency AmountDiscount;
        @Column
        public Currency AmountCard;
        @Column
        public Currency AmountCredit;
        @Column
        public Currency AmountCash;
        @Column
        public Currency PayableAmount;
        @Column
        public Currency Recipe;
        @Column
        public Currency AmountCheque;
        @Column
        public Currency OrderDiscountAmount;
        @Column
        public Currency OrderAddAmount;
        @Column
        public Currency ReturnDiscountAmount;
        @Column
        public Currency ReturnAddAmount;
        @Column
        public Currency TotalPaidAmount;
        @Column
        public Currency ReturnRequestAmount;
}
