package com.varanegar.vaslibrary.model.customercallreturnview;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

@Table
public class CustomerCallReturnAfterDiscountViewModel extends CustomerCallReturnBaseViewModel {
    @Column
    public String ReturnReasonName;
    @Column
    public String EditReasonName;
    @Column
    public String ReturnRequestBackOfficeNo;
}
