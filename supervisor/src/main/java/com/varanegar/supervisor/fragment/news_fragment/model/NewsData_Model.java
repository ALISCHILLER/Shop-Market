package com.varanegar.supervisor.fragment.news_fragment.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class NewsData_Model extends BaseModel {
    @Column
    public String title;
    @Column
    public String body;
    @Column
    public String subSystemType;
    @Column
    public String centerUniqueIds;
    @Column
    public String publishDate;
    @Column
    public String publishPDate;
}
