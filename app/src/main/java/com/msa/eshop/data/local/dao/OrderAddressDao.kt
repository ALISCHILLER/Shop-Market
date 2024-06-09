package com.msa.eshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.eshop.data.local.entity.OrderAddressEntity
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface OrderAddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pyment: OrderAddressEntity)


    @Query("DELETE FROM `orderAddress`")
    fun delete()

    @Query("SELECT * FROM orderAddress")
    fun getAllOrderAddress(): Flow<List<OrderAddressEntity>>
}