package shiddush.view.com.mmvsd.utill
import android.content.Context
import android.content.SharedPreferences

/**
 *  Created by Sumit Kumar
 *  this class is  uses for the storage using the Shared preference class .
 *  It contain all the methods for the fetching and storing the values as per requirement
 */

class PreferenceConnector {
    companion object {
        val MODE = Context.MODE_PRIVATE
        val PREF_NAME = "VideoSpeedDating"
        val isRemember = "remember_login"

        val DEVICE_TOKEN = "device_token"
        val USER_DATA = "user_data"
        val BRANCH_DATA = "branch_data"
        val CHAT_DATA = "chat_data"
        val IS_APP_OPEN = "is_app_open"
        val IS_APP_LAUNCHED= "is_app_launched"

        val ALARM_DELAY_TIME = "alarm_delay_time"
        val LAT = "lat"
        val LNG = "lng"
        val CITY = "city"
        val COUNTRY = "country"
        val REVIEW_YES_NO = "REVIEW_YES_NO"
        val COUNTRYCODE = "countryCode"
        val IS_LOCATION_ALARM_ENABLED = "isLocationAlarmEnabled"
        val VIDEO_CALL_COUNT= "videoCallCount"
        val IS_GENDER_ALARM_ENABLED = "isGenderAlarmEnabled"
        val SHOW_SCHEDULING_SCREEN = "showSchedulingScreen"
        val SAID_YES_IN_REVIEW = "saidYesInReview"
        val SESSION_ID = "sessionId"
        val MATCH_USER_ID = "match_user_sessionId"
        val MATCH_USER_NAME= "match_user_name"
        val REVIEW_TO_USER_ID= "review_to_user_id"

        fun writeBoolean(context: Context, key: String, value: Boolean) {
            getEditor(context).putBoolean(key, value).commit()
        }

        fun readBoolean(context: Context, key: String, defValue: Boolean): Boolean {
            return getPreferences(context)!!.getBoolean(key, defValue)
        }

        fun writeInteger(context: Context, key: String, value: Int) {
            getEditor(context).putInt(key, value).commit()
        }

        fun readInteger(context: Context, key: String, defValue: Int): Int {
            return getPreferences(context)!!.getInt(key, defValue)
        }

        fun writeString(context: Context, key: String, value: String) {
            getEditor(context).putString(key, value).commit()
        }


        fun readString(context: Context, key: String, defValue: String): String? {
            return getPreferences(context)!!.getString(key, defValue)
        }


        fun writeFloat(context: Context, key: String, value: Float) {
            getEditor(context).putFloat(key, value).commit()
        }

        fun readFloat(context: Context, key: String, defValue: Float): Float {
            return getPreferences(context)!!.getFloat(key, defValue)
        }

        fun writeLong(context: Context, key: String, value: Long) {
            getEditor(context).putLong(key, value).commit()
        }

        fun readLong(context: Context, key: String, defValue: Long): Long {
            return getPreferences(context)!!.getLong(key, defValue)
        }

        fun getPreferences(context: Context?): SharedPreferences? {
            return context?.getSharedPreferences(PREF_NAME, MODE)
        }

        fun getEditor(context: Context): SharedPreferences.Editor {
            return getPreferences(context)!!.edit()
        }

        fun clearSharePreferenceKey(context: Context, key: String) {
            getPreferences(context)!!.edit().remove(key).commit()
        }
    }


}