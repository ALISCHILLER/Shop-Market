package com.msa.eshop.data.Model



data class BannerResponse(
    val banners: List<BannerModel>
) : BaseResponse<List<BannerModel>>(banners, false, null)