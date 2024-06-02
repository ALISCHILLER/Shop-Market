package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.SimulateModel

data class SimulateResultModel(
    val simulateModel: List<SimulateModel>
): BaseResponse<List<SimulateModel>>(simulateModel, false, null)
