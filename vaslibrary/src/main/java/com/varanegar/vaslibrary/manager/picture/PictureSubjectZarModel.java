package com.varanegar.vaslibrary.manager.picture;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class PictureSubjectZarModel extends BaseModel {
    @Column
    @SerializedName("subSystemTypeUniqueId")
    public UUID subSystemTypeUniqueId ;

    @Column
    @SerializedName("demandTypeUniqueId")
    public UUID demandTypeUniqueId ;
    @Column
    @SerializedName("subjectTitle")
    public String subjectTitle;
    @Column
    @SerializedName("centerUniqueIds")
    public String centerUniqueIds;
    @Nullable
    @Column
    @SerializedName("customerCategoryUniqueIds")
    public String customerCategoryUniqueIds;
    @Column
    @Nullable
    @SerializedName("customerActivityUniqueIds")
    public String customerActivityUniqueIds ;


}
