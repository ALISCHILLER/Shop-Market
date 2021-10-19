package com.varanegar.vaslibrary.model.profileView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 18/03/2017.
 */
@Table
public class ProfileViewModel extends BaseModel {

    @Column
    public int  Ordered;
    @Column
    public int Rejected;
    @Column
    public int Visited;
    @Column
    public int Delivered;
    @Column
    public int DistCustCount;
    @Column
    public int CompletelyReturned;
    @Column
    public Currency TotalDist;
    @Column
    public Currency DeliveredAmount;
    @Column
    public Currency ReturnedAmount;
    @Column
    public Currency OrderAmount;
    @Column
    public Currency DistAmount;
    @Column
    public int CustCount;

}
