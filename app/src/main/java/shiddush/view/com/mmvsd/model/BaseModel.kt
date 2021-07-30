package shiddush.view.com.mmvsd.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 * @Base Model class :  This class contain the common response of every Api.
 **/


open class BaseModel {
    @SerializedName("error")
    var error: String = ""

    @SerializedName("message")
    var message: String = ""

}