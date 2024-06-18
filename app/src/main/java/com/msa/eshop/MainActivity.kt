@file:OptIn(ExperimentalMaterial3Api::class)


package com.msa.eshop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.ui.navigation.SetupNavigator
import com.msa.eshop.ui.theme.EShopTheme
import com.msa.eshop.utils.map.location.PiLocationManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var navManager: NavManager
    @Inject
    lateinit var piLocationManager: PiLocationManager
    var speechInput = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        piLocationManager.setActivity(this)
        setContent {
            EShopTheme {
                    SetupNavigator()
                }

        }
    }


    fun askSpeechInput(context: Context) {
        val language = "fa-IR"
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk")
            startActivityForResult(intent, 102)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            speechInput.value = result?.get(0).toString()
        }
    }

    // Override onBackPressed to handle back button press
//    override fun onBackPressed() {
//        super.onBackPressed()
//        // Custom logic for handling back button press
//        // For example, you can navigate back in your navigation stack
//        // or perform other actions as needed
//        navManager.navigate(NavInfo(id = Route.BACK.route))
//    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EShopTheme {

    }
}