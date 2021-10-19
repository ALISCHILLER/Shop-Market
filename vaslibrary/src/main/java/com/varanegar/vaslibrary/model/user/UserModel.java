package com.varanegar.vaslibrary.model.user;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;


/**
 * Created by atp on 8/10/2016.
 */
@Table
public class UserModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String UserName;
    @Column
    public Integer Status;
    @Column
    public boolean IsSalesman;
    @Column
    public boolean IsDistributer;
    @Column
    public boolean IsCollector;
    @Nullable
    public Date LoginDate;
    @Override
    public String toString() {
        return UserName;
    }
}
