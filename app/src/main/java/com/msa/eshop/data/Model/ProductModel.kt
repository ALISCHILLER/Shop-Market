package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductModelEntity

data class ProductModel(
    override val hasError: Boolean,
    override val message: String,
    val data:List<ProductModelEntity>
):BaseResponseAbstractModel()
