package com.msa.eshop.data.Model

data class TokenModel(
    override val hasError: Boolean,
    override val message: String,
    val data: String?
) : BaseResponseAbstractModel()