package shiddush.view.com.mmvsd.ui.signup

import android.app.DatePickerDialog
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.utill.showToast
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Sumit Kumar.
 */
class SignUpOneViewModel : ViewModel() {

    var name = ObservableField<String>()
    var phone = ObservableField<String>()
    var dob = ObservableField<String>()
    var gender = ObservableField<String>()
    var height = ObservableField<String>()
    var email = ObservableField<String>()
    var password = ObservableField<String>()
    var confirmPassword = ObservableField<String>()
    var promoCode = ObservableField<String>()

    init {
        name.set("")
        dob.set("")
        phone.set("")
        gender.set("")
        height.set("")
        email.set("")
        password.set("")
        confirmPassword.set("")
        promoCode.set("")
    }

    fun setEmailCommunicator(txt: String) {
        email.set(txt)
    }
    fun setPhoneCommunicator(txt: String) {
        phone.set(txt)
    }

    fun setNameCommunicator(txt: String) {
        name.set(txt)
    }

    fun setDOBCommunicator(txt: String) {
        dob.set(txt)
    }

    fun setGenderCommunicator(txt: String) {
        gender.set(txt)
    }
    fun setPromoCodeCommunicator(txt: String) {
        promoCode.set(txt)
    }

    fun onDOBClick(view: View) {
        val cldr = Calendar.getInstance()
        val day = cldr.get(Calendar.DAY_OF_MONTH)
        val month = cldr.get(Calendar.MONTH)
        val year = cldr.get(Calendar.YEAR)
        // date picker dialog
        var picker = DatePickerDialog(view.context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> verifyDOB(view, dayOfMonth.toString(), monthOfYear, year) }, year, month, day)
        picker.show()
    }

    private fun verifyDOB(view: View, dayOfMonth: String, monthOfYear: Int, year: Int) {
        try {
            val dateInString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = Date()
            System.out.println("Current Date : " + dateFormat.format(date))
            val date1 = dateFormat.parse(dateInString)
            System.out.println(dateFormat.format(date1!!))
            val diff = Math.abs(date.time - date1.getTime())
            val diffDays = diff / (24 * 60 * 60 * 1000)
            println("Difference$diffDays")
            if (diffDays > 18) {
                dob.set(dateInString)
                //(view.context as SignUpActivity).callFragTwo()
            } else {
                dob.set("")
                showToast(view.context, view.context.getString(R.string.not_valid_dob), Toast.LENGTH_SHORT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dob.set("")
            showToast(view.context, view.context.getString(R.string.not_valid_dob), Toast.LENGTH_SHORT)
        }
    }


}
