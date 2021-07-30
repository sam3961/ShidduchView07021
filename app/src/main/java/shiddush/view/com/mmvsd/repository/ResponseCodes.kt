package shiddush.view.com.mmvsd.repository

/**
 * Created by Sumit Kumar.
 * @ResponseCodes class contain all the static values of responses
 */
class ResponseCodes {

    companion object {


        /** System Level Response Codes Starts Here **/

        /** The Constant Success.  */
        val Success = 200

        /** The Constant Success.  */
        val Accepted = 201

        /** The Constant Review Not Done.  */
        val ReviewNotDone = 202

        /** The Constant Account Blocked.  */
        val AccountBlocked = 204

        /** The Constant AccessToken expired.
         *
         * */

        val ACCESS_TOKEN_EXPIRED = 401

        /** The Constant RefreshToken expired.  */
        val REFRESH_TOKEN_EXPIRED = 400

        /** The Constant InvalidUseridPassword  */
        val BAD_REQUEST = 400

        /** The Constant No User Found  */
        val NoUserFound = 404

        /** The Constant Subscription expired.
         *
         * */
        val APP_SUBSCRIPTION_EXPIRED = 501

        /**
         *Internal Server Error.
         **/
        val INTERNAL_SERVER_ERROR = 500


        // Error Codes
        val REQUEST_CANCEL = -1
        val RESPONSE_JSON_NOT_VALID = 1
        val MODEL_TYPE_CAST_EXCEPTION = 2
        val INTERNET_NOT_AVAILABLE = 3
        val WRONG_URL = 4
        val WRONG_METHOD_NAME = 5
        val URL_CONNECTION_ERROR = 6
        val UNKNOWN_ERROR = 10

        val NOT_ALLOWED = 403


        fun logErrorMessage(code: Int): String {
            var errorMessage = ""

            when (code) {

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.REQUEST_CANCEL -> errorMessage = "Request Canceled"

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.INTERNET_NOT_AVAILABLE -> errorMessage = "Internet connection is not available. Please check it and try again"

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.WRONG_URL -> errorMessage = "You are trying to hit wrong url, Please check it and try again"

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.WRONG_METHOD_NAME -> errorMessage = "You are passing wrong method name. i.e POST, GET, DELETE etc"

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.URL_CONNECTION_ERROR -> errorMessage = "Connection is not established, Please try again"

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.RESPONSE_JSON_NOT_VALID ->

                    errorMessage = "Json you are getting is not valid"

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.MODEL_TYPE_CAST_EXCEPTION -> errorMessage = "Server is not working. Please try after some time."

                shiddush.view.com.mmvsd.repository.ResponseCodes.Companion.NOT_ALLOWED -> errorMessage = "Server is not working. Please try after some time."

                else -> errorMessage = "Unknown error"
            }
            return errorMessage
        }
    }
}