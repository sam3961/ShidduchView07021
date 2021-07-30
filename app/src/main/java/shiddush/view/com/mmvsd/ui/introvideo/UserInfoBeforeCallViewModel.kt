package shiddush.view.com.mmvsd.ui.introvideo

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

/**
 * Created by Sumit Kumar.
 */

class UserInfoBeforeCallViewModel : ViewModel() {

    var email = ObservableField<String>()
    var password = ObservableField<String>()

    init {
        email.set("")
        password.set("")
    }


}
