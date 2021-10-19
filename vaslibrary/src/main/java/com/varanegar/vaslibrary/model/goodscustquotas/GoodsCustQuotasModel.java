package com.varanegar.vaslibrary.model.goodscustquotas;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 11/18/2018.
 */
@Table
public class GoodsCustQuotasModel extends BaseModel {
    @Column
    public int id;
    @Column
    public String startDate;
    @Column
    public String endDate;
    @Column
    public int ruleNo;
    @Column
    public String ruleDesc;
    @Column
    public boolean applyInGroup;
    @Column
    public int goodsRef;
    @Column
    public int goodsCtgrRef;
    @Column
    public int mainTypeRef;
    @Column
    public int subTypeRef;
    @Column
    public int dcRef;
    @Column
    public int custRef;
    @Column
    public int custCtgrRef;
    @Column
    public int custActRef;
    @Column
    public int stateRef;
    @Column
    public int countyRef;
    @Column
    public int areaRef;
    @Column
    public int saleOfficeRef;
    @Column
    public int minQty;
    @Column
    public int maxQty;
    @Column
    public int unitRef;
    @Column
    public int checkDuration;
    @Column
    public String hostName;
    @Column
    public int userRef;
    @Column
    public String defineDate;
    @Column
    public int goodsGroupRef;
    @Column
    public int manufacturerRef;
    @Column
    public int custLevelRef;
    @Column
    public UUID unitUniqueId;
    @Column
    public UUID unitStatusUniqueId;
    @Column
    public UUID goodUniqueId;
    @Column
    public UUID goodGroupUniqueId;
    @Column
    public UUID dcUniqueId;
    @Column
    public UUID customerUniqueId;
    @Column
    public UUID customerActUniqueId;
    @Column
    public UUID stateUniqueId;
    @Column
    public UUID countyUniqueId;
    @Column
    public UUID areaUniqueId;
}
