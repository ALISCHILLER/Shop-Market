package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.BannerModel


data class BannerResponse(
    val banners: List<BannerModel>
) : BaseResponse<List<BannerModel>>(banners, false, null)