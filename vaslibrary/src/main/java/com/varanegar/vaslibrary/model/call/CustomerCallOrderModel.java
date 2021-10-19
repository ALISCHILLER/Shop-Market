package com.varanegar.vaslibrary.model.call;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 07/02/2017.
 */

@Table
public class CustomerCallOrderModel extends OrderBaseModel {

    @Column
    public boolean IsSent;
}
