package com.varanegar.vaslibrary.model.city;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CityModel extends BaseModel {

    @Column
    public String CityName;

    @Column
    public int BackOfficeId;

    @Column
    public UUID StateUniqueId;

    @Override
    public String toString() {
        return CityName;
    }
}
