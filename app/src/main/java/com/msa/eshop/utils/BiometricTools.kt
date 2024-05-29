package com.msa.eshop.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.msa.eshop.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BiometricTools @Inject constructor(
    @ApplicationContext private val context: Context
) {

    //---------------------------------------------------------------------------------------------- checkDeviceHasBiometric
    private fun checkDeviceHasBiometric(biometricPrompt : BiometricPrompt) : String? {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricDialog(biometricPrompt)
                null
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                context.getString(R.string.login)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                context.getString(R.string.login)
            }

            else -> {
                context.getString(R.string.login)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- checkDeviceHasBiometric


    //---------------------------------------------------------------------------------------------- showBiometricDialog
    private fun showBiometricDialog(biometricPrompt : BiometricPrompt) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود از طریق بایومتریک")
            .setNegativeButtonText("انصراف")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
    //---------------------------------------------------------------------------------------------- showBiometricDialog

    // val fragmentActivity = LocalContext.current as FragmentActivity
    //---------------------------------------------------------------------------------------------- showBiometricDialog
    fun showBiometricDialog(
        fragment: FragmentActivity,
        onAuthenticationError: () -> Unit,
        onAuthenticationFailed: () -> Unit = {},
        onAuthenticationSucceeded: () -> Unit
    ): String? {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            fragment,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onAuthenticationError.invoke()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onAuthenticationFailed.invoke()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onAuthenticationSucceeded.invoke()
                }
            })
        return checkDeviceHasBiometric(biometricPrompt)
    }
    //---------------------------------------------------------------------------------------------- showBiometricDialog

}