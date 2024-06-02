package com.msa.eshop.data.Model.request

data class TokenRequest(
    var customerCode: String? ,
    var password: String?
) {
}