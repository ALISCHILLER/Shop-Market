package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity

data class SimulateResultModel(
    val simulateModel: List<SimulateModel>
): BaseResponse<List<SimulateModel>>(simulateModel, false, null)
