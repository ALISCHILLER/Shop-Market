package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.OrderAddressModel
import com.msa.eshop.data.Model.response.PaymentTermModle

data class PaymentTermResponse(
    val paymentTerm: List<PaymentTermModle>
) : BaseResponse<List<PaymentTermModle>>(paymentTerm, false, null)