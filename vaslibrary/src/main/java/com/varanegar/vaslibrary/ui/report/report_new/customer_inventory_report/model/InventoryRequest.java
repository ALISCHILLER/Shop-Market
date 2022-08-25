package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model;

/**
 * Create by Mehrdad Latifi on 8/24/2022
 */

public class InventoryRequest {
    String FromDate;
    String ToDate;
    String PersonnelId;
    String CustomerUniqueIdList;
    String VisitTemplatePathId;

    public InventoryRequest(String startDate, String endDate, String personnelId, String customerUniqueIdList, String visitTemplatePathId) {
        this.FromDate = startDate;
        this.ToDate = endDate;
        PersonnelId = personnelId;
        CustomerUniqueIdList = customerUniqueIdList;
        VisitTemplatePathId = visitTemplatePathId;
    }
}
