package shiddush.view.com.mmvsd.facebook

import java.util.ArrayList
/**
 * Created by Sumit Kumar.
 */
class FacebookRequest {

    var facebook_id: String? = ""
    var email: String? = ""
    var dob: String? = ""
    var first_name: String? = ""
    var last_name: String? = ""
    var gender: String? = ""
    var profile_path: String? = ""
    var device_id: String? = ""
    var device_type: String? = ""
    var profession: String? = ""
    var position: String? = ""
    var education: String? = ""
    var college_name: String? = ""
    var about_me: String? = ""

    var userImages = ArrayList<shiddush.view.com.mmvsd.facebook.ProfileImages>()

    var profile_image: String? = ""
}