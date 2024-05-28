package com.msa.eshop.ui.component.imageloader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ComponentRegistry
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.util.DebugLogger
import com.msa.eshop.R
import kotlinx.coroutines.Dispatchers

@Composable
fun CachedImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.not_load_image,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    // ایجاد یک ImageLoader سفارشی
    val imageLoader = // اگر به دیکودرهای اضافی نیاز دارید، آنها را اضافه کنید. مثلاً SvgDecoder
        ImageLoader.Builder(context)
            .components(fun ComponentRegistry.Builder.() {
                // اگر به دیکودرهای اضافی نیاز دارید، آنها را اضافه کنید. مثلاً SvgDecoder
            })
        .logger(DebugLogger())
        .respectCacheHeaders(false) // نادیده گرفتن هدرهای کش
        .build()

    // ساخت یک ImageRequest با Coil
    val listener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, result: ErrorResult) {
            super.onError(request, result)
        }

        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            super.onSuccess(request, result)
        }
    }

    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl)
        .listener(listener)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageUrl)
        .diskCacheKey(imageUrl)
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    // بارگذاری و نمایش تصویر با AsyncImage
    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        imageLoader = imageLoader // استفاده از ImageLoader سفارشی
    )
}
