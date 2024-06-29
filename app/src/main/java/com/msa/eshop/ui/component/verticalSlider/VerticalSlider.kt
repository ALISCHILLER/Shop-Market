package com.msa.eshop.ui.component.verticalSlider

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.msa.eshop.data.Model.response.BannerModel
import com.msa.eshop.ui.theme.*
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalSlider(
    modifier: Modifier = Modifier,
    imageUrl: List<BannerModel> = emptyList()
) {

//    val imageUrl = listOf(
//        "https://axprint.com/blog/wp-content/uploads/2020/10/girl4.jpg",
//        "https://www.gfxdownload.ir/uploads/posts/2023-09/nature3.jpg",
//        "https://dl1.mrtarh.com/QLUU-GXDA/preview.jpg",
//        "https://www.jowhareh.com/images/Jowhareh/galleries_5/poster_24691af5-5db6-49c2-a775-6ce55ab66bc3.jpeg",
//        "https://mag.parsnews.com/wp-content/uploads/2022/06/Most-Beautiful-Nature-Wallpapers-Top-Free-Most-Beautiful-25.jpg",
//        "https://www.eligasht.com/Blog/wp-content/uploads/2019/03/maxresdefault-2.jpg"
//    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { imageUrl.size })

    VerticalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(start = PaddingLarge, end = PaddingLarge, bottom = 25.dp, top = PaddingNormal),
    ) { page ->
        val pageOffset = (page - pagerState.currentPage) + pagerState.currentPageOffsetFraction
        val scaleFactor = 0.85f + (1f - 0.85f) * (1f - pageOffset.absoluteValue)
        LoadAndCacheImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scaleFactor
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .offset(
                    y = if (pagerState.currentPage == page) 0.dp * pageOffset else (-130 * pageOffset).dp
                )
                .fillMaxSize()
                .clip(RoundedCornerShape(CornerSizeLarge)),
            imageLink = imageUrl[page].bannerImage,
            token = ""
        )
    }
}
