@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.msa.eshop.ui.screen.addressRegistration

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorColors
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.msa.eshop.R
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight
import com.msa.eshop.ui.theme.barcolorlight2

@SuppressLint("ResourceType")
@Preview
@Composable
fun AddressRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {

    val state = rememberRichTextState()
    Scaffold(
        modifier = modifier
            .background(color = Color.White),
        topBar = {
            TopBarDetails(" آدرس جدید")
        },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .shadow(5.dp)
                        .background(color = MaterialTheme.colors.surface)
                        .weight(1.0f)
                        .fillMaxSize()
                        .padding(horizontal = 5.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .weight(1f)
                                .border(
                                    width = 2.dp, // عرض حاشیه
                                    color = Color.Gray, // رنگ حاشیه
                                    shape = RoundedCornerShape(16.dp) // شکل حاشیه
                                )
                        ) {
                            RichTextEditor(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 3.dp)
                                    .fillMaxSize(),
                                state = state,
                                colors = RichTextEditorDefaults.richTextEditorColors(
                                    containerColor = Color.White
                                ),
                                label = { Text(text = "آدرس جدید را وارد کنید...") },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f) // فضای باکس خالی برای پر کردن نیمه دوم صفحه
                        )


                    }

                }



                Box(
                    Modifier
                        .padding(vertical = 5.dp, horizontal = 5.dp)
                        .shadow(5.dp)
                        .background(color = MaterialTheme.colors.surface)
                        .height(64.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Button(
                                onClick = {
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    stringResource(id = R.string.address_registration),
                                    style = Typography.titleSmall,
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Button(
                                onClick = {
                                    viewModel.navigateToLocationRegistration()
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, color = barcolorlight2)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "LocationOn",
                                    tint = barcolorlight2
                                )
                                Text(
                                    stringResource(id = R.string.map),
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                            }
                        }

                    }
                }

            }

        }
    }
}