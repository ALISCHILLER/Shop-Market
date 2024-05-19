package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.UserModelEntity




data class TokenResponse(
    val token: String?
) : BaseResponse<String?>(token, false, null)