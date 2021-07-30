package shiddush.view.com.mmvsd.ui.videocall

import android.webkit.URLUtil
import shiddush.view.com.mmvsd.model.videocall.MatchedAnswersData

object OpenTokConfig {
    // *** Fill the following variables using your own Project info from the OpenTok dashboard  ***
    // ***                      https://dashboard.tokbox.com/projects                           ***

    //https://tokbox.com/account/#/project/46361742?selectedMetricTab=usage
    //admin@shidduchview.com / Pistons123*

    // Replace with your OpenTok API key
    var API_KEY = ""
    // Replace with a generated Session ID
    var SESSION_ID = ""
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    var TOKEN = ""

    /*                           ***** OPTIONAL *****
     If you have set up a server to provide session information replace the null value
     in CHAT_SERVER_URL with it.

     For example: "https://yoursubdomain.com"
    */
    val CHAT_SERVER_URL: String? = ""
    val SESSION_INFO_ENDPOINT = CHAT_SERVER_URL!! + "/session"


    // *** The code below is to validate this configuration file. You do not need to modify it  ***

    lateinit var webServerConfigErrorMessage: String
    lateinit var hardCodedConfigErrorMessage: String

    val isWebServerConfigUrlValid: Boolean
        get() {
            return if (CHAT_SERVER_URL == null || CHAT_SERVER_URL.isEmpty()) {
                webServerConfigErrorMessage = "SERVER_URL in OpenTokConfig.java must not be null or empty"
                false
            } else if (!(URLUtil.isHttpsUrl(CHAT_SERVER_URL) || URLUtil.isHttpUrl(CHAT_SERVER_URL))) {
                webServerConfigErrorMessage = "SERVER_URL in OpenTokConfig.java must be specified as either http or https"
                false
            } else if (!URLUtil.isValidUrl(CHAT_SERVER_URL)) {
                webServerConfigErrorMessage = "SERVER_URL in OpenTokConfig.java is not a valid URL"
                false
            } else {
                true
            }
        }

    fun areHardCodedConfigsValid(): Boolean {
        return if (API_KEY != null && !API_KEY.isEmpty()
                && SESSION_ID != null && !SESSION_ID.isEmpty()
                && TOKEN != null && !TOKEN.isEmpty()) {
            true
        } else {
            hardCodedConfigErrorMessage = "API KEY, SESSION ID and TOKEN in OpenTokConfig.java cannot be null or empty."
            false
        }
    }



    //Other User Details for video call
    var ISBLOCK = false
    var MATCHED_USER_NAME = ""
    var MATCHED_USER_AGE = ""
    var MATCHED_USER_RATING = 0
    var MATCHED_USER_DATA = ArrayList<MatchedAnswersData>()
    var MATCH_USER_ANSWER_COUNT = 0;
    var MATCH_USER_ID = ""
}
