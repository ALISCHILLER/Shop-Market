package com.msa.eshop.data.Model

data class ChangePasswordResponse(
    val insertCart:Boolean
) : BaseResponse<Boolean>(insertCart, false, null)
