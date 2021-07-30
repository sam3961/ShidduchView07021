package shiddush.view.com.mmvsd.model.signupbiblequiz

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class SignUpBibleQuizGetRequest : Serializable {

    @SerializedName("userid")
    @Expose
    var userId: String? = ""
}