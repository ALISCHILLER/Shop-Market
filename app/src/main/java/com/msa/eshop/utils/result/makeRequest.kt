package com.msa.eshop.utils.result





import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber


fun <T> makeRequest(
    scope: CoroutineScope,
    request: suspend () -> Flow<Resource<T>>,
    onSuccess: (T) -> Unit,
    updateStateLoading: (Boolean) -> Unit,
    updateStateError: (String?) -> Unit
) {
    scope.launch {
        request().onEach { response ->
            Timber.d(response.data.toString())
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    response.data?.let(onSuccess)
                }
                Resource.Status.LOADING -> {
                    Timber.tag("NetworkUtils").e("Request LOADING")
                    updateStateLoading(true)
                }
                Resource.Status.ERROR -> {
                    Timber.tag("NetworkUtils").e("Request ERROR: ${response.error}")
                    updateStateLoading(false)
                    updateStateError(response.error?.message)
                }
            }
        }.collect()
    }
}


