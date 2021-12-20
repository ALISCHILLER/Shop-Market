package com.varanegar.vaslibrary.model.evcstatute;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 11/20/2017.
 */
@Table
public class EvcStatuteProductGroupModel extends BaseModel {
    @Column
    public UUID TemplateId;
    @Column
    public String evcStatuteUniqueId;
    @Column
    public UUID ProductGroupUniqueId;
    @Column
    public String Description;







}
