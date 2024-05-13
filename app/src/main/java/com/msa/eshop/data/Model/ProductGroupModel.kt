package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductGroupEntity

data class ProductGroupModel(
    override val hasError: Boolean,
    override val message: String,
    val data:List<ProductGroupEntity>

):BaseResponseAbstractModel()
