package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.UserModelEntity


data class UserModel(
    override val hasError: Boolean,
    override val message: String,
    val data:List<UserModelEntity>
):BaseResponseAbstractModel()
