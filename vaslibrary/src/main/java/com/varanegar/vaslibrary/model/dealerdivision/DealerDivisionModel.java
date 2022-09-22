package com.varanegar.vaslibrary.model.dealerdivision;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Create by Mehrdad Latifi on 9/21/2022
 */

@Table
public class DealerDivisionModel extends BaseModel {

    @Column
    @NotNull
    public UUID DivisionCenterKey;

    @Column
    @NotNull
    public String DivisionBackOfficeCode;

    @Column
    @NotNull
    public String DivisionSalesOrg;

    @Column
    @NotNull
    public String DivisionDisChanel;

    @Column
    @NotNull
    public String DivisionCode;


    public DealerDivisionModel() {
    }


    public DealerDivisionModel(String divisionBackOfficeCode, String divisionSalesOrg, String divisionDisChanel, String divisionCode) {
        DivisionBackOfficeCode = divisionBackOfficeCode;
        DivisionSalesOrg = divisionSalesOrg;
        DivisionDisChanel = divisionDisChanel;
        DivisionCode = divisionCode;
    }
}
