package com.varanegar.supervisor.model;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/8/2018.
 */
@Table
public class VisitorModel extends BaseModel {
    @Column
    public String Name;
    @Column
    public Integer Status;
    @Column
    public String Phone;
    @Override
    public String toString() {
        return Name;
    }
}
