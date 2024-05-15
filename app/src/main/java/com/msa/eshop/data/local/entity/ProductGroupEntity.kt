package com.msa.eshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("ProductGroup")
data class ProductGroupEntity(
    @PrimaryKey
    val productCategoryCode: Int,
    val productCategoryName: String?,
    val productCategoryImage: String?,

)