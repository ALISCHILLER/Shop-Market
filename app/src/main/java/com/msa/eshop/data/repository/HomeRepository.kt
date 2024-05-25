package com.msa.eshop.data.repository


import com.msa.eshop.data.Model.BannerResponse
import com.msa.eshop.data.Model.DiscountResponse
import com.msa.eshop.data.Model.ProductGroupResponse
import com.msa.eshop.data.Model.ProductResponse
import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.dao.ProductDao
import com.msa.eshop.data.local.dao.ProductGroupDao
import com.msa.eshop.data.local.dao.UserDao
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity
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
    private val orderDao: OrderDao,
    )  {

    val getAllProduct: Flow<List<ProductModelEntity>> = productDao.getAll()
    val getAllProductGroup: Flow<List<ProductGroupEntity>> = productGroupDao.getAll()
    val getAllOrder: Flow<List<OrderEntity>> = orderDao.getAll()


    // Product
    suspend fun productRequest(
    ): Flow<Resource<ProductResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO) {
                apiService.getProductData()
            }
        } as Flow<Resource<ProductResponse?>>
    }

    // Product Group
    suspend fun productGroupRequest(
    ): Flow<Resource<ProductGroupResponse?>> {
        return apiManager.makeSafeApiCall {
            withContext(Dispatchers.IO) {
                apiService.getProductGroupData()
            }
        } as Flow<Resource<ProductGroupResponse?>>
    }

    suspend fun requestBanner():Flow<Resource<BannerResponse>>{
        return apiManager.makeSafeApiCall {
            apiService.getBanner()
        } as Flow<Resource<BannerResponse>>
    }

    suspend fun requestDiscount(productCode:String):Flow<Resource<DiscountResponse>>{
        return apiManager.makeSafeApiCall {
            apiService.getListDiscounts(productCode)
        }as Flow<Resource<DiscountResponse>>
    }

    fun getProductCount(): Int {
        return productDao.getProductCount()
    }


    fun getProduct(code: Int): Flow<List<ProductModelEntity>> {
        return productDao.getProduct(code)
    }



     fun insertProductGroup(productGroupEntity: List<ProductGroupEntity>){
        productGroupDao.insert(productGroupEntity)
    }

    fun insertProduct(productModelEntity: List<ProductModelEntity>){
        productDao.insert(productModelEntity)
    }



    fun searchProduct(search:String): Flow<List<ProductModelEntity>>{
        return productDao.searchProducts(search)
    }



    fun getOrder(code: Int): Flow<OrderEntity> {
        return orderDao.getOrder(code.toString())
    }
    suspend fun insertOrder(orderEntity: OrderEntity){
        orderDao.insert(orderEntity)
    }
    suspend fun deleteOrder(orderId: String){
        orderDao.deleteOrder(orderId)
    }
}