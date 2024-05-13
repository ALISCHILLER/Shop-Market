package com.msa.eshop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msa.eshop.data.local.entity.ProductGroupEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductGroupDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productGroupEntity:List<ProductGroupEntity>)

    @Query("DELETE FROM ProductGroup")
    fun delete()


    @Query("SELECT * FROM ProductGroup")
    fun getAll(): Flow<List<ProductGroupEntity>>

    @Query("INSERT OR REPLACE INTO ProductGroup (productCategoryCode, productCategoryImage) VALUES (0, 'همه')")
    suspend fun insertZeroItem()

}