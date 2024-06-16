package com.msa.eshop.data.Model.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
