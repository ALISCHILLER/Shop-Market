package com.msa.eshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.eshop.data.local.entity.ProductModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productModelEntity: List<ProductModelEntity>)

    @Query("DELETE FROM product")
    fun delete()

    @Query("SELECT * FROM product")
    fun getAll(): Flow<List<ProductModelEntity>>

    @Query("SELECT * FROM product where productGroupCode= :code")
    fun getProduct(code:Int): Flow<List<ProductModelEntity>>

    @Query("SELECT COUNT(*) FROM product")
     fun getProductCount(): Int


    @Query("SELECT * FROM product WHERE productName LIKE '%' || :searchQuery ||  '%'")
     fun searchProducts(searchQuery: String): Flow<List<ProductModelEntity>>
}
