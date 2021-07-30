package shiddush.view.com.mmvsd.utill

import shiddush.view.com.mmvsd.model.User
import shiddush.view.com.mmvsd.model.login.LoginResponse
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginResponse

/**
 * Created by Sumit Kumar.
 */
object AppInstance {

    private var appInstance: AppInstance? = null
    var logObj: LoginResponse? = null
    var sociallogObj: SocialLoginResponse? = null
    var userObj: User? = null
    var isCallCut = false

    /**
     * To initialize the appInstance Object
     *
     * @return singleton instance
     */

    fun getAppInstance(): AppInstance {
        if (appInstance == null) {
            appInstance = AppInstance()

            /**
             * The object will manage the User information
             */
            logObj = LoginResponse()
            sociallogObj = SocialLoginResponse()

        }

        return appInstance as AppInstance
    }

    private operator fun invoke(): AppInstance? {
        return null
    }

}