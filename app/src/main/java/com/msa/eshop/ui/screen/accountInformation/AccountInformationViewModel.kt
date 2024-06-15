package com.msa.eshop.ui.screen.accountInformation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.data.repository.ProfileRepository
import com.msa.eshop.ui.navigation.NavManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class AccountInformationViewModel @Inject constructor(
    private val navManager: NavManager,
    private val profileRepository: ProfileRepository
):ViewModel(){
    private val _user = MutableStateFlow<UserModelEntity?>(null)
    val user: MutableStateFlow<UserModelEntity?> = _user

    init {
        getUser()
    }
    private fun getUser() {
        viewModelScope.launch {
            profileRepository.getUser.collect {
                Timber.tag(TAG).e("getUser: %s", it)
                _user.value = it
            }
        }
    }
}