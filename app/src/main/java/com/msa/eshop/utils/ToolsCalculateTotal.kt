package com.msa.eshop.utils

import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity

fun calculateTotalValue(
    value1: Int,
    value2: Int,
    convertFactor2: Int
): Int {
    val valueNumber = (value2 * (convertFactor2 ?: 0)) + value1
    return valueNumber.coerceAtLeast(0)
}

fun calculateSalePrice(
    totalValue: Int,
    price: Int
): Float {
    return totalValue * price.toFloat()
}

fun createOrderEntity(
    productModelEntity: ProductModelEntity,
    totalValue: Int,
    value1: Int,
    value2: Int
): OrderEntity {
    with(productModelEntity) {
        return OrderEntity(
            id = id,
            convertFactor1 = convertFactor1,
            convertFactor2 = convertFactor2,
            fullNameKala1 = fullNameKala1,
            fullNameKala2 = fullNameKala2,
            productCode = productCode,
            productGroupCode = productGroupCode,
            productName = productName,
            unit1 = unit1,
            unit2 = unit2,
            unitid1 = unitid1,
            unitid2 = unitid2,
            price = price,
            productImage = productImage,
            numberOrder = totalValue,
            numberOrder1 = value1,
            numberOrder2 = value2,
            unitOrder = unit1
        )
    }
}