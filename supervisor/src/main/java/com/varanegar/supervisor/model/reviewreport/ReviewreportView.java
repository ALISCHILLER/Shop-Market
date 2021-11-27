package com.varanegar.supervisor.model.reviewreport;

import com.varanegar.framework.database.model.ModelProjection;

public class ReviewreportView extends ModelProjection<ReviewreportModel> {
    protected ReviewreportView(String name) {
        super(name);
    }

    public static ReviewreportView orderNumber = new ReviewreportView("Reviewreport.orderNumber");
    public static ReviewreportView orderStatus = new ReviewreportView("Reviewreport.orderStatus");
    public static ReviewreportView orderDate = new ReviewreportView("Reviewreport.orderDate");
    public static ReviewreportView dealerCode = new ReviewreportView("Reviewreport.dealerCode");
    public static ReviewreportView dealerName = new ReviewreportView("Reviewreport.dealerName");
    public static ReviewreportView customerCode = new ReviewreportView("Reviewreport.customerCode");
    public static ReviewreportView customerName = new ReviewreportView("Reviewreport.customerName");
    public static ReviewreportView paymentType = new ReviewreportView("Reviewreport.paymentType");
    public static ReviewreportView comment = new ReviewreportView("Reviewreport.comment");
    public static ReviewreportView customerCategory = new ReviewreportView("Reviewreport.customerCategory");
    public static ReviewreportView UniqueId = new ReviewreportView("Reviewreport.UniqueId");
    public static ReviewreportView ReviewreportTbl = new ReviewreportView("Reviewreport");
    public static ReviewreportView ReviewreportAll = new ReviewreportView("Reviewreport.*");
}
