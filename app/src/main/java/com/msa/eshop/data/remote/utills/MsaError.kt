/*
 * **
 * Pi App Studio. All rights reserved.Copyright (c) 2022.
 *
 */

package com.msa.eshop.data.remote.utills

import okhttp3.ResponseBody

data class MsaError(
    val code:Int = -1,
    val dataError: ResponseBody? =null,
    val message:String ?=null
)