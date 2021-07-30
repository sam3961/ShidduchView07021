package shiddush.view.com.mmvsd.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Sumit Kumar.
 */
class SignUpThreeViewModel: ViewModel(){
    var isLoading = MutableLiveData<Boolean>()
    var apiError = MutableLiveData<String>()
    var apiResponse = MutableLiveData<Any>()

}
