package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.ReportCartDetailsModel
import com.msa.eshop.data.local.entity.ProductGroupEntity

data class ReportCartDetailsResponse(
    val reportCartDetails: List<ReportCartDetailsModel>
):BaseResponse<List<ReportCartDetailsModel>>(reportCartDetails,false,null)