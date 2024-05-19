package com.msa.eshop.data.Model

open class BaseResponse<T>(
    val data: T?,
    val hasError: Boolean,
    val message: String?
)
