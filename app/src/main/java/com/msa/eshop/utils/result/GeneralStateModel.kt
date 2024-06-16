package com.msa.eshop.utils.result



data class GeneralStateModel(
    val isLoading:Boolean = false,
    val error: String?=null,
    val message: String?=null,
)
