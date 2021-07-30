package shiddush.view.com.mmvsd.ui.review

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityReviewBinding
import shiddush.view.com.mmvsd.databinding.FragmentSignUpOneBinding

/**
 * Created by Sumit Kumar.
 */
class ReviewViewModel : ViewModel() {

    var dateAgain = MutableLiveData<Boolean>()
    var pleasantRating = MutableLiveData<Int>()
    var attractiveRating = MutableLiveData<Int>()
    var religiousRating = MutableLiveData<Int>()

}
