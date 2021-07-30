package shiddush.view.com.mmvsd.widget.loader

import android.content.Context

/**
 * Created by Sumit Kumar.
 */
class UtilLoader {
     var progressDialogObj: TransparentProgressDialog? = null
    lateinit var context: Context

    constructor(context: Context) {
        this.context = context
    }

    fun startLoader(context: Context) {
        try {
            progressDialogObj = TransparentProgressDialog(context)
            progressDialogObj?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopLoader() {
        try {
            if (progressDialogObj != null && progressDialogObj?.isShowing!!)
                progressDialogObj?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}