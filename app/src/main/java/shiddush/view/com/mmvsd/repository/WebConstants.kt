package shiddush.view.com.mmvsd.repository

/**
 * Created by Sumit Kumar
 * This class is to hold web services functions and base url
 */
class WebConstants {
    companion object {

        //Base URL
        //const val BASE_URL = "https://theaishadchan.com/api/live/" // Live  //beta,live
        //const val BASE_URL = "https://e372c391.ngrok.io/api/beta/" // Live  //beta,live
        // const val BASE_URL = "https://admin.shidduchview.com/api/beta/" // Live
        //    const val BASE_URL = "http://172.10.218.222:5952/api/beta/" // local
        //const val BASE_URL = "https://2d4eaaab.ngrok.io/api/beta/" // local
        //  const val BASE_URL = "http://54.71.18.74:5952/api/beta/" // staging
        //const val BASE_URL = "https://meanstack.stagingsdei.com:5952/api/beta/" // meanstack

        //Socket URL
        //const val SOCKET_URL = "http://35.182.146.136:5959"//"http://7f58ebfc.ngrok.io" // Live
//        const val SOCKET_URL = "http://5a061ab9.ngrok.io" // Live
        //const val SOCKET_URL = "http://172.10.218.222:5959" // local.
        //const val SOCKET_URL = "https://a1faaeee.ngrok.io:5959" // local
        //const val SOCKET_URL = "http://54.71.18.74:5959" // staging

        //privacy policy and terms & conditions
        const val PRIVACY_URL = "https://theaishadchan.com/privacy-policy"
        const val TERMS_URL = "https://theaishadchan.com/terms-and-condition"
        const val EULA_URL = "https://theaishadchan.com/eula"
        const val CREATE_RESUME_URL = "https://shidduchview.formstack.com/forms/life_coaching"

        // Application type
        const val DEVICE_TYPE = "Android"

        /**
         * Do not try to change below constant texts those are related to web service call
         */
        //Apis
        const val LOGIN_WS = "user/userLoginNormal"
        const val SOCIAL_LOGIN_WS = "user/userSocialLogin"
        const val FORGOT_PASS_WS = "user/forgotpassword"
        const val SIGNUP_WS = "user/normalandsocialSignUp"
        const val POST_LOGOUT_WS = "user/userLogout"
        const val APP_SUBSCRIPTION_DONE_WS = "user/subscriptionDone"
        const val APP_SUBSCRIPTION_DETAIL_WS = "user/appSubscriptionDetail"
        const val APP_SUBSCRIPTION_STATUS_WS = "user/subscription/status"
        const val UPDATE_PHONE_NO = "user/updatePhoneNo"

        const val POST_NORMAL_QUESTIONS_WS = "useranswers/addUseranswersForQuestion"
        const val POST_TRAIN_THE_AI_QUES_WS = "useranswers/addWaitingScreenQuestions"
        const val POST_BIBLE_QUESTIONS_WS = "useranswers/answerBibleQuestion"

        const val GET_NORMAL_QUESTIONS_WS = "rQuestions/getSuperImportantQuestions"
        const val GET_BIBLE_QUESTIONS_WS = "rQuestions/gettheBibleQuestions"
        const val GET_TRAIN_THE_AI_QUES_WS = "rQuestions/getWaitingScreenQuestions"
        const val GET_TIMER_WS = "matching/timerForCountDown"
        const val CALL_DROP_AFTER_TWO_MIN_WS = "matching/callDropAfterTwoMin"
        const val ARCHIVING_VIDEO_CALL_WS = "matching/archive"
        const val PICK_RANDOM_MATCH_WS = "matching/pickaRandomMatch"
        const val MATCH_WHILE_REGISTRATION_WS = "matching/matchingWhileRegisteration"
        const val MATCH_USER_DETAIL_WS = "matching/incomingCallInfo"
        const val RE_GENERATE_TOKEN_WS = "matching/regenerateToken"

        const val REPORT_USER_WS = "review/reportUser"
        const val END_CALL_WS = "review/endCall"
        const val BLOCKED_USER_WS = "review/blockUserVideoCallScreen"
        const val CHECK_FOR_REVIEW_WS = "review/checkForReview"
        const val REFFERAL_WS = "ShidduchViewRef/api/userRefer.php"
        const val ADD_REVIEW_WS = "review/addReview"

        const val USER_SAID_NO_REVIEW_WS = "chatzapier/setchoiceno.php"

        const val GET_BACKGROUND_MUSIC_WS = "bgMusic/getBackgroundMusic"
        const val POST_TRIGGER_ALARM_WS = "abusive/addWhenAbusiveHappens"
        const val GET_YOUTUBE_LINKS_WS = "youtube/getYouTubeLink"

        const val GET_QUESTIONS = "questions/get"
        const val SUBMIT_QUESTION_ANSWER = "questions/answer"

        const val UPLOAD_CHAT_IMAGE = "chat/upload"

    }
}


