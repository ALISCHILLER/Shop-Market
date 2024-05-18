package com.msa.eshop.data.Model


data class BannerRequestModel(
    override val hasError: Boolean,
    override val message: String,
    val data: List<BannerModel>?
) : BaseResponseAbstractModel()