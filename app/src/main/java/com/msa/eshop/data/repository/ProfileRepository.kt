package com.msa.eshop.data.repository

import com.msa.eshop.data.local.dao.UserDao
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.UserModelEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val userDao: UserDao
) {
    val getUser: Flow<UserModelEntity> = userDao.getUserLogin()

}