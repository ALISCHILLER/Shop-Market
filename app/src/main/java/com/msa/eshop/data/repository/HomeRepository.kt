package com.msa.eshop.data.repository

import com.msa.eshop.data.Model.ProductGroupModel
import com.msa.eshop.data.Model.ProductModel
import com.msa.eshop.data.local.dao.ProductDao
import com.msa.eshop.data.local.dao.ProductGroupDao
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.data.remote.utills.MakeSafeApiCall
import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiManager: MakeSafeApiCall,
    private val  productDao: ProductDao,
    private val productGroupDao: ProductGroupDao,
){



    fun getProduct(code: Int): Flow<List<ProductModelEntity>> {
        return productDao.getProduct(code)
    }

    val getAllProduct: Flow<List<ProductModelEntity>> = productDao.getAll()
    val getAllProductGroup: Flow<List<ProductGroupEntity>> = productGroupDao.getAll()

    suspend fun productRequest(
    ): Flow<Resource<ProductModel?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO) {
                apiService.getProductData()
            }
        } as Flow<Resource<ProductModel?>>
    }


    suspend fun productGroupRequest(
    ): Flow<Resource<ProductGroupModel?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO) {
                apiService.getProductGroupData()
            }
        } as Flow<Resource<ProductGroupModel?>>
    }



    suspend fun insertProductGroup(productGroupEntity: List<ProductGroupEntity>){
        productGroupDao.insertZeroItem()
        productGroupDao.insert(productGroupEntity)
    }
}