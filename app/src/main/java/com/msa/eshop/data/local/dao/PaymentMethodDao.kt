package com.msa.eshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pyment: PaymentMethodEntity)


    @Query("DELETE FROM `paymentMethod`")
    fun delete()

    @Query("SELECT * FROM paymentMethod")
     fun getAllPaymentMethods(): Flow<List<PaymentMethodEntity>>
}