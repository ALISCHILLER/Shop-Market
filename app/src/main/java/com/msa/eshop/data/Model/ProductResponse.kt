package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductModelEntity


data class ProductResponse(
    val products: List<ProductModelEntity>
) : BaseResponse<List<ProductModelEntity>>(products, false, null)
