package com.varanegar.vaslibrary.model.customercallreturnlinesview;

import androidx.annotation.Nullable;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 4/15/2017.
 */
@Table
public class CustomerCallReturnLinesViewModel extends BaseCustomerCallReturnLinesViewModel {
    @Column
    public BigDecimal OriginalTotalReturnQty;
    @Column
    public boolean IsFromRequest;
    @Column
    public boolean IsCancelled;
    @Nullable
    @Column
    public UUID EditReasonId;
    @Column
    public String ReturnRequestBackOfficeNo;

}
