package shiddush.view.com.mmvsd.ui.forgotpassword

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.model.forgotpassword.ForgotPasswordRequest
import shiddush.view.com.mmvsd.model.forgotpassword.ForgotPasswordResponse
import shiddush.view.com.mmvsd.repository.*
import shiddush.view.com.mmvsd.utill.PreferenceConnector
import shiddush.view.com.mmvsd.utill.getPrefString
import shiddush.view.com.mmvsd.utill.hideVirtualKeyboard
import shiddush.view.com.mmvsd.utill.isValidEmail

/**
 * Created by Sumit Kumar.
 * This  model class  contain all the fields of the forgot passpword api.
 */
class ForgotPasswordViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var apiError = MutableLiveData<String>()
    var apiResponse = MutableLiveData<Any>()
    var email = ObservableField<String>()

    init {
        email.set("")
        isLoading.value = false
    }

    //forgot password click
    fun onForgotPassword(view: View) {
        try {
            if (checkValidation(view)) {
                hideVirtualKeyboard(view.context)
                val request = ForgotPasswordRequest()
                request.email = email.get()
                request.deviceType = WebConstants.DEVICE_TYPE
                request.deviceToken = getPrefString(view.context, PreferenceConnector.DEVICE_TOKEN)

                if (isLoading.value == false) {
                    isLoading.value = true
                    val manager = NetworkManager()
                    manager.createApiRequest(ApiUtilities.getAPIService().forgotPassword(request), object : ServiceListener<ForgotPasswordResponse> {
                        override fun getServerResponse(response: ForgotPasswordResponse, requestcode: Int) {
                            //AppInstance.logObj = response
                            System.out.println("Login Response ==> " + response)
                            apiResponse.value = response
                            //apiError.value = response.getMessage()
                            isLoading.value = false
                        }

                        override fun getError(error: ErrorModel, requestcode: Int) {
                            apiError.value = error.error_message
                            isLoading.value = false
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * to check validations
     */
    private fun checkValidation(view: View): Boolean {
        var isValid = false
        if (email.get()!!.trim().isEmpty()) {
            isValid = false
        } else isValid = isValidEmail(email.get()!!)
        return isValid
    }
}