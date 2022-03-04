package com.varanegar.vaslibrary.model.call;

import androidx.annotation.ColorInt;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/18/2018.
 */

public class ReturnBaseModel extends BaseModel {
    @Column
    @NotNull
    public UUID CustomerUniqueId;
    @Column
    @NotNull
    public UUID ReturnTypeUniqueId;
    @Column
    @NotNull
    public UUID PersonnelUniqueId;
    @Column
    public UUID ShipToPartyUniqueId;
    @Column
    public String ShipToPartyCode;
    @Column
    public String LocalPaperNo;
    @Column
    public String BackOfficeDistId;
    @Column
    public UUID BackOfficeInvoiceId;
    @Column
    public String BackOfficeInvoiceNo;
    @Column
    public Date BackOfficeInvoiceDate;
    @Column
    public Long ReturnRequestBackOfficeId;
    @Column
    public Date ReturnRequestBackOfficeDate;
    @Column
    public String ReturnRequestBackOfficeNo;
    @Column
    public String Comment;
    @Column
    @SerializedName("dcRefSDS")
    public int DCRefSDS;
    @Column
    public int SaleOfficeRefSDS;
    @Column
    public Date StartTime;
    @Column
    public Date EndTime;
    @Column
    public UUID DealerUniqueId;
    @Column
    public boolean IsFromRequest;
    @Column
    public boolean ReplacementRegistration;
}
