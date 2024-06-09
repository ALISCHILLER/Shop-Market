package com.msa.eshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msa.eshop.data.local.dao.OrderAddressDao
import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.dao.PaymentMethodDao
import com.msa.eshop.data.local.dao.ProductDao
import com.msa.eshop.data.local.dao.ProductGroupDao
import com.msa.eshop.data.local.dao.UserDao
import com.msa.eshop.data.local.entity.OrderAddressEntity
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity

@Database(
    entities = [
        UserModelEntity::class,
        ProductModelEntity::class,
        ProductGroupEntity::class,
        OrderEntity::class,
        PaymentMethodEntity::class,
        OrderAddressEntity::class,
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun productGroupDao(): ProductGroupDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun orderAddressDao(): OrderAddressDao

}