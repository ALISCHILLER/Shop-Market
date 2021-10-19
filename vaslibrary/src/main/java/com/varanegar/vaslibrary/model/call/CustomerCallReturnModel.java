package com.varanegar.vaslibrary.model.call;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.call.tempreturn.CustomerCallReturnTempModel;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CustomerCallReturnModel extends ReturnBaseModel {
    @Column
    public boolean IsCancelled;
}
