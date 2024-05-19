package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductGroupEntity


data class ProductGroupResponse(
    val productGroups: List<ProductGroupEntity>
) : BaseResponse<List<ProductGroupEntity>>(productGroups, false, null)
