package com.varanegar.vaslibrary.model.customeractiontime;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/21/2018.
 */
@Table
public class CallStartEndTimeModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public Date StartDate;
    @Column
    public Date EndDate;

    /***
     *
     * @return returns call duration in milliseconds
     */
    public long getDuration() {
        if (StartDate == null)
            return 0;
        if (EndDate == null)
            return new Date().getTime() - StartDate.getTime();

        return EndDate.getTime() - StartDate.getTime();
    }
}
