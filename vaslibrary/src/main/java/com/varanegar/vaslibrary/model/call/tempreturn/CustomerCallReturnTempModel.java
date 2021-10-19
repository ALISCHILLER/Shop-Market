package com.varanegar.vaslibrary.model.call.tempreturn;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.call.ReturnBaseModel;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CustomerCallReturnTempModel extends ReturnBaseModel {
    @Column
    public boolean IsCancelled;
}
