@file:OptIn(ExperimentalFoundationApi::class)

package com.msa.eshop.ui.component.pager


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.ui.util.lerp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.msa.eshop.R
import com.msa.eshop.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.absoluteValue
@Composable
fun AutoImsgtSlider(modifier: Modifier = Modifier) {

}

@ExperimentalPagerApi
@Composable
fun SliderBanner(
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val imageSlider = listOf(
        "https://imgv3.fotor.com/images/slider-image/Female-portrait-picture-enhanced-with-better-clarity-and-higher-quality-using-Fotors-free-online-AI-photo-enhancer.jpg",
        "https://imgv3.fotor.com/images/slider-image/A-blurry-close-up-photo-of-a-woman.jpg",
    )

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(2600)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % (pagerState.pageCount)
            )
        }
    }

    Column(modifier=modifier.padding(top=10.dp)){
        HorizontalPager(
            count = imageSlider.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = DIMENS_16dp),
            modifier = modifier
                .fillMaxWidth()
        ) { page ->
            Card(
                shape = RoundedCornerShape(DIMENS_12dp),
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                Image(
                    modifier = modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.banner),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )

            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(DIMENS_16dp)
        )
    }
}

@ExperimentalPagerApi
@Preview
@Composable
fun SliderBannerPreview() {
    SliderBanner()
}